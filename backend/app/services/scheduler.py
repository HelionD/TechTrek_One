import asyncio
from datetime import timedelta

from apscheduler.schedulers.asyncio import AsyncIOScheduler
from sqlalchemy.ext.asyncio import AsyncSession

from app.config import settings
from app.database import AsyncSessionLocal
from app.services.scraper import scrape_all
from app.crud.users import get_all_users
from app.services.discount_engine import generate_discounts_for_user
from app.crud.discounts import expire_old_discounts

scheduler = AsyncIOScheduler()


async def _run_scrape():
    try:
        async with AsyncSessionLocal() as db:  # type: AsyncSession
            result = await scrape_all(db)
            print(f"Scrape complete: {result}")
            return result
    except Exception as e:
        print("Scrape job failed:", repr(e))
        return None


async def _run_initial_scrape():
    """Run an initial scrape immediately on startup if DB has no products."""
    try:
        async with AsyncSessionLocal() as db:
            from sqlalchemy import select, func
            from app.models.product import Product

            count_result = await db.execute(select(func.count()).select_from(Product))
            count = count_result.scalar_one()
            if count == 0:
                print("No products found in DB. Running initial scrape...")
                await _run_scrape()
            else:
                print(f"DB already has {count} products. Skipping initial scrape.")
    except Exception as e:
        print("Initial scrape check failed:", repr(e))


async def _refresh_discounts():
    async with AsyncSessionLocal() as db:
        users = await get_all_users(db)
        for u in users:
            try:
                await generate_discounts_for_user(db, u)
            except Exception:
                continue


async def _expire_discounts():
    try:
        async with AsyncSessionLocal() as db:
            await expire_old_discounts(db)
    except Exception as e:
        print("Expire discounts job failed:", repr(e))


def start_scheduler():
    # Run an initial scrape immediately if DB is empty
    try:
        asyncio.create_task(_run_initial_scrape())
    except Exception as e:
        print("Failed to schedule initial scrape:", repr(e))

    # Periodic scrape every N hours
    scheduler.add_job(
        lambda: asyncio.create_task(_run_scrape()),
        "interval",
        hours=settings.SCRAPE_INTERVAL_HOURS,
        id="scrape_job",
    )
    # Refresh discounts every 24 hours
    scheduler.add_job(
        lambda: asyncio.create_task(_refresh_discounts()),
        "interval",
        hours=24,
        id="refresh_discounts",
    )
    # Expire discounts every 1 hour
    scheduler.add_job(
        lambda: asyncio.create_task(_expire_discounts()),
        "interval",
        hours=1,
        id="expire_discounts",
    )
    scheduler.start()


def shutdown_scheduler():
    try:
        scheduler.shutdown(wait=False)
    except Exception:
        pass
