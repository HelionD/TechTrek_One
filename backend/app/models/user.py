import uuid
from datetime import datetime, date
from sqlalchemy import String, Float, Boolean, Date, DateTime, Enum as SAEnum, func
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.database import Base
import enum


class PlanType(str, enum.Enum):
    prepaid = "prepaid"
    postpaid = "postpaid"


class AgeGroup(str, enum.Enum):
    young = "18-25"
    adult = "26-35"
    middle = "36-50"
    senior = "50+"


class User(Base):
    __tablename__ = "users"

    id: Mapped[uuid.UUID] = mapped_column(primary_key=True, default=uuid.uuid4)
    external_id: Mapped[str] = mapped_column(
        String(100), unique=True, nullable=False, index=True
    )
    name: Mapped[str] = mapped_column(String(100), nullable=False)
    surname: Mapped[str] = mapped_column(String(100), nullable=False)
    email: Mapped[str] = mapped_column(
        String(255), unique=True, nullable=False, index=True
    )
    phone: Mapped[str] = mapped_column(String(20), nullable=True)

    # One Albania plan info
    plan_type: Mapped[PlanType] = mapped_column(
        SAEnum(PlanType), nullable=False, default=PlanType.prepaid
    )
    plan_name: Mapped[str] = mapped_column(String(100), nullable=True)
    subscription_start_date: Mapped[date] = mapped_column(Date, nullable=True)
    monthly_spend_avg: Mapped[float] = mapped_column(Float, nullable=True, default=0.0)
    data_usage_gb: Mapped[float] = mapped_column(Float, nullable=True, default=0.0)
    age_group: Mapped[AgeGroup] = mapped_column(SAEnum(AgeGroup), nullable=True)
    preferred_language: Mapped[str] = mapped_column(String(5), default="sq")

    # Student flag — enables extra student-only discounts
    is_student: Mapped[bool] = mapped_column(Boolean, default=False)

    # Device info — updated by mobile app on each session
    current_device_model: Mapped[str] = mapped_column(String(100), nullable=True)
    current_device_year: Mapped[int] = mapped_column(nullable=True)
    current_device_brand: Mapped[str] = mapped_column(String(50), nullable=True)

    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), server_default=func.now()
    )
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), server_default=func.now(), onupdate=func.now()
    )

    # Relationships
    discounts: Mapped[list["UserDiscount"]] = relationship(
        back_populates="user", cascade="all, delete-orphan"
    )
    discount_logs: Mapped[list["DiscountLog"]] = relationship(
        back_populates="user", cascade="all, delete-orphan"
    )
