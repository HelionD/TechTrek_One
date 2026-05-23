from datetime import datetime, timezone, timedelta
from typing import List, Tuple

from app.models.product import Product


def score_products_for_user(
    user, products: List[Product]
) -> List[Tuple[Product, float]]:
    scored: List[Tuple[Product, float]] = []
    now = datetime.now(timezone.utc)
    for p in products:
        score = 0.0
        # Brand match
        try:
            if p.brand and getattr(user, "current_device_brand", None):
                if p.brand.lower() == user.current_device_brand.lower():
                    score += 30.0
        except Exception:
            pass

        # Device age: if user's device is older than 3 years, prefer upgrade-worthy items
        try:
            dev_year = getattr(user, "current_device_year", None)
            if dev_year and isinstance(dev_year, int):
                if dev_year <= (now.year - 3):
                    score += 10.0
        except Exception:
            pass

        # Plan type boost: postpaid users get a modest boost
        try:
            if str(getattr(user, "plan_type", None)) == "postpaid":
                score += 10.0
        except Exception:
            pass

        # Student boost: students get extra relevance for affordable devices
        try:
            if bool(getattr(user, "is_student", False)):
                score += 5.0
                # prefer cheaper items for students
                if p.price_original and p.price_original < 50000:
                    score += 10.0
        except Exception:
            pass

        # Freshness: prefer recently scraped items
        try:
            if getattr(p, "scraped_at", None):
                delta = now - p.scraped_at
                if delta <= timedelta(days=3):
                    score += 5.0
        except Exception:
            pass

        # Price sensitivity: if user's monthly spend average is low, prefer lower-priced items
        try:
            avg = getattr(user, "monthly_spend_avg", 0) or 0
            if p.price_original and avg and avg < p.price_original:
                # penalize expensive items relative to avg
                score -= min(10.0, (p.price_original - avg) / max(1.0, avg) * 2)
            else:
                score += 2.0
        except Exception:
            pass

        scored.append((p, score))

    scored.sort(key=lambda x: x[1], reverse=True)
    return scored
