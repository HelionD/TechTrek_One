from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from typing import List
from app.schemas.discount import DiscountRead
from app.crud import discounts as crud_discounts
from app.database import get_db

router = APIRouter(prefix="/discounts", tags=["discounts"])


@router.get("/user/{user_id}", response_model=List[DiscountRead])
async def get_active_discounts(user_id: str, db: AsyncSession = Depends(get_db)):
    discounts = await crud_discounts.get_active_discounts_for_user(db, user_id)
    return discounts


@router.post("/user/{user_id}/regenerate")
async def regenerate_discounts_for_user(
    user_id: str, db: AsyncSession = Depends(get_db)
):
    # Trigger LLM-driven regeneration for a single user
    # Lazy import to avoid cycles
    from app.crud.users import get_user_by_id
    from app.services.discount_engine import generate_discounts_for_user

    user = await get_user_by_id(db, user_id)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    results = await generate_discounts_for_user(db, user)
    return {"generated": len(results)}
