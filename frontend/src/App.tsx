import { useEffect, useMemo, useRef, useState } from 'react';
import Header from './components/Header';
import CartDrawer from './components/CartDrawer';
import ProductCard from './components/ProductCard';
import ProductDetailModal from './components/ProductDetailModal';
import type { CartItem, Product } from './types';
import { fetchProducts, MOCK_PRODUCTS } from './services/api';

const FILTERS = [
  { label: 'All', value: '' },
  { label: 'Phones', value: 'telefona' },
  { label: 'Wearables', value: 'wearables' },
];

function App() {
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [isCartOpen, setIsCartOpen] = useState(false);
  const shopRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const controller = new AbortController();
    loadProducts(controller.signal);
    return () => controller.abort();
  }, [selectedCategory]);

  async function loadProducts(signal: AbortSignal) {
    setLoading(true);
    setError(null);

    try {
      const response = await fetchProducts({
        category: selectedCategory || undefined,
        page: 1,
        limit: 48,
      });
      if (!signal.aborted) {
        setProducts(response.items);
      }
    } catch (err) {
      if (!signal.aborted) {
        setError('Unable to reach backend products. Showing sample products until the API is available.');
        setProducts(MOCK_PRODUCTS);
      }
    } finally {
      if (!signal.aborted) {
        setLoading(false);
      }
    }
  }

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

  const visibleProducts = useMemo(
    () => (selectedCategory ? products.filter((product) => product.category === selectedCategory) : products),
    [products, selectedCategory],
  );

  function scrollToShop() {
    setSelectedCategory('');
    shopRef.current?.scrollIntoView({ behavior: 'smooth' });
  }

  return (
    <>
      <Header cartItems={cartItems} onCartClick={() => setIsCartOpen(true)} onShopClick={scrollToShop} isShopPage />

      <main className="page-shell">
        <section className="hero-section">
          <div className="hero-copy">
            <span className="hero-eyebrow">One Albania E-Shop</span>
            <h1 className="hero-title">Discover phones and wearables with prepay or postpay checkout.</h1>
            <p className="hero-description">
              Browse the latest devices, get personalised prices, and generate an invoice instantly after checkout.
            </p>
            <div className="hero-actions">
              <button className="hero-button" onClick={scrollToShop}>
                Browse products
              </button>
              <button className="hero-button hero-button--secondary" onClick={() => { setSelectedCategory('telefona'); shopRef.current?.scrollIntoView({ behavior: 'smooth' }); }}>
                Shop phones
              </button>
            </div>
          </div>

          <div className="hero-panel">
            <div>
              <span className="panel-label">Today&apos;s best deals</span>
              <h2 className="panel-title">Make your next phone or wearable payment simple.</h2>
            </div>
            <div className="panel-stats">
              <div>
                <strong>8</strong>
                <span>Featured devices</span>
              </div>
              <div>
                <strong>2</strong>
                <span>Payment options</span>
              </div>
              <div>
                <strong>1</strong>
                <span>Instant invoice</span>
              </div>
            </div>
          </div>
        </section>

        <section className="shop-section" ref={shopRef}>
          <div className="shop-header">
            <div>
              <p className="section-label">E-Shop only</p>
              <h2 className="section-title">Select a device</h2>
            </div>
            <div className="filter-pill-group">
              {FILTERS.map((filter) => (
                <button
                  key={filter.value}
                  className={`filter-pill ${filter.value === selectedCategory ? 'active' : ''}`}
                  onClick={() => setSelectedCategory(filter.value)}
                >
                  {filter.label}
                </button>
              ))}
            </div>
          </div>

          {error && <div className="alert-banner">{error}</div>}

          {loading ? (
            <div className="loading-state">Loading products…</div>
          ) : visibleProducts.length === 0 ? (
            <div className="empty-state">
              <h3>No products found</h3>
              <p>Try another category or run the backend scraper to populate the product catalog.</p>
            </div>
          ) : (
            <div className="product-grid">
              {visibleProducts.map((product) => (
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
