from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from typing import Optional
from app.schemas.product import ProductListResponse, ProductWithDiscount
from app.crud import products as crud_products
from app.database import get_db
from app.models.product import ProductCategory

router = APIRouter(prefix="/products", tags=["products"])


@router.get("/", response_model=ProductListResponse)
async def list_products(
    category: Optional[ProductCategory] = None,
    page: int = 1,
    limit: int = 20,
    db: AsyncSession = Depends(get_db),
):
    items, total = await crud_products.get_products(
        db, category=category, page=page, limit=limit
    )
    # map to ProductWithDiscount (no discounts attached here)
    resp_items = [ProductWithDiscount.model_validate(p) for p in items]
    return {
        "total": total,
        "page": page,
        "limit": limit,
        "items": resp_items,
    }
