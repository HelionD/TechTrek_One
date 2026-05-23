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
            await scrape_all(db)
    except Exception as e:
        # log and swallow to keep scheduler running
        print("Scrape job failed:", repr(e))


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
    # Scrape every N hours
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
