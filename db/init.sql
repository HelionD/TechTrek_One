-- Full DB initialization for TechTrek One
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ENUM types — values match Python enum KEY names
DO $$ BEGIN
  CREATE TYPE plantype AS ENUM ('prepaid', 'postpaid');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;
DO $$ BEGIN
  CREATE TYPE agegroup AS ENUM ('18-25', '26-35', '36-50', '50+');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_id VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    plan_type plantype NOT NULL DEFAULT 'prepaid',
    plan_name VARCHAR(100),
    subscription_start_date DATE,
    monthly_spend_avg FLOAT DEFAULT 0.0,
    data_usage_gb FLOAT DEFAULT 0.0,
    age_group agegroup,
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
  ('fc6b2c66fbac26e0239bb798b7807d31', 'Samsung Galaxy S26 Ultra 512GB CobViolet', 'Samsung', 'telefona', 157900, 'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-s26-ultra', 'Flagship Android smartphone with 200MP camera, 6.9" Dynamic AMOLED display, and 5000mAh battery.', '{"RAM": "12GB", "Storage": "512GB", "Display": "6.9\"", "Camera": "200+12MP", "Color": "CobViolet", "Dual SIM": "Yes"}', TRUE),
  ('55931efd94a0237beaa7a4931de581a0', 'Samsung Galaxy S26 Ultra 512GB Black', 'Samsung', 'telefona', 157900, 'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-s26-ultra-black', 'Flagship Android smartphone with 200MP camera system.', '{"RAM": "12GB", "Storage": "512GB", "Display": "6.9\"", "Color": "Black"}', TRUE),


  ('5f53351c1a554ee6db6ebf11f5d4505d', 'Samsung Galaxy S26 Ultra 256GB CobViolet', 'Samsung', 'telefona', 138900, 'https://images.unsplash.com/photo-1589492477829-5b89351aff91?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-s26-ultra-256', 'Flagship Android smartphone, 256GB storage variant.', '{"RAM": "12GB", "Storage": "256GB", "Display": "6.9\"", "Color": "CobViolet"}', TRUE),
  ('e738532444b41b28644daf404e88b540', 'Samsung Galaxy S26 Ultra 256GB Black', 'Samsung', 'telefona', 138900, 'https://images.unsplash.com/photo-1589492477829-5b89351aff91?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-s26-ultra-256-black', 'Flagship Android smartphone, 256GB Black variant.', '{"RAM": "12GB", "Storage": "256GB", "Display": "6.9\"", "Color": "Black"}', TRUE),

  ('27d64d9fe18f5f62e47c9b372ab255fe', 'Apple iPhone 16 Pro Max 256GB Natural Titanium', 'Apple', 'telefona', 165000, 'https://images.unsplash.com/photo-1591779051696-e8e82be70582?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/apple-iphone-16-pro-max', 'Apple latest flagship with A18 Pro chip and titanium design.', '{"Storage": "256GB", "Display": "6.9\"", "Color": "Natural Titanium", "Chip": "A18 Pro"}', TRUE),
  ('5d240d0ed7804724685899803cedfb58', 'Samsung Galaxy Z Fold 6 512GB Gray', 'Samsung', 'telefona', 249900, 'https://images.unsplash.com/photo-1570129477492-7f2609a8a07a?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-z-fold6', 'Foldable flagship with 7.6" main display.', '{"RAM": "12GB", "Storage": "512GB", "Display": "7.6\"", "Camera": "50+12MP", "Color": "Gray"}', TRUE),
  ('7e1964cdf94bff9ad13c77ea184f9b4b', 'Samsung Galaxy Z Flip 6 256GB Blue', 'Samsung', 'telefona', 129900, 'https://images.unsplash.com/photo-1570129477492-7f2609a8a07a?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/samsung-galaxy-z-flip6', 'Compact foldable smartphone with Flex Mode camera.', '{"RAM": "8GB", "Storage": "256GB", "Display": "6.7\"", "Color": "Blue"}', TRUE),
  ('d0a2a6e8098ece945f18df46bc61f3b1', 'Xiaomi 14 Pro 512GB Black', 'Xiaomi', 'telefona', 89900, 'https://images.unsplash.com/photo-1586953208448-8e5f35f7d7cb?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/xiaomi-14-pro', 'Flagship Xiaomi with Leica optics.', '{"RAM": "12GB", "Storage": "512GB", "Display": "6.73\"", "Camera": "50+50+50MP", "Color": "Black"}', TRUE),
  ('526c558f19580b2203c8546b59576752', 'Xiaomi 14T Pro 256GB Titanium', 'Xiaomi', 'telefona', 74900, 'https://images.unsplash.com/photo-1586953208448-8e5f35f7d7cb?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/xiaomi-14t-pro', 'Mid-range flagship with Dimensity 9300+.', '{"RAM": "12GB", "Storage": "256GB", "Display": "6.67\"", "Camera": "50+12+50MP", "Color": "Titanium"}', TRUE),


  ('5d3ddca95ae0e4f0e4c5c95f839a2e81', 'Google Pixel 9 Pro 256GB Obsidian', 'Google', 'telefona', 119900, 'https://images.unsplash.com/photo-1589492477829-354056aaf998?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/google-pixel-9-pro', 'Google flagship with Tensor G4.', '{"RAM": "12GB", "Storage": "256GB", "Display": "6.7\"", "Camera": "48+48+48MP", "Color": "Obsidian"}', TRUE),
  ('e571933c24ba32e902983261a2197d3f', 'Google Pixel 9 Pro XL 256GB Porcelain', 'Google', 'telefona', 139900, 'https://images.unsplash.com/photo-1589492477829-354056aaf998?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/google-pixel-9-pro-xl', 'Google largest flagship.', '{"RAM": "16GB", "Storage": "256GB", "Display": "6.8\"", "Camera": "48+48+48MP", "Color": "Porcelain"}', TRUE),

  ('44692ab00527affef2d68c09d4bbca9a', 'OnePlus 12 512GB Flowy Emerald', 'OnePlus', 'telefona', 99900, 'https://images.unsplash.com/photo-1591779051696-6b58648ae8a8?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/telefona/oneplus-12', 'Flagship killer with Snapdragon 8 Gen 3.', '{"RAM": "16GB", "Storage": "512GB", "Display": "6.82\"", "Camera": "50+48+64MP", "Color": "Flowy Emerald"}', TRUE),
  ('374096da8137c1f5106d2f78525eb8a4', 'Apple Watch Ultra 2 49mm', 'Apple', 'wearables', 89900, 'https://images.unsplash.com/photo-1567581935884-af0de0ae72a0?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/wearables/apple-watch-ultra-2', 'Rugged smartwatch.', '{"Display": "49mm", "Material": "Titanium", "GPS": "Yes"}', TRUE),
  ('37727d0ce5da82832f0560f0c1fc375f', 'Apple AirPods Pro 2nd Gen', 'Apple', 'wearables', 28900, 'https://images.unsplash.com/photo-1505156868547-6a9cec5e2e8c?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/wearables/apple-airpods-pro-2', 'Premium ANC earbuds.', '{"ANC": "Yes", "Battery": "6hr (30hr case)", "Chip": "H2"}', TRUE),
  ('6de72e8f66d420a186358ccfbf87f1c9', 'Samsung Galaxy Buds3 Pro', 'Samsung', 'wearables', 22900, 'https://images.unsplash.com/photo-1606220588913-b3aacb4d2f46?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/wearables/samsung-galaxy-buds3-pro', 'Premium earbuds with adaptive ANC.', '{"ANC": "Yes", "Battery": "6hr (26hr case)", "Driver": "2-way"}', TRUE),
  ('6281634577132373d237d4b3fe04af35', 'Xiaomi Watch S3 46mm', 'Xiaomi', 'wearables', 15900, 'https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/wearables/xiaomi-watch-s3', 'Stylish smartwatch with AMOLED display.', '{"Display": "46mm", "Battery": "14 days", "GPS": "Yes"}', TRUE),
  ('3260223988f210528ecd75d298e48df5', 'Huawei Watch GT 4 46mm', 'Huawei', 'wearables', 25900, 'https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=400&h=400&fit=crop', 'https://www.one.al/sq/individi/e-shop/e_shop/wearables/huawei-watch-gt-4', 'Elegant smartwatch with 14-day battery.', '{"Display": "46mm", "Battery": "14 days", "GPS": "Yes"}', TRUE)
ON CONFLICT (external_id) DO NOTHING;
