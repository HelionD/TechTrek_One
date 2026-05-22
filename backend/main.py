from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.config import settings
from app.routers import users, products, discounts, admin
from app.services.scheduler import start_scheduler, shutdown_scheduler


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
        # start background scheduler
        start_scheduler()

    @app.on_event("shutdown")
    async def _shutdown():
        shutdown_scheduler()

    return app


app = create_app()
