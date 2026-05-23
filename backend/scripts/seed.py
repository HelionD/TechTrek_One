import asyncio
import random
import uuid
from faker import Faker

from app.database import AsyncSessionLocal
from app.crud.users import upsert_user
from app.schemas.user import UserCreate
from app.models.user import PlanType

fake = Faker()


async def seed(n: int = 50):
    async with AsyncSessionLocal() as db:
        for _ in range(n):
            plan = random.choice([PlanType.prepaid, PlanType.postpaid])
            u = UserCreate(
                external_id=str(uuid.uuid4()),
                name=fake.first_name(),
                surname=fake.last_name(),
                email=fake.unique.email(),
                phone=fake.phone_number(),
                plan_type=plan,
                plan_name=("Post 100" if plan == PlanType.postpaid else "Pre 5"),
                monthly_spend_avg=round(random.uniform(5, 150), 2),
                data_usage_gb=round(random.uniform(0.1, 50.0), 2),
                is_student=random.choice([True, False]),
            )
            await upsert_user(db, u)


if __name__ == "__main__":
    asyncio.run(seed(50))
