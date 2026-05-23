from fastapi import APIRouter, Depends, Query
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import Optional
from uuid import UUID
from app.schemas.product import ProductListResponse, ProductWithDiscount
from app.crud import products as crud_products
from app.database import get_db
from app.models.product import Product, ProductCategory
from app.models.discount import UserDiscount

router = APIRouter(prefix="/products", tags=["products"])


@router.get("/", response_model=ProductListResponse)
async def list_products(
    category: Optional[ProductCategory] = None,
    page: int = 1,
    limit: int = 20,
    user_id: Optional[UUID] = Query(
        None, description="If provided, attaches active discounts for this user"
    ),
    db: AsyncSession = Depends(get_db),
):
    items, total = await crud_products.get_products(
        db, category=category, page=page, limit=limit
    )

    # If a user_id is provided, load their active discounts and attach them
    discount_map: dict[UUID, UserDiscount] = {}
    if user_id is not None:
        result = await db.execute(
            select(UserDiscount).where(
                UserDiscount.user_id == user_id,
                UserDiscount.is_active == True,
            )
        )
        for d in result.scalars().all():
            discount_map[d.product_id] = d

    resp_items = []
    for p in items:
        discount = discount_map.get(p.id)
        if discount:
            resp_items.append(
                ProductWithDiscount(
                    **{c.name: getattr(p, c.name) for c in Product.__table__.columns},
                    discount_percentage=discount.discount_percentage,
                    final_price=discount.final_price,
                    reasoning=discount.reasoning,
                    discount_expires_at=discount.expires_at,
                )
            )
        else:
            resp_items.append(ProductWithDiscount.model_validate(p))

    return {
        "total": total,
        "page": page,
        "limit": limit,
        "items": resp_items,
    }
