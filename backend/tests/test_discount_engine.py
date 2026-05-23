import asyncio
from types import SimpleNamespace
from unittest.mock import patch

import app.llm.discount_engine as discount_engine_impl
import app.services.discount_engine as discount_engine


def test_generate_discounts_for_user_uses_fallback_when_ollama_is_unavailable():
    user = SimpleNamespace(
        id="user-1",
        plan_type="postpaid",
        current_device_model=None,
        current_device_year=None,
        current_device_brand=None,
        is_student=False,
        age_group=None,
    )

    products = [
        SimpleNamespace(
            id="product-1",
            name="Product 1",
            product_url="https://www.one.al/product-1",
            price_original=100.0,
        )
    ]

    async def fake_get_all_available_products(db):
        return products

    async def fake_create_or_replace_discount(
        db,
        user_id,
        product_id,
        discount_percentage,
        final_price,
        reasoning,
        llm_factors,
    ):
        return SimpleNamespace(id="discount-1")

    async def fake_call_ollama(prompt):
        raise RuntimeError("service unavailable")

    # Patch at the implementation module where the names are actually looked up
    with patch.object(
        discount_engine_impl, "get_all_available_products", fake_get_all_available_products
    ), patch.object(
        discount_engine_impl, "create_or_replace_discount", fake_create_or_replace_discount
    ), patch.object(
        discount_engine_impl, "call_ollama", fake_call_ollama
    ):
        result = asyncio.run(
            discount_engine.generate_discounts_for_user(None, user, top_k=1)
        )

    assert isinstance(result, list)
    assert len(result) == 1
    assert result[0]["discount_id"] == "discount-1"