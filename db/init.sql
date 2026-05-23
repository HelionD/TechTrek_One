-- Full DB initialization for TechTrek One
-- Runs on first startup of the PostgreSQL container.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_id VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    plan_type VARCHAR(20) NOT NULL DEFAULT 'prepaid',
    plan_name VARCHAR(100),
    subscription_start_date DATE,
    monthly_spend_avg FLOAT DEFAULT 0.0,
    data_usage_gb FLOAT DEFAULT 0.0,
    age_group VARCHAR(10),
    preferred_language VARCHAR(5) DEFAULT 'sq',
    is_student BOOLEAN DEFAULT FALSE,
    current_device_model VARCHAR(100),
    current_device_year INT,
    current_device_brand VARCHAR(50),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS ix_users_email ON users(email);
CREATE INDEX IF NOT EXISTS ix_users_external_id ON users(external_id);

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_id VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(100),
    category VARCHAR(50) NOT NULL,
    price_original FLOAT,
    image_url VARCHAR(500),
    product_url VARCHAR(500),
    description VARCHAR(1000),
    specs JSONB,
    is_available BOOLEAN DEFAULT TRUE,
    scraped_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS ix_products_category ON products(category);
CREATE INDEX IF NOT EXISTS ix_products_external_id ON products(external_id);

-- User discounts table
CREATE TABLE IF NOT EXISTS user_discounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    discount_percentage FLOAT NOT NULL,
    final_price FLOAT,
    reasoning VARCHAR(500),
    llm_factors JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    generated_at TIMESTAMPTZ DEFAULT NOW(),
    expires_at TIMESTAMPTZ,
    UNIQUE (user_id, product_id, is_active)
);
CREATE INDEX IF NOT EXISTS ix_user_discounts_user_id ON user_discounts(user_id);
CREATE INDEX IF NOT EXISTS ix_user_discounts_product_id ON user_discounts(product_id);
CREATE INDEX IF NOT EXISTS ix_user_discounts_is_active ON user_discounts(is_active);

-- Discount logs table
CREATE TABLE IF NOT EXISTS discount_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    products_processed INT NOT NULL,
    model_used VARCHAR(100),
    duration_seconds FLOAT,
    status VARCHAR(20) NOT NULL DEFAULT 'running',
    error_msg TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS ix_discount_logs_user_id ON discount_logs(user_id);

-- Scrape logs table
CREATE TABLE IF NOT EXISTS scrape_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category VARCHAR(50) NOT NULL,
    products_found INT NOT NULL,
    products_upserted INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'running',
    error_msg TEXT,
    started_at TIMESTAMPTZ DEFAULT NOW(),
    finished_at TIMESTAMPTZ
);

-- Seed products so frontend has data without scraping
-- external_ids are MD5 hashes of product_url, matching what upsert_product() generates
INSERT INTO products (external_id, name, brand, category, price_original, image_url, product_url, description, specs, is_available)
VALUES
  ('fc6b2c66fbac26e0239bb798b7807d31', 'Samsung Galaxy S26 Ultra 512GB CobViolet', 'Samsung', 'telefona', 157900, 'https://images.samsung.com/is/image/samsung/p6pcd/one-ui-7-galaxy-s26-ultra.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-s26-ultra', 'Flagship Android smartphone with 200MP camera, 6.9" Dynamic AMOLED display, and 5000mAh battery.', '{"RAM": "12GB", "Storage": "512GB", "Display": "6.9\"", "Camera": "200+12MP", "Color": "CobViolet", "Dual SIM": "Yes"}', TRUE),
  ('55931efd94a0237beaa7a4931de581a0', 'Samsung Galaxy S26 Ultra 512GB Black', 'Samsung', 'telefona', 157900, 'https://images.samsung.com/is/image/samsung/p6pcd/one-ui-7-galaxy-s26-ultra-black.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-s26-ultra-black', 'Flagship Android smartphone with 200MP camera system.', '{"RAM": "12GB", "Storage": "512GB", "Display": "6.9\"", "Color": "Black"}', TRUE),
  ('5f53351c1a554ee6db6ebf11f5d4505d', 'Samsung Galaxy S26 Ultra 256GB CobViolet', 'Samsung', 'telefona', 138900, 'https://images.samsung.com/is/image/samsung/p6pcd/one-ui-7-galaxy-s26-ultra-256.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-s26-ultra-256', 'Flagship Android smartphone, 256GB storage variant.', '{"RAM": "12GB", "Storage": "256GB", "Display": "6.9\"", "Color": "CobViolet"}', TRUE),
  ('e738532444b41b28644daf404e88b540', 'Samsung Galaxy S26 Ultra 256GB Black', 'Samsung', 'telefona', 138900, 'https://images.samsung.com/is/image/samsung/p6pcd/one-ui-7-galaxy-s26-ultra-256-black.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-s26-ultra-256-black', 'Flagship Android smartphone, 256GB Black variant.', '{"RAM": "12GB", "Storage": "256GB", "Display": "6.9\"", "Color": "Black"}', TRUE),
  ('27d64d9fe18f5f62e47c9b372ab255fe', 'Apple iPhone 16 Pro Max 256GB Natural Titanium', 'Apple', 'telefona', 165000, 'https://store.storeimages.cdn-apple.com/1/as-images.apple.com/is/iphone-16-pro-max-natural-titanium-select', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/apple-iphone-16-pro-max', 'Apple latest flagship with A18 Pro chip and titanium design.', '{"Storage": "256GB", "Display": "6.9\"", "Color": "Natural Titanium", "Chip": "A18 Pro"}', TRUE),
  ('cf72e6d8c78e0e649365fc28ae6c7262', 'Apple iPhone 16 Pro Max 512GB Natural Titanium', 'Apple', 'telefona', 189000, 'https://store.storeimages.cdn-apple.com/1/as-images.apple.com/is/iphone-16-pro-max-natural-titanium-512', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/apple-iphone-16-pro-max-512', 'Apple flagship with 512GB storage and A18 Pro chip.', '{"Storage": "512GB", "Display": "6.9\"", "Color": "Natural Titanium", "Chip": "A18 Pro"}', TRUE),
  ('b91deb76f6bd1b1a5448a506d0d9c400', 'Apple iPhone 16 Pro Max 1TB Natural Titanium', 'Apple', 'telefona', 219000, 'https://store.storeimages.cdn-apple.com/1/as-images.apple.com/is/iphone-16-pro-max-natural-titanium-1tb', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/apple-iphone-16-pro-max-1tb', 'Apple flagship with 1TB storage.', '{"Storage": "1TB", "Display": "6.9\"", "Color": "Natural Titanium", "Chip": "A18 Pro"}', TRUE),
  ('5d240d0ed7804724685899803cedfb58', 'Samsung Galaxy Z Fold 6 512GB Gray', 'Samsung', 'telefona', 249900, 'https://images.samsung.com/is/image/samsung/p6pcd/galaxy-z-fold6-gray.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-z-fold6', 'Foldable flagship with 7.6" main display.', '{"RAM": "12GB", "Storage": "512GB", "Display": "7.6\"", "Camera": "50+12MP", "Color": "Gray"}', TRUE),
  ('7e1964cdf94bff9ad13c77ea184f9b4b', 'Samsung Galaxy Z Flip 6 256GB Blue', 'Samsung', 'telefona', 129900, 'https://images.samsung.com/is/image/samsung/p6pcd/galaxy-z-flip6-blue.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-z-flip6', 'Compact foldable smartphone with Flex Mode camera.', '{"RAM": "8GB", "Storage": "256GB", "Display": "6.7\"", "Color": "Blue"}', TRUE),
  ('d0a2a6e8098ece945f18df46bc61f3b1', 'Xiaomi 14 Pro 512GB Black', 'Xiaomi', 'telefona', 89900, 'https://i01.appmifile.com/webfile/globalimg/xiaomi-14-pro-black.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/xiaomi-14-pro', 'Flagship Xiaomi with Leica optics.', '{"RAM": "12GB", "Storage": "512GB", "Display": "6.73\"", "Camera": "50+50+50MP", "Color": "Black"}', TRUE),
  ('526c558f19580b2203c8546b59576752', 'Xiaomi 14T Pro 256GB Titanium', 'Xiaomi', 'telefona', 74900, 'https://i01.appmifile.com/webfile/globalimg/xiaomi-14t-pro-titanium.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/xiaomi-14t-pro', 'Mid-range flagship with Dimensity 9300+.', '{"RAM": "12GB", "Storage": "256GB", "Display": "6.67\"", "Camera": "50+12+50MP", "Color": "Titanium"}', TRUE),
  ('5d3ddca95ae0e4f0e4c5c95f839a2e81', 'Google Pixel 9 Pro 256GB Obsidian', 'Google', 'telefona', 119900, 'https://store.google.com/product/pixel_9_pro_obsidian.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/google-pixel-9-pro', 'Google flagship with Tensor G4.', '{"RAM": "12GB", "Storage": "256GB", "Display": "6.7\"", "Camera": "48+48+48MP", "Color": "Obsidian"}', TRUE),
  ('e571933c24ba32e902983261a2197d3f', 'Google Pixel 9 Pro XL 256GB Porcelain', 'Google', 'telefona', 139900, 'https://store.google.com/product/pixel_9_pro_xl_porcelain.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/google-pixel-9-pro-xl', 'Google largest flagship.', '{"RAM": "16GB", "Storage": "256GB", "Display": "6.8\"", "Camera": "48+48+48MP", "Color": "Porcelain"}', TRUE),
  ('44692ab00527affef2d68c09d4bbca9a', 'OnePlus 12 512GB Flowy Emerald', 'OnePlus', 'telefona', 99900, 'https://image01.oneplus.net/oneplus/oneplus-12-flowy-emerald.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/oneplus-12', 'Flagship killer with Snapdragon 8 Gen 3.', '{"RAM": "16GB", "Storage": "512GB", "Display": "6.82\"", "Camera": "50+48+64MP", "Color": "Flowy Emerald"}', TRUE),
  ('374096da8137c1f5106d2f78525eb8a4', 'Apple Watch Ultra 2 49mm', 'Apple', 'wearables', 89900, 'https://store.storeimages.cdn-apple.com/1/as-images.apple.com/is/apple-watch-ultra-2-49mm.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/wearables/apple-watch-ultra-2', 'Rugged smartwatch.', '{"Display": "49mm", "Material": "Titanium", "GPS": "Yes"}', TRUE),
  ('b7491ffc72d85dee7b17594315b38423', 'Apple Watch Series 9 45mm Midnight', 'Apple', 'wearables', 59900, 'https://store.storeimages.cdn-apple.com/1/as-images.apple.com/is/apple-watch-series9-45mm-midnight.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/wearables/apple-watch-series-9', 'Advanced health tracking smartwatch.', '{"Display": "45mm", "Chip": "S9", "GPS": "Yes"}', TRUE),
  ('d06ca9bb33b4ab8e3945d669de7f82d0', 'Samsung Galaxy Watch 7 44mm', 'Samsung', 'wearables', 42900, 'https://images.samsung.com/is/image/samsung/p6pcd/galaxy-watch7-44mm.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/wearables/samsung-galaxy-watch7', 'Advanced health tracking smartwatch.', '{"Display": "44mm", "Battery": "40mAh", "OS": "Wear OS"}', TRUE),
  ('e80b7a3ac1424c1c3a82dd6602d5db37', 'Samsung Galaxy Watch 7 Ultra 47mm', 'Samsung', 'wearables', 59900, 'https://images.samsung.com/is/image/samsung/p6pcd/galaxy-watch7-ultra-47mm.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/wearables/samsung-galaxy-watch7-ultra', 'Premium rugged smartwatch.', '{"Display": "47mm", "Material": "Titanium", "GPS": "Yes"}', TRUE),
  ('37727d0ce5da82832f0560f0c1fc375f', 'Apple AirPods Pro 2nd Gen', 'Apple', 'wearables', 28900, 'https://store.storeimages.cdn-apple.com/1/as-images.apple.com/is/airpods-pro-2nd-gen.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/wearables/apple-airpods-pro-2', 'Premium ANC earbuds.', '{"ANC": "Yes", "Battery": "6hr (30hr case)", "Chip": "H2"}', TRUE),
  ('6de72e8f66d420a186358ccfbf87f1c9', 'Samsung Galaxy Buds3 Pro', 'Samsung', 'wearables', 22900, 'https://images.samsung.com/is/image/samsung/p6pcd/galaxy-buds3-pro.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/wearables/samsung-galaxy-buds3-pro', 'Premium earbuds with adaptive ANC.', '{"ANC": "Yes", "Battery": "6hr (26hr case)", "Driver": "2-way"}', TRUE),
  ('6281634577132373d237d4b3fe04af35', 'Xiaomi Watch S3 46mm', 'Xiaomi', 'wearables', 15900, 'https://i01.appmifile.com/webfile/globalimg/xiaomi-watch-s3.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/wearables/xiaomi-watch-s3', 'Stylish smartwatch with AMOLED display.', '{"Display": "46mm", "Battery": "14 days", "GPS": "Yes"}', TRUE),
  ('3260223988f210528ecd75d298e48df5', 'Huawei Watch GT 4 46mm', 'Huawei', 'wearables', 25900, 'https://consumer.huawei.com/content/dam/huawei/watch-gt4-46mm.jpg', 'https://www.one.al/sq/individi/e-shop/e_shop/wearables/huawei-watch-gt-4', 'Elegant smartwatch with 14-day battery.', '{"Display": "46mm", "Battery": "14 days", "GPS": "Yes"}', TRUE)
ON CONFLICT (external_id) DO NOTHING;
