import uuid
from datetime import date, datetime
from pydantic import BaseModel, EmailStr
from typing import Optional
from app.models.user import PlanType, AgeGroup


class DeviceContext(BaseModel):
    phone_model: Optional[str] = None
    phone_year: Optional[int] = None
    phone_brand: Optional[str] = None
    source: str = "web"  # "web" | "mobile" | "app"


class UserBase(BaseModel):
    external_id: str
    name: str
    surname: str
    email: str
    phone: Optional[str] = None
    plan_type: PlanType = PlanType.prepaid
    plan_name: Optional[str] = None
    subscription_start_date: Optional[date] = None
    monthly_spend_avg: Optional[float] = 0.0
    data_usage_gb: Optional[float] = 0.0
    age_group: Optional[AgeGroup] = None
    preferred_language: str = "sq"


class UserCreate(UserBase):
    pass


class UserUpdate(BaseModel):
    plan_type: Optional[PlanType] = None
    plan_name: Optional[str] = None
    monthly_spend_avg: Optional[float] = None
    data_usage_gb: Optional[float] = None
    age_group: Optional[AgeGroup] = None
    preferred_language: Optional[str] = None
    current_device_model: Optional[str] = None
    current_device_year: Optional[int] = None
    current_device_brand: Optional[str] = None


class UserRead(UserBase):
    id: uuid.UUID
    current_device_model: Optional[str] = None
    current_device_year: Optional[int] = None
    current_device_brand: Optional[str] = None
    created_at: datetime
    updated_at: datetime

    # computed
    @property
    def loyalty_months(self) -> int:
        if not self.subscription_start_date:
            return 0
        delta = date.today() - self.subscription_start_date
        return delta.days // 30

    model_config = {"from_attributes": True}
