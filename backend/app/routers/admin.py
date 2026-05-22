from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from app.database import get_db
from app.services.scraper import scrape_all
from app.services.scheduler import start_scheduler, shutdown_scheduler
from app.crud.discounts import expire_old_discounts

router = APIRouter(prefix="/admin", tags=["admin"])


@router.post("/scrape")
async def trigger_scrape(db: AsyncSession = Depends(get_db)):
    result = await scrape_all(db)
    return {"scraped_counts": result}


@router.post("/start-scheduler")
async def start_jobs():
    start_scheduler()
    return {"status": "started"}


@router.post("/stop-scheduler")
async def stop_jobs():
    shutdown_scheduler()
    return {"status": "stopped"}


@router.post("/expire-discounts")
async def trigger_expire(db: AsyncSession = Depends(get_db)):
    count = await expire_old_discounts(db)
    return {"expired": count}
