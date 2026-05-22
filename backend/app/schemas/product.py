import uuid
from datetime import datetime
from pydantic import BaseModel
from typing import Optional
from app.models.product import ProductCategory


class ProductBase(BaseModel):
    name: str
    brand: Optional[str] = None
    category: ProductCategory
    price_original: Optional[float] = None
    image_url: Optional[str] = None
    product_url: Optional[str] = None
    description: Optional[str] = None
    specs: Optional[dict] = None
    is_available: bool = True


class ProductRead(ProductBase):
    id: uuid.UUID
    external_id: str
    scraped_at: Optional[datetime] = None
    created_at: datetime

    model_config = {"from_attributes": True}


class ProductWithDiscount(ProductRead):
    discount_percentage: Optional[float] = None
    final_price: Optional[float] = None
    reasoning: Optional[str] = None
    discount_expires_at: Optional[datetime] = None
    # ranking score — not exposed to client, used internally for sorting
    rank_score: Optional[float] = None

    model_config = {"from_attributes": True}


class ProductListResponse(BaseModel):
    total: int
    page: int
    limit: int
    items: list[ProductWithDiscount]
