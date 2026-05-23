"""Seed products into the database.

Run this after the DB is initialized if you want products
without running the scraper (which requires a browser).

Usage:
    python scripts/seed_products.py
"""

import asyncio
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.models.product import Product, ProductCategory


SEED_PRODUCTS = [
    {
        "external_id": "p_seed_01",
        "name": "Samsung Galaxy S26 Ultra 512GB CobViolet",
        "brand": "Samsung",
        "category": ProductCategory.telefona,
        "price_original": 157900,
        "image_url": "https://images.samsung.com/is/image/samsung/p6pcd/one-ui-7-galaxy-s26-ultra.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-s26-ultra",
        "description": 'Flagship Android smartphone with 200MP camera, 6.9" Dynamic AMOLED display, and 5000mAh battery.',
        "specs": {"RAM": "12GB", "Storage": "512GB", "Display": '6.9"', "Camera": "200+12MP", "Color": "CobViolet", "Dual SIM": "Yes"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_02",
        "name": "Samsung Galaxy S26 Ultra 512GB Black",
        "brand": "Samsung",
        "category": ProductCategory.telefona,
        "price_original": 157900,
        "image_url": "https://images.samsung.com/is/image/samsung/p6pcd/one-ui-7-galaxy-s26-ultra-black.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-s26-ultra-black",
        "description": "Flagship Android smartphone with 200MP camera system.",
        "specs": {"RAM": "12GB", "Storage": "512GB", "Display": '6.9"', "Color": "Black"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_03",
        "name": "Samsung Galaxy S26 Ultra 256GB CobViolet",
        "brand": "Samsung",
        "category": ProductCategory.telefona,
        "price_original": 138900,
        "image_url": "https://images.samsung.com/is/image/samsung/p6pcd/one-ui-7-galaxy-s26-ultra-256.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-s26-ultra-256",
        "description": "Flagship Android smartphone, 256GB storage variant.",
        "specs": {"RAM": "12GB", "Storage": "256GB", "Display": '6.9"', "Color": "CobViolet"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_04",
        "name": "Samsung Galaxy S26 Ultra 256GB Black",
        "brand": "Samsung",
        "category": ProductCategory.telefona,
        "price_original": 138900,
        "image_url": "https://images.samsung.com/is/image/samsung/p6pcd/one-ui-7-galaxy-s26-ultra-256-black.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-s26-ultra-256-black",
        "description": "Flagship Android smartphone, 256GB Black variant.",
        "specs": {"RAM": "12GB", "Storage": "256GB", "Display": '6.9"', "Color": "Black"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_05",
        "name": "Apple iPhone 16 Pro Max 256GB Natural Titanium",
        "brand": "Apple",
        "category": ProductCategory.telefona,
        "price_original": 165000,
        "image_url": "https://store.storeimages.cdn-apple.com/1/as-images.apple.com/is/iphone-16-pro-max-natural-titanium-select",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/telefona/apple-iphone-16-pro-max",
        "description": "Apple latest flagship with A18 Pro chip and titanium design.",
        "specs": {"Storage": "256GB", "Display": '6.9"', "Color": "Natural Titanium", "Chip": "A18 Pro"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_06",
        "name": "Apple iPhone 16 Pro Max 512GB Natural Titanium",
        "brand": "Apple",
        "category": ProductCategory.telefona,
        "price_original": 189000,
        "image_url": "https://store.storeimages.cdn-apple.com/1/as-images.apple.com/is/iphone-16-pro-max-natural-titanium-512",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/telefona/apple-iphone-16-pro-max-512",
        "description": "Apple flagship with 512GB storage and A18 Pro chip.",
        "specs": {"Storage": "512GB", "Display": '6.9"', "Color": "Natural Titanium", "Chip": "A18 Pro"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_07",
        "name": "Apple iPhone 16 Pro Max 1TB Natural Titanium",
        "brand": "Apple",
        "category": ProductCategory.telefona,
        "price_original": 219000,
        "image_url": "https://store.storeimages.cdn-apple.com/1/as-images.apple.com/is/iphone-16-pro-max-natural-titanium-1tb",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/telefona/apple-iphone-16-pro-max-1tb",
        "description": "Apple flagship with 1TB storage.",
        "specs": {"Storage": "1TB", "Display": '6.9"', "Color": "Natural Titanium", "Chip": "A18 Pro"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_08",
        "name": "Samsung Galaxy Z Fold 6 512GB Gray",
        "brand": "Samsung",
        "category": ProductCategory.telefona,
        "price_original": 249900,
        "image_url": "https://images.samsung.com/is/image/samsung/p6pcd/galaxy-z-fold6-gray.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-z-fold6",
        "description": 'Foldable flagship with 7.6" main display and multitasking capabilities.',
        "specs": {"RAM": "12GB", "Storage": "512GB", "Display": '7.6"', "Camera": "50+12MP", "Color": "Gray"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_09",
        "name": "Samsung Galaxy Z Flip 6 256GB Blue",
        "brand": "Samsung",
        "category": ProductCategory.telefona,
        "price_original": 129900,
        "image_url": "https://images.samsung.com/is/image/samsung/p6pcd/galaxy-z-flip6-blue.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-z-flip6",
        "description": "Compact foldable smartphone with Flex Mode camera.",
        "specs": {"RAM": "8GB", "Storage": "256GB", "Display": '6.7"', "Color": "Blue"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_10",
        "name": "Xiaomi 14 Pro 512GB Black",
        "brand": "Xiaomi",
        "category": ProductCategory.telefona,
        "price_original": 89900,
        "image_url": "https://i01.appmifile.com/webfile/globalimg/xiaomi-14-pro-black.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/telefona/xiaomi-14-pro",
        "description": "Flagship Xiaomi with Leica optics and Snapdragon 8 Gen 3.",
        "specs": {"RAM": "12GB", "Storage": "512GB", "Display": '6.73"', "Camera": "50+50+50MP", "Color": "Black"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_11",
        "name": "Xiaomi 14T Pro 256GB Titanium",
        "brand": "Xiaomi",
        "category": ProductCategory.telefona,
        "price_original": 74900,
        "image_url": "https://i01.appmifile.com/webfile/globalimg/xiaomi-14t-pro-titanium.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/telefona/xiaomi-14t-pro",
        "description": "Mid-range flagship with Dimensity 9300+ chip.",
        "specs": {"RAM": "12GB", "Storage": "256GB", "Display": '6.67"', "Camera": "50+12+50MP", "Color": "Titanium"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_12",
        "name": "Google Pixel 9 Pro 256GB Obsidian",
        "brand": "Google",
        "category": ProductCategory.telefona,
        "price_original": 119900,
        "image_url": "https://store.google.com/product/pixel_9_pro_obsidian.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/telefona/google-pixel-9-pro",
        "description": "Google flagship with Tensor G4 and 48MP camera.",
        "specs": {"RAM": "12GB", "Storage": "256GB", "Display": '6.7"', "Camera": "48+48+48MP", "Color": "Obsidian"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_13",
        "name": "Google Pixel 9 Pro XL 256GB Porcelain",
        "brand": "Google",
        "category": ProductCategory.telefona,
        "price_original": 139900,
        "image_url": "https://store.google.com/product/pixel_9_pro_xl_porcelain.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/telefona/google-pixel-9-pro-xl",
        "description": 'Google largest flagship with 6.8" display.',
        "specs": {"RAM": "16GB", "Storage": "256GB", "Display": '6.8"', "Camera": "48+48+48MP", "Color": "Porcelain"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_14",
        "name": "OnePlus 12 512GB Flowy Emerald",
        "brand": "OnePlus",
        "category": ProductCategory.telefona,
        "price_original": 99900,
        "image_url": "https://image01.oneplus.net/oneplus/oneplus-12-flowy-emerald.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/telefona/oneplus-12",
        "description": "Flagship killer with Snapdragon 8 Gen 3 and 100W charging.",
        "specs": {"RAM": "16GB", "Storage": "512GB", "Display": '6.82"', "Camera": "50+48+64MP", "Color": "Flowy Emerald"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_15",
        "name": "Apple Watch Ultra 2 49mm",
        "brand": "Apple",
        "category": ProductCategory.wearables,
        "price_original": 89900,
        "image_url": "https://store.storeimages.cdn-apple.com/1/as-images.apple.com/is/apple-watch-ultra-2-49mm.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/wearables/apple-watch-ultra-2",
        "description": "Rugged smartwatch built for extreme environments.",
        "specs": {"Display": "49mm", "Material": "Titanium", "GPS": "Yes", "Water Resistant": "100m"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_16",
        "name": "Apple Watch Series 9 45mm Midnight",
        "brand": "Apple",
        "category": ProductCategory.wearables,
        "price_original": 59900,
        "image_url": "https://store.storeimages.cdn-apple.com/1/as-images.apple.com/is/apple-watch-series9-45mm-midnight.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/wearables/apple-watch-series-9",
        "description": "Advanced health tracking smartwatch.",
        "specs": {"Display": "45mm", "Chip": "S9", "GPS": "Yes", "Water Resistant": "50m"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_17",
        "name": "Samsung Galaxy Watch 7 44mm",
        "brand": "Samsung",
        "category": ProductCategory.wearables,
        "price_original": 42900,
        "image_url": "https://images.samsung.com/is/image/samsung/p6pcd/galaxy-watch7-44mm.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/wearables/samsung-galaxy-watch7",
        "description": "Advanced health tracking smartwatch with 3nm chip.",
        "specs": {"Display": "44mm", "Battery": "40mAh", "OS": "Wear OS", "Water Resistant": "50m"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_18",
        "name": "Samsung Galaxy Watch 7 Ultra 47mm",
        "brand": "Samsung",
        "category": ProductCategory.wearables,
        "price_original": 59900,
        "image_url": "https://images.samsung.com/is/image/samsung/p6pcd/galaxy-watch7-ultra-47mm.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/wearables/samsung-galaxy-watch7-ultra",
        "description": "Premium rugged smartwatch with titanium case.",
        "specs": {"Display": "47mm", "Material": "Titanium", "GPS": "Yes", "Battery": "590mAh"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_19",
        "name": "Apple AirPods Pro 2nd Gen",
        "brand": "Apple",
        "category": ProductCategory.wearables,
        "price_original": 28900,
        "image_url": "https://store.storeimages.cdn-apple.com/1/as-images.apple.com/is/airpods-pro-2nd-gen.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/wearables/apple-airpods-pro-2",
        "description": "Premium ANC earbuds with H2 chip and personalized spatial audio.",
        "specs": {"ANC": "Yes", "Battery": "6hr (30hr case)", "Chip": "H2", "Water": "IPX4"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_20",
        "name": "Samsung Galaxy Buds3 Pro",
        "brand": "Samsung",
        "category": ProductCategory.wearables,
        "price_original": 22900,
        "image_url": "https://images.samsung.com/is/image/samsung/p6pcd/galaxy-buds3-pro.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/wearables/samsung-galaxy-buds3-pro",
        "description": "Premium earbuds with 2-way speakers and adaptive ANC.",
        "specs": {"ANC": "Yes", "Battery": "6hr (26hr case)", "Driver": "2-way", "Water": "IP57"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_21",
        "name": "Xiaomi Watch S3 46mm",
        "brand": "Xiaomi",
        "category": ProductCategory.wearables,
        "price_original": 15900,
        "image_url": "https://i01.appmifile.com/webfile/globalimg/xiaomi-watch-s3.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/wearables/xiaomi-watch-s3",
        "description": "Stylish smartwatch with AMOLED display and 14-day battery.",
        "specs": {"Display": "46mm", "Battery": "14 days", "GPS": "Yes", "Water": "5ATM"},
        "is_available": True,
    },
    {
        "external_id": "p_seed_22",
        "name": "Huawei Watch GT 4 46mm",
        "brand": "Huawei",
        "category": ProductCategory.wearables,
        "price_original": 25900,
        "image_url": "https://consumer.huawei.com/content/dam/huawei/watch-gt4-46mm.jpg",
        "product_url": "https://www.one.al/sq/individi/e-shop/e_shop/wearables/huawei-watch-gt-4",
        "description": "Elegant smartwatch with 14-day battery and health tracking.",
        "specs": {"Display": "46mm", "Battery": "14 days", "GPS": "Yes", "Water": "5ATM"},
        "is_available": True,
    },
]


async def seed(clear_first: bool = False):
    async with AsyncSessionLocal() as db:
        if clear_first:
            from app.models.product import Product
            from sqlalchemy import delete
            await db.execute(delete(Product))
            print("Cleared existing products")

        created = 0
        for data in SEED_PRODUCTS:
            from app.crud.products import upsert_product
            _, was_created = await upsert_product(db, data)
            if was_created:
                created += 1

        print(f"Seeded {created} new products (total products may include existing)")


if __name__ == "__main__":
    import sys as _sys
    from app.database import AsyncSessionLocal

    clear = "--clear" in _sys.argv
    asyncio.run(seed(clear_first=clear))
