import uuid
from datetime import datetime
from sqlalchemy import (
    String,
    Float,
    Boolean,
    DateTime,
    ForeignKey,
    JSON,
    func,
    UniqueConstraint,
)
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.database import Base


class UserDiscount(Base):
    __tablename__ = "user_discounts"

    __table_args__ = (
        # one active discount per user per product at a time
        UniqueConstraint(
            "user_id", "product_id", "is_active", name="uq_user_product_active"
        ),
    )

    id: Mapped[uuid.UUID] = mapped_column(primary_key=True, default=uuid.uuid4)
    user_id: Mapped[uuid.UUID] = mapped_column(
        ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True
    )
    product_id: Mapped[uuid.UUID] = mapped_column(
        ForeignKey("products.id", ondelete="CASCADE"), nullable=False, index=True
    )

    discount_percentage: Mapped[float] = mapped_column(Float, nullable=False)
    final_price: Mapped[float] = mapped_column(Float, nullable=True)
    reasoning: Mapped[str] = mapped_column(
        String(500), nullable=True
    )  # LLM explanation
    llm_factors: Mapped[dict] = mapped_column(
        JSON, nullable=True
    )  # factors sent to LLM, stored for debug

    is_active: Mapped[bool] = mapped_column(Boolean, default=True, index=True)
    generated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), server_default=func.now()
    )
    expires_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=True)

    # Relationships
    user: Mapped["User"] = relationship(back_populates="discounts")
    product: Mapped["Product"] = relationship(back_populates="discounts")
