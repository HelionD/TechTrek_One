from fastapi import APIRouter, Body, Depends, HTTPException, Header, status
from sqlalchemy.ext.asyncio import AsyncSession
from typing import Optional
from app.schemas.user import UserCreate, UserRead, UserUpdate, DeviceContext
from app.crud import users as crud_users
from app.database import get_db

router = APIRouter(prefix="/users", tags=["users"])


@router.post("/sync", response_model=UserRead)
async def sync_user(
    payload: UserCreate = Body(...),
    x_device_model: Optional[str] = Header(None),
    x_device_year: Optional[str] = Header(None),
    x_device_brand: Optional[str] = Header(None),
    db: AsyncSession = Depends(get_db),
):
    user = await crud_users.upsert_user(db, payload)
    if x_device_model or x_device_year or x_device_brand:
        year: Optional[int] = None
        if x_device_year:
            try:
                year = int(x_device_year)
            except ValueError:
                year = None
        await crud_users.update_device_info(db, user, x_device_model, year, x_device_brand)
    return user


@router.get("/{user_id}", response_model=UserRead)
async def get_user(user_id: str, db: AsyncSession = Depends(get_db)):
    user = await crud_users.get_user_by_id(db, user_id)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="User not found"
        )
    return user


@router.patch("/{user_id}", response_model=UserRead)
async def patch_user(
    user_id: str, payload: UserUpdate, db: AsyncSession = Depends(get_db)
):
    user = await crud_users.get_user_by_id(db, user_id)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="User not found"
        )
    user = await crud_users.update_user(db, user, payload)
    return user
