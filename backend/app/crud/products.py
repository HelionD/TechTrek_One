import uuid
import hashlib
from datetime import datetime, timezone
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, func
from app.models.product import Product, ProductCategory


def make_external_id(product_url: str) -> str:
    # stable unique hash from URL — deduplicates across scrape runs
    return hashlib.md5(product_url.encode()).hexdigest()


async def get_product_by_id(db: AsyncSession, product_id: uuid.UUID) -> Product | None:
    result = await db.execute(select(Product).where(Product.id == product_id))
    return result.scalar_one_or_none()


async def get_product_by_external_id(
    db: AsyncSession, external_id: str
) -> Product | None:
    result = await db.execute(select(Product).where(Product.external_id == external_id))
    return result.scalar_one_or_none()


async def get_products(
    db: AsyncSession,
    category: ProductCategory | None = None,
    page: int = 1,
    limit: int = 20,
    available_only: bool = True,
) -> tuple[list[Product], int]:
    query = select(Product)
    count_query = select(func.count()).select_from(Product)

    if category:
        query = query.where(Product.category == category)
        count_query = count_query.where(Product.category == category)
    if available_only:
        query = query.where(Product.is_available == True)
        count_query = count_query.where(Product.is_available == True)

    total_result = await db.execute(count_query)
    total = total_result.scalar_one()

    query = query.offset((page - 1) * limit).limit(limit)
    result = await db.execute(query)
    return list(result.scalars().all()), total


async def upsert_product(db: AsyncSession, data: dict) -> tuple[Product, bool]:
    # returns (product, was_created)
    external_id = make_external_id(data["product_url"])
    product = await get_product_by_external_id(db, external_id)

    if product:
        for field, value in data.items():
            setattr(product, field, value)
        product.scraped_at = datetime.now(timezone.utc)
        product.is_available = True
        was_created = False
    else:
        product = Product(
            **data,
            external_id=external_id,
            scraped_at=datetime.now(timezone.utc),
        )
        db.add(product)
        was_created = True

    await db.flush()
    await db.refresh(product)
    return product, was_created


async def mark_unavailable(
    db: AsyncSession,
    category: ProductCategory,
    seen_external_ids: list[str],
) -> int:
    # anything not seen in latest scrape → mark unavailable
    result = await db.execute(
        select(Product).where(
            Product.category == category,
            Product.external_id.not_in(seen_external_ids),
            Product.is_available == True,
        )
    )
    products = result.scalars().all()
    for p in products:
        p.is_available = False
    await db.flush()
    return len(products)


async def get_all_available_products(db: AsyncSession) -> list[Product]:
    result = await db.execute(select(Product).where(Product.is_available == True))
    return list(result.scalars().all())
