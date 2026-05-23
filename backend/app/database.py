"""Compatibility shim: re-export DB helpers from app.db.database.

This file preserves existing import paths (`app.database`) while the
implementation is moved to `app.db.database`.
"""

from app.db.database import *  # noqa: F401,F403
