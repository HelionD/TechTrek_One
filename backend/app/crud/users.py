import uuid
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.models.user import User
from app.schemas.user import UserCreate, UserUpdate


async def get_user_by_id(db: AsyncSession, user_id: uuid.UUID) -> User | None:
    result = await db.execute(select(User).where(User.id == user_id))
    return result.scalar_one_or_none()


async def get_user_by_external_id(db: AsyncSession, external_id: str) -> User | None:
    result = await db.execute(select(User).where(User.external_id == external_id))
    return result.scalar_one_or_none()


async def get_user_by_email(db: AsyncSession, email: str) -> User | None:
    result = await db.execute(select(User).where(User.email == email))
    return result.scalar_one_or_none()


async def get_all_users(db: AsyncSession) -> list[User]:
    result = await db.execute(select(User))
    return list(result.scalars().all())


async def upsert_user(db: AsyncSession, data: UserCreate) -> User:
    # used when One Albania syncs a user into our system
    user = await get_user_by_external_id(db, data.external_id)
    if user:
        for field, value in data.model_dump(exclude_unset=True).items():
            setattr(user, field, value)
    else:
        user = User(**data.model_dump())
        db.add(user)
    await db.flush()
    await db.refresh(user)
    return user


async def update_user(db: AsyncSession, user: User, data: UserUpdate) -> User:
    for field, value in data.model_dump(exclude_unset=True).items():
        setattr(user, field, value)
    await db.flush()
    await db.refresh(user)
    return user


async def update_device_info(
    db: AsyncSession,
    user: User,
    model: str | None,
    year: int | None,
    brand: str | None,
) -> User:
    if model:
        user.current_device_model = model
    if year:
        user.current_device_year = year
    if brand:
        user.current_device_brand = brand
    await db.flush()
    await db.refresh(user)
    return user
