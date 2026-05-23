import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { ChevronDown } from 'lucide-react';
import Header from './components/Header';
import CartDrawer from './components/CartDrawer';
import ProductCard from './components/ProductCard';
import ProductDetailModal from './components/ProductDetailModal';
import { useLanguage } from './locales/LanguageContext';
import type { CartItem, Product } from './types';
import { fetchProducts, MOCK_PRODUCTS } from './services/api';

function App() {
  const { t } = useLanguage();

  const SORT_OPTIONS = [
    { label: t.filter.recommended, value: 'recommended' },
    { label: t.filter.priceLowToHigh, value: 'price_asc' },
    { label: t.filter.priceHighToLow, value: 'price_desc' },
    { label: t.filter.newest, value: 'newest' },
  ];

  const PRICE_RANGES = [
    { label: t.filter.allPrices, value: 'all', min: 0, max: Infinity },
    { label: t.filter.under100k, value: 'budget', min: 0, max: 100000 },
    { label: t.filter.range100_300k, value: 'mid', min: 100000, max: 300000 },
    { label: t.filter.range300_500k, value: 'premium', min: 300000, max: 500000 },
    { label: t.filter.above500k, value: 'luxury', min: 500000, max: Infinity },
  ];

  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [sortBy, setSortBy] = useState('recommended');
  const [priceRange, setPriceRange] = useState('all');
  const [filterOpen, setFilterOpen] = useState(false);
  const shopRef = useRef<HTMLDivElement | null>(null);

  // Demo user — in production, this comes from auth context
  const userId = localStorage.getItem('demo_user_id') || undefined;

  const loadProducts = useCallback(async (signal: AbortSignal) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetchProducts({
        page: 1,
        limit: 48,
        userId,
      });
      if (!signal.aborted) {
        setProducts(response.items);
      }
    } catch {
      if (!signal.aborted) {
        setError('Unable to reach backend products. Showing sample products until the API is available.');
        setProducts(MOCK_PRODUCTS);
      }
    } finally {
      if (!signal.aborted) {
        setLoading(false);
      }
    }
  }, [userId]);

  useEffect(() => {
    const controller = new AbortController();

    async function run() {
      await loadProducts(controller.signal);
    }

    run();
    return () => controller.abort();
  }, [loadProducts]);

  function handleAddToCart(product: Product) {
    setCartItems((items) => {
      const existing = items.find((item) => item.product.id === product.id);
      if (existing) {
        return items.map((item) =>
          item.product.id === product.id
            ? { ...item, quantity: item.quantity + 1 }
            : item,
        );
      }
      return [...items, { product, quantity: 1 }];
    });
  }

  function handleUpdateQty(id: string, delta: number) {
    setCartItems((items) =>
      items
        .map((item) =>
          item.product.id === id
            ? { ...item, quantity: Math.max(1, item.quantity + delta) }
            : item,
        )
        .filter((item) => item.quantity > 0),
    );
  }

  function handleRemove(id: string) {
    setCartItems((items) => items.filter((item) => item.product.id !== id));
  }

  const filteredAndSortedProducts = useMemo(() => {
    const priceRangeData = PRICE_RANGES.find((r) => r.value === priceRange) || PRICE_RANGES[0];

    let filtered = products.filter((product) => {
      const price = product.final_price ?? product.price_original ?? 0;
      return price >= priceRangeData.min && price <= priceRangeData.max;
    });

    // Apply sorting
    switch (sortBy) {
      case 'price_asc':
        filtered = [...filtered].sort((a, b) => {
          const priceA = a.final_price ?? a.price_original ?? 0;
          const priceB = b.final_price ?? b.price_original ?? 0;
          return priceA - priceB;
        });
        break;
      case 'price_desc':
        filtered = [...filtered].sort((a, b) => {
          const priceA = a.final_price ?? a.price_original ?? 0;
          const priceB = b.final_price ?? b.price_original ?? 0;
          return priceB - priceA;
        });
        break;
      case 'newest':
        filtered = [...filtered].reverse();
        break;
      default:
        // 'recommended' - keep original order
        break;
    }

    return filtered;
  }, [products, sortBy, priceRange]);

  function scrollToShop() {
    shopRef.current?.scrollIntoView({ behavior: 'smooth' });
  }

  return (
    <>
      <Header cartItems={cartItems} onCartClick={() => setIsCartOpen(true)} onShopClick={scrollToShop} isShopPage />

      <main className="page-shell">
        <section className="hero-section">
          <div className="hero-copy">
            <span className="hero-eyebrow">{t.hero.eyebrow}</span>
            <h1 className="hero-title">{t.hero.title}</h1>
            <p className="hero-description">
              {t.hero.description}
            </p>
            <div className="hero-actions">
              <button className="hero-button" onClick={scrollToShop}>
                {t.hero.browseProducts}
              </button>
              <button className="hero-button hero-button--secondary" onClick={scrollToShop}>
                {t.hero.shopNow}
              </button>
            </div>
          </div>
        </section>

        <section className="shop-section" ref={shopRef}>
          <div className="shop-header">
            <div>
              <p className="section-label">{t.shop.label}</p>
              <h2 className="section-title">{t.shop.title}</h2>
            </div>
            <div className="shop-controls">
              <div className="filter-menu">
                <button
                  className="filter-trigger"
                  onClick={() => setFilterOpen(!filterOpen)}
                >
                  <span>{t.shop.filterSort}</span>
                  <ChevronDown size={16} style={{ transform: filterOpen ? 'rotate(180deg)' : 'rotate(0deg)', transition: 'transform 0.2s' }} />
                </button>
                {filterOpen && (
                  <div className="filter-dropdown">
                    <div className="filter-section">
                      <h3 className="filter-section-title">{t.filter.sortBy}</h3>
                      {SORT_OPTIONS.map((option) => (
                        <label key={option.value} className="filter-option">
                          <input
                            type="radio"
                            name="sort"
                            value={option.value}
                            checked={sortBy === option.value}
                            onChange={(e) => {
                              setSortBy(e.target.value);
                              setFilterOpen(false);
                            }}
                          />
                          <span>{option.label}</span>
                        </label>
                      ))}
                    </div>
                    <div className="filter-divider" />
                    <div className="filter-section">
                      <h3 className="filter-section-title">{t.filter.priceRange}</h3>
                      {PRICE_RANGES.map((range) => (
                        <label key={range.value} className="filter-option">
                          <input
                            type="radio"
                            name="price"
                            value={range.value}
                            checked={priceRange === range.value}
                            onChange={(e) => setPriceRange(e.target.value)}
                          />
                          <span>{range.label}</span>
                        </label>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>

          {error && <div className="alert-banner">{error}</div>}

          {loading ? (
            <div className="loading-state">{t.status.loading}</div>
          ) : filteredAndSortedProducts.length === 0 ? (
            <div className="empty-state">
              <h3>{t.shop.noProducts}</h3>
              <p>{t.shop.tryAdjustFilters}</p>
            </div>
          ) : (
            <div className="product-grid">
              {filteredAndSortedProducts.map((product) => (
                <ProductCard key={product.id} product={product} onClick={setSelectedProduct} />
              ))}
            </div>
          )}
        </section>
      </main>

      {selectedProduct && (
        <ProductDetailModal
          product={selectedProduct}
          onClose={() => setSelectedProduct(null)}
          onAddToCart={(product) => {
            handleAddToCart(product);
            setSelectedProduct(null);
          }}
        />
      )}

      {isCartOpen && (
        <CartDrawer
          items={cartItems}
          onClose={() => setIsCartOpen(false)}
          onUpdateQty={handleUpdateQty}
          onRemove={handleRemove}
          onProductClick={(product) => {
            setSelectedProduct(product);
            setIsCartOpen(false);
          }}
        />
      )}
    </>
  );
}

export default App;
