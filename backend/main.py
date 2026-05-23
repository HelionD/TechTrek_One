from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from datetime import date

from app.config import settings
from app.routers import users, products, discounts, admin
from app.services.scheduler import start_scheduler, shutdown_scheduler
from app.database import get_session_factory
from app.crud import users as crud_users
from app.schemas.user import UserCreate
from app.llm.discount_engine import generate_discounts_for_user


async def _seed_demo_user():
    """Create a demo user + generate discounts if they don't exist."""
    try:
        factory = get_session_factory()
        async with factory() as db:
            demo = await crud_users.get_user_by_external_id(db, "demo-user-001")
            if demo is None:
                demo = await crud_users.upsert_user(
                    db,
                    UserCreate(
                        external_id="demo-user-001",
                        name="Demo",
                        surname="User",
                        email="demo@one.al",
                        phone="+355691234567",
                        plan_type="postpaid",
                        plan_name="Premium Plus",
                        subscription_start_date=date(2023, 6, 1),
                        monthly_spend_avg=4500.0,
                        data_usage_gb=25.0,
                        age_group="26-35",
                        preferred_language="sq",
                        is_student=False,
                    ),
                )
                await db.commit()
                print("Created demo user: demo-user-001")
            else:
                print("Demo user already exists")

            # Trigger discount generation for demo user
            discounts = await generate_discounts_for_user(db, demo, top_k=10)
            print(f"Generated {len(discounts)} discounts for demo user")
    except Exception as e:
        print(f"Demo user seeding skipped: {e}")


def create_app() -> FastAPI:
    app = FastAPI(title="TechTrek One API")

    app.add_middleware(
        CORSMiddleware,
        allow_origins=settings.CORS_ORIGINS,
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    app.include_router(users.router)
    app.include_router(products.router)
    app.include_router(discounts.router)
    app.include_router(admin.router)

    @app.on_event("startup")
    async def _startup():
        # Seed demo user + discounts
        await _seed_demo_user()

        # start background scheduler
        try:
            start_scheduler()
        except Exception as e:
            print("Failed to start scheduler:", repr(e))

    @app.on_event("shutdown")
    async def _shutdown():
        try:
            shutdown_scheduler()
        except Exception:
            pass

    return app


app = create_app()
