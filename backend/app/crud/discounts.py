import uuid
from datetime import datetime, timezone, timedelta
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, and_, delete
from sqlalchemy.orm import selectinload
from app.models.discount import UserDiscount
from app.models.product import Product
from app.config import settings


async def get_active_discounts_for_user(
    db: AsyncSession, user_id: uuid.UUID
) -> list[UserDiscount]:
    result = await db.execute(
        select(UserDiscount)
        .options(selectinload(UserDiscount.product))
        .where(
            and_(
                UserDiscount.user_id == user_id,
                UserDiscount.is_active == True,
            )
        )
    )
    return list(result.scalars().all())


async def get_discount_for_product(
    db: AsyncSession, user_id: uuid.UUID, product_id: uuid.UUID
) -> UserDiscount | None:
    result = await db.execute(
        select(UserDiscount).where(
            and_(
                UserDiscount.user_id == user_id,
                UserDiscount.product_id == product_id,
                UserDiscount.is_active == True,
            )
        )
    )
    return result.scalar_one_or_none()


async def create_or_replace_discount(
    db: AsyncSession,
    user_id: uuid.UUID,
    product_id: uuid.UUID,
    discount_percentage: float,
    final_price: float | None,
    reasoning: str | None,
    llm_factors: dict | None,
) -> UserDiscount:
    # remove any existing active discount for this user+product before inserting
    await db.execute(
        delete(UserDiscount).where(
            and_(
                UserDiscount.user_id == user_id,
                UserDiscount.product_id == product_id,
                UserDiscount.is_active == True,
            )
        )
    )

    expires_at = datetime.now(timezone.utc) + timedelta(
        hours=settings.DISCOUNT_EXPIRY_HOURS
    )

    discount = UserDiscount(
        user_id=user_id,
        product_id=product_id,
        discount_percentage=discount_percentage,
        final_price=final_price,
        reasoning=reasoning,
        llm_factors=llm_factors,
        is_active=True,
        expires_at=expires_at,
    )
    db.add(discount)
    await db.flush()
    await db.refresh(discount)
    return discount


async def expire_old_discounts(db: AsyncSession) -> int:
    result = await db.execute(
        select(UserDiscount).where(
            and_(
                UserDiscount.is_active == True,
                UserDiscount.expires_at < datetime.now(timezone.utc),
            )
        )
    )
    expired = result.scalars().all()
    for d in expired:
        d.is_active = False
    await db.flush()
    return len(expired)


async def get_discounts_expiring_soon(
    db: AsyncSession, hours: int = 6
) -> list[UserDiscount]:
    cutoff = datetime.now(timezone.utc) + timedelta(hours=hours)
    result = await db.execute(
        select(UserDiscount).where(
            and_(
                UserDiscount.is_active == True,
                UserDiscount.expires_at <= cutoff,
            )
        )
    )
    return list(result.scalars().all())
