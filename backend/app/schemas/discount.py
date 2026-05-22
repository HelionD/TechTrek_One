import uuid
from datetime import datetime
from pydantic import BaseModel
from typing import Optional
from app.schemas.product import ProductRead


class DiscountRead(BaseModel):
    id: uuid.UUID
    user_id: uuid.UUID
    product_id: uuid.UUID
    discount_percentage: float
    final_price: Optional[float] = None
    reasoning: Optional[str] = None
    llm_factors: Optional[dict] = None
    is_active: bool
    generated_at: datetime
    expires_at: Optional[datetime] = None
    product: Optional[ProductRead] = None

    model_config = {"from_attributes": True}


class DiscountGenerateRequest(BaseModel):
    user_id: uuid.UUID
    product_ids: Optional[list[uuid.UUID]] = None  # None = regenerate all
    device_context: Optional["DeviceContext"] = None


class DiscountSummary(BaseModel):
    product_id: uuid.UUID
    product_name: str
    original_price: Optional[float] = None
    discount_percentage: float
    final_price: Optional[float] = None
    reasoning: Optional[str] = None
    expires_at: Optional[datetime] = None

    model_config = {"from_attributes": True}


# avoid circular import
from app.schemas.user import DeviceContext

DiscountGenerateRequest.model_rebuild()
