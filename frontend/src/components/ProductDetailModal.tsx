import { useState } from 'react';
import { X, ArrowLeft, ShoppingBag, Heart, Package, Smartphone, Watch } from 'lucide-react';
import type { Product, PaymentMethod } from '../types';
import PaymentModal from './PaymentModal';
import './ProductDetailModal.css';

interface ProductDetailModalProps {
  product: Product;
  onClose: () => void;
  onAddToCart: (product: Product) => void;
}

export default function ProductDetailModal({ product, onClose, onAddToCart }: ProductDetailModalProps) {
  const [payMethod, setPayMethod] = useState<PaymentMethod | null>(null);
  const [addedToCart, setAddedToCart] = useState(false);

  const displayPrice = product.final_price ?? product.price_original;
  const hasDiscount = !!product.discount_percentage && product.discount_percentage > 0;
  const savings = product.price_original && product.final_price
    ? product.price_original - product.final_price
    : 0;

  function handleAddToCart() {
    onAddToCart(product);
    setAddedToCart(true);
    setTimeout(() => setAddedToCart(false), 2000);
  }

  return (
    <>
      <div className="modal-backdrop pdm-backdrop" onClick={(event) => { if (event.target === event.currentTarget) onClose(); }}>
        <div className="pdm-sheet scale-in">
          <div className="pdm-topbar">
            <button className="pdm-back" onClick={onClose}>
              <ArrowLeft size={18} /> Back
            </button>
            <div className="pdm-topbar-logo">
              <span className="pdm-logo-box">1</span>
              <span>One Shop</span>
            </div>
            <div style={{ width: 80 }} />
          </div>

          <div className="pdm-body">
            <div className="pdm-image-col">
              <div className="pdm-image-wrap">
                {hasDiscount && <div className="pdm-badge">−{product.discount_percentage}%</div>}
                {product.image_url ? (
                  <img src={product.image_url} alt={product.name} className="pdm-image" />
                ) : (
                  <div className="pdm-placeholder">
                    {product.category === 'telefona'
                      ? <Smartphone size={80} strokeWidth={1} />
                      : <Watch size={80} strokeWidth={1} />
                    }
                  </div>
                )}
              </div>

              <div className="pdm-thumbs">
                {[0, 1, 2, 3, 4].map((i) => (
                  <div key={i} className={`pdm-thumb ${i === 0 ? 'active' : ''}`} />
                ))}
              </div>
            </div>

            <div className="pdm-info-col">
              {product.brand && <p className="pdm-brand">{product.brand}</p>}
              <h1 className="pdm-name">{product.name}</h1>

              <div className="pdm-price-row">
                <span className="pdm-price">
                  {displayPrice != null ? `${displayPrice.toLocaleString('sq-AL')} L` : 'N/A'}
                </span>
                {hasDiscount && product.price_original && (
                  <span className="pdm-original">{product.price_original.toLocaleString('sq-AL')} L</span>
                )}
              </div>

              {savings > 0 && (
                <div className="pdm-saving">
                  You save {savings.toLocaleString('sq-AL')} L
                </div>
              )}

              {product.reasoning && (
                <div className="pdm-reasoning">
                  <span>✨</span>
                  <p>{product.reasoning}</p>
                </div>
              )}

              {product.specs && Object.keys(product.specs).length > 0 && (
                <div className="pdm-specs-section">
                  <h3 className="pdm-specs-title">Details</h3>
                  <div className="pdm-specs-grid">
                    {Object.entries(product.specs).map(([key, value]) => (
                      <div key={key} className="pdm-spec-row">
                        <span className="pdm-spec-key">{key}</span>
                        <span className="pdm-spec-val">{value}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {product.description && <p className="pdm-description">{product.description}</p>}

              <div className="pdm-actions">
                <button className={`pdm-btn pdm-btn--cart ${addedToCart ? 'added' : ''}`} onClick={handleAddToCart}>
                  <ShoppingBag size={16} />
                  {addedToCart ? '✓ Added!' : 'Add to Cart'}
                </button>
                <button className="pdm-btn pdm-btn--wish">
                  <Heart size={16} /> Wishlist
                </button>
              </div>

              <div className="pdm-pay-section">
                <p className="pdm-pay-label">Buy Now</p>
                <div className="pdm-pay-btns">
                  <button className="pdm-pay-btn pdm-pay-btn--pre" onClick={() => setPayMethod('prepaid')}>
                    💳 Pre Pay
                  </button>
                  <button className="pdm-pay-btn pdm-pay-btn--post" onClick={() => setPayMethod('postpaid')}>
                    📅 Post Pay
                  </button>
                </div>
              </div>

              <div className="pdm-shipping">
                <Package size={14} /> Free delivery for One Albania customers
              </div>
            </div>
          </div>

          <button className="pdm-close-fab" onClick={onClose} aria-label="Close">
            <X size={20} />
          </button>
        </div>
      </div>

      {payMethod && (
        <PaymentModal
          product={product}
          method={payMethod}
          onClose={() => setPayMethod(null)}
          onSuccess={() => { setPayMethod(null); onClose(); }}
        />
      )}
    </>
  );
}
