from typing import List
from urllib.parse import urljoin

from playwright.async_api import async_playwright
from sqlalchemy.ext.asyncio import AsyncSession

from app.crud.products import upsert_product, mark_unavailable
from app.models.product import ProductCategory


CATEGORY_URLS = {
    ProductCategory.telefona: "https://www.one.al/sq/individi/e-shop/e_shop/telefona",
    ProductCategory.wearables: "https://www.one.al/sq/individi/e-shop/e_shop/wearables",
}


async def _click_load_more(page):
    # Try several common selectors used for JS pagination buttons
    selectors = [
        "button.load-more",
        "button[aria-label=\"Load more\"]",
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


async def _gather_product_links(page) -> List[str]:
    # Collect anchor hrefs which look like product detail links
    anchors = await page.query_selector_all("a[href]")
    links = set()
    for a in anchors:
        try:
            href = await a.get_attribute("href")
            if not href:
                continue
            # normalize
            if href.startswith("/"):
                href = urljoin("https://www.one.al", href)
            if "one.al" not in href:
                continue
            # heuristics: product pages often contain 'produkt' or '/p/' or '/product'
            if any(x in href for x in ["/produkt", "/p/", "/product", "e-shop"]):
                links.add(href.split("#")[0])
        except Exception:
            continue
    return list(links)


async def _parse_product_page(page, url: str) -> dict:
    # Best-effort parsing: extract title, price, image, description
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
        price_selectors = [".price", ".product-price", "[itemprop='price']"]
        for sel in price_selectors:
            el = await page.query_selector(sel)
            if el:
                text = (await el.inner_text()).strip()
                if text:
                    # keep digits and dots/comma
                    cleaned = "".join(ch for ch in text if (ch.isdigit() or ch in ",."))
                    try:
                        price = float(cleaned.replace(",", ""))
                    except Exception:
                        price = None
                    break

        # image
        img = await page.query_selector("img[src]")
        if img:
            src = await img.get_attribute("src")
            if src and src.startswith("/"):
                image = urljoin("https://www.one.al", src)
            else:
                image = src

        # description
        desc_el = await page.query_selector(".description, #description, [itemprop='description']")
        if desc_el:
            description = (await desc_el.inner_text()).strip()

        # brand: try meta or specific selector
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
        await page.goto(url, wait_until="networkidle")

        # Try to click load-more until it stops working
        for _ in range(10):
            clicked = await _click_load_more(page)
            if not clicked:
                break

        links = await _gather_product_links(page)

        for link in links:
            try:
                await page.goto(link, wait_until="networkidle")
                data = await _parse_product_page(page, link)
                data["category"] = category
                product, created = await upsert_product(db, data)
                seen_external_ids.append(product.external_id)
            except Exception:
                continue

        await browser.close()

    # mark unavailable those not seen this run
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
