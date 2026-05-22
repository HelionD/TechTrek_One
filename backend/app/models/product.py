import uuid
from datetime import datetime
from sqlalchemy import String, Float, Boolean, DateTime, Enum as SAEnum, JSON, func
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.database import Base
import enum


class ProductCategory(str, enum.Enum):
    telefona = "telefona"
    wearables = "wearables"


class Product(Base):
    __tablename__ = "products"

    id: Mapped[uuid.UUID] = mapped_column(primary_key=True, default=uuid.uuid4)
    # stable hash of product URL — prevents duplicates across scrape runs
    external_id: Mapped[str] = mapped_column(
        String(255), unique=True, nullable=False, index=True
    )

    name: Mapped[str] = mapped_column(String(255), nullable=False)
    brand: Mapped[str] = mapped_column(String(100), nullable=True)
    category: Mapped[ProductCategory] = mapped_column(
        SAEnum(ProductCategory), nullable=False, index=True
    )
    price_original: Mapped[float] = mapped_column(Float, nullable=True)
    image_url: Mapped[str] = mapped_column(String(500), nullable=True)
    product_url: Mapped[str] = mapped_column(String(500), nullable=True)
    description: Mapped[str] = mapped_column(String(1000), nullable=True)
    specs: Mapped[dict] = mapped_column(JSON, nullable=True)  # raw specs from scrape
    is_available: Mapped[bool] = mapped_column(Boolean, default=True)
    scraped_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=True)

    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), server_default=func.now()
    )
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), server_default=func.now(), onupdate=func.now()
    )

    # Relationships
    discounts: Mapped[list["UserDiscount"]] = relationship(
        back_populates="product", cascade="all, delete-orphan"
    )
