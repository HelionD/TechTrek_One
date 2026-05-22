from datetime import datetime, timezone
from types import SimpleNamespace

from app.services.ranking import score_products_for_user


def test_score_products_for_user_applies_brand_and_age_boosts():
    user = SimpleNamespace(
        current_device_brand="OnePlus",
        current_device_year=2019,
        plan_type="postpaid",
        monthly_spend_avg=50,
    )
    product = SimpleNamespace(
        brand="OnePlus",
        price_original=100.0,
        scraped_at=datetime.now(timezone.utc),
    )

    scored = score_products_for_user(user, [product])

    assert len(scored) == 1
    assert scored[0][0] is product
    assert scored[0][1] > 0
