from typing import List
from urllib.parse import urljoin

from playwright.async_api import Page, async_playwright
from sqlalchemy.ext.asyncio import AsyncSession

from app.crud.products import upsert_product, mark_unavailable
from app.models.product import ProductCategory

CATEGORY_URLS = {
    ProductCategory.telefona: "https://www.one.al/sq/individi/e-shop/e_shop/telefona",
    ProductCategory.wearables: "https://www.one.al/sq/individi/e-shop/e_shop/wearables",
}


async def _click_load_more(page):
    selectors = [
        "button.load-more",
        'button[aria-label="Load more"]',
        "button[data-load-more]",
        "button:has-text('Shfaq më shumë')",
        "button:has-text('Load more')",
    ]
    for sel in selectors:
        try:
            btn = await page.query_selector(sel)
            if btn and await btn.is_enabled():
                await btn.click()
                await page.wait_for_timeout(1000)
                return True
        except Exception:
            continue
    return False


async def _accept_cookies(page: Page) -> bool:
    selectors = [
        "button:has-text('Pranoj')",
        "button:has-text('Prano')",
        "button:has-text('Accept')",
        "button:has-text('Agree')",
    ]
    for sel in selectors:
        try:
            button = page.locator(sel)
            if await button.count() > 0:
                await button.first.click()
                await page.wait_for_timeout(2000)
                return True
        except Exception:
            continue
    return False


async def _gather_product_links(page) -> List[str]:
    anchors = await page.query_selector_all("a[href]")
    links = set()
    for a in anchors:
        try:
            href = await a.get_attribute("href")
            if not href:
                continue
            if href.startswith("/"):
                href = urljoin("https://www.one.al", href)
            if "one.al" not in href:
                continue
            if "/PV" in href or any(x in href for x in ["/produkt", "/p/", "/product"]):
                links.add(href.split("#")[0])
        except Exception:
            continue
    return list(links)


async def _parse_product_page(page, url: str) -> dict:
    name = None
    price = None
    image = None
    description = None
    brand = None

    try:
        # title
        title_selectors = ["h1", "h1.product-title", "h1[itemprop='name']"]
        for sel in title_selectors:
            el = await page.query_selector(sel)
            if el:
                text = (await el.inner_text()).strip()
                if text:
                    name = text
                    break

        # price
        price_js = await page.evaluate(
            '() => { const r = /(\\d[\\d.,]*)(?:\\s*(?:LEK|LEKË|L|€))/i;'
            ' const nodes = Array.from(document.querySelectorAll("body *"));'
            ' for (const n of nodes) { const t = n.textContent?.trim();'
            ' if (!t || t.length > 80) continue; const m = r.exec(t);'
            ' if (m) return m[0]; } return null; }'
        )
        if price_js:
            cleaned = "".join(ch for ch in price_js if ch.isdigit() or ch in ",.")
            try:
                price = float(cleaned.replace(",", ""))
            except Exception:
                price = None

        # image — try multiple selectors for the product image
        img_selectors = [
            "img[itemprop='image']",
            ".product-gallery img",
            ".product-image img",
            ".gallery-cell img",
            "main .swiper-slide img",
            "main img[src*='product']",
            "main img[src*='/is/image/']",
            "main img[src*='/content/dam/']",
            "main img[src*='cdn']",
        ]
        for sel in img_selectors:
            imgs = await page.query_selector_all(sel)
            for img in imgs:
                src = await img.get_attribute("src")
                if src:
                    if src.startswith("/"):
                        image = urljoin("https://www.one.al", src)
                    else:
                        image = src
                    break
            if image:
                break

        # fallback: largest visible image in main content area
        if not image:
            try:
                fallback_img = await page.evaluate(
                    '() => { const imgs = Array.from(document.querySelectorAll("main img[src], article img[src], .content img[src]"));'
                    ' if (imgs.length === 0) return null;'
                    ' imgs.sort((a, b) => (b.naturalWidth || 0) - (a.naturalWidth || 0));'
                    ' return imgs[0]?.getAttribute("src") || null; }'
                )
                if fallback_img:
                    if fallback_img.startswith("/"):
                        image = urljoin("https://www.one.al", fallback_img)
                    else:
                        image = fallback_img
            except Exception:
                pass

        # description
        desc_el = await page.query_selector(
            ".description, #description, [itemprop='description']"
        )
        if desc_el:
            description = (await desc_el.inner_text()).strip()

        # brand
        brand_el = await page.query_selector(".brand, [itemprop='brand']")
        if brand_el:
            brand = (await brand_el.inner_text()).strip()
    except Exception:
        pass

    return {
        "name": name or "",
        "price_original": price,
        "image_url": image,
        "product_url": url,
        "description": description,
        "brand": brand,
        "specs": {},
    }


async def scrape_category(db: AsyncSession, category: ProductCategory, url: str) -> int:
    seen_external_ids: List[str] = []
    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=True)
        page = await browser.new_page()
        await page.goto(url, wait_until="load")
        await _accept_cookies(page)
        await page.wait_for_timeout(2500)

        for _ in range(10):
            clicked = await _click_load_more(page)
            if not clicked:
                break

        links = await _gather_product_links(page)

        for link in links:
            try:
                await page.goto(link, wait_until="load")
                await _accept_cookies(page)
                await page.wait_for_timeout(1500)
                data = await _parse_product_page(page, link)
                data["category"] = category
                product, created = await upsert_product(db, data)
                seen_external_ids.append(product.external_id)
            except Exception:
                continue

        await browser.close()

    removed = await mark_unavailable(db, category, seen_external_ids)
    return len(links)


async def scrape_all(db: AsyncSession) -> dict:
    results = {}
    for category, url in CATEGORY_URLS.items():
        try:
            count = await scrape_category(db, category, url)
            results[category.value] = count
        except Exception:
            results[category.value] = 0
    return results
