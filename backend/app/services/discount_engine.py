"""Compatibility shim importing LLM functions from app.llm.
This keeps existing imports working while the implementation lives under `app.llm`.
"""

from app.llm.discount_engine import call_ollama, generate_discounts_for_user

__all__ = ["call_ollama", "generate_discounts_for_user"]
