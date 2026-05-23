import json
from typing import List

import httpx
from sqlalchemy.ext.asyncio import AsyncSession

from app.config import settings
from app.crud.products import get_all_available_products
from app.crud.discounts import create_or_replace_discount
from app.models.product import Product


async def call_ollama(prompt: str) -> str:
    url = f"{settings.OLLAMA_BASE_URL}/api/generate"
    payload = {"model": settings.OLLAMA_MODEL, "prompt": prompt}
    async with httpx.AsyncClient(timeout=30.0) as client:
        resp = await client.post(url, json=payload)
        resp.raise_for_status()
        # Ollama may return streaming or JSON; prefer JSON if available
        try:
            return resp.json()
        except Exception:
            return resp.text


async def generate_discounts_for_user(
    db: AsyncSession, user, top_k: int = 10
) -> List[dict]:
    # Get candidate products
    products: List[Product] = await get_all_available_products(db)
    candidates = products[:top_k]

    # Build prompt with user factors and candidates
    user_factors = {
        "plan_type": str(getattr(user, "plan_type", None)),
        "age_group": str(getattr(user, "age_group", None)),
        "device_model": getattr(user, "current_device_model", None),
        "device_year": getattr(user, "current_device_year", None),
        "device_brand": getattr(user, "current_device_brand", None),
        "is_student": bool(getattr(user, "is_student", False)),
    }

    prompt_items = [f"User factors: {json.dumps(user_factors)}"]
    prompt_items.append(
        "\nNOTE: If the user is a student (is_student=true), apply an additional 5% discount on top of the base percentage."
    )
    prompt_items.append("\nProducts:\n")
    for p in candidates:
        prompt_items.append(
            json.dumps(
                {"name": p.name, "price": p.price_original, "url": p.product_url}
            )
        )

    prompt_items.append(
        "\nReturn a JSON array of objects with keys: 'product_url', 'discount_percentage', 'final_price', 'reasoning', 'llm_factors'. Keep values numeric where appropriate. If uncertain, return empty array."
    )

    prompt = "\n".join(prompt_items)

    # Call Ollama
    try:
        resp = await call_ollama(prompt)
        # Try to extract JSON array
        if isinstance(resp, dict):
            # If Ollama returned structured JSON under a key
            content = resp.get("text") or resp.get("output") or json.dumps(resp)
        else:
            content = resp if isinstance(resp, str) else json.dumps(resp)
        try:
            parsed = json.loads(content)
        except Exception:
            # Try to find first JSON array in the text
            import re

            m = re.search(r"\[\s*\{.*\}\s*\]", content, flags=re.S)
            if m:
                parsed = json.loads(m.group(0))
            else:
                parsed = []
    except Exception:
        parsed = []

    results = []
    is_student = bool(getattr(user, "is_student", False))
    student_bonus = 0.05 if is_student else 0.0  # extra 5% for students

    # If parsed is empty, fallback to simple rule: postpaid -> 20% else 10%, with student boost
    if not parsed:
        base_pct = (
            0.20 if str(getattr(user, "plan_type", "prepaid")) == "postpaid" else 0.10
        )
        default_pct = min(base_pct + student_bonus, 0.50)  # cap at 50%
        for p in candidates:
            pct = default_pct
            final_price = (
                p.price_original - (p.price_original * pct)
                if p.price_original
                else None
            )
            discount = await create_or_replace_discount(
                db,
                user.id,
                p.id,
                discount_percentage=round(pct * 100, 2),
                final_price=round(final_price, 2) if final_price else None,
                reasoning="fallback-rule" + ("-student" if is_student else ""),
                llm_factors={"rule": "postpaid_boost", "is_student": is_student},
            )
            results.append({"product_id": str(p.id), "discount_id": str(discount.id)})
        return results

    # Otherwise, persist results returned by LLM — apply student bonus on top
    for item in parsed:
        try:
            url = item.get("product_url")
            pct = float(item.get("discount_percentage", 0))
            # Apply student bonus
            pct = min(pct + (student_bonus * 100), 50.0)
            final_price = item.get("final_price")
            reasoning = item.get("reasoning")
            llm_factors = item.get("llm_factors")
            if llm_factors is None:
                llm_factors = {}
            llm_factors["is_student"] = is_student
            # find product by url
            prod = next(
                (
                    p
                    for p in candidates
                    if p.product_url == url or p.product_url == url.rstrip("/")
                ),
                None,
            )
            if not prod:
                continue
            discount = await create_or_replace_discount(
                db,
                user.id,
                prod.id,
                discount_percentage=pct,
                final_price=final_price,
                reasoning=reasoning,
                llm_factors=llm_factors,
            )
            results.append(
                {"product_id": str(prod.id), "discount_id": str(discount.id)}
            )
        except Exception:
            continue

    return results
