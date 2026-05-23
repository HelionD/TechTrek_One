import { X, Plus, Minus, Trash2, ShoppingBag } from 'lucide-react';
import type { CartItem, Product } from '../types';
import './CartDrawer.css';

interface CartDrawerProps {
  items: CartItem[];
  onClose: () => void;
  onUpdateQty: (id: string, delta: number) => void;
  onRemove: (id: string) => void;
  onProductClick: (product: Product) => void;
}

export default function CartDrawer({ items, onClose, onUpdateQty, onRemove, onProductClick }: CartDrawerProps) {
  const total = items.reduce((sum, item) => {
    const price = item.product.final_price ?? item.product.price_original ?? 0;
    return sum + price * item.quantity;
  }, 0);

  return (
    <>
      <div className="cart-overlay" onClick={onClose} />
      <aside className="cart-drawer slide-up">
        <div className="cart-header">
          <div className="cart-title">
            <ShoppingBag size={20} />
            <span>Cart</span>
            {items.length > 0 && <span className="cart-count">{items.reduce((sum, item) => sum + item.quantity, 0)}</span>}
          </div>
          <button className="cart-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        {items.length === 0 ? (
          <div className="cart-empty">
            <ShoppingBag size={48} strokeWidth={1} />
            <p>Your cart is empty</p>
            <button className="cart-shop-btn" onClick={onClose}>Continue Shopping</button>
          </div>
        ) : (
          <>
            <div className="cart-items">
              {items.map(({ product, quantity }) => {
                const price = product.final_price ?? product.price_original ?? 0;
                return (
                  <div key={product.id} className="cart-item">
                    <div className="ci-image" onClick={() => { onProductClick(product); onClose(); }}>
                      {product.image_url ? (
                        <img src={product.image_url} alt={product.name} />
                      ) : (
                        <span>{product.category === 'telefona' ? '📱' : '⌚'}</span>
                      )}
                    </div>
                    <div className="ci-info">
                      <p className="ci-name" onClick={() => { onProductClick(product); onClose(); }}>{product.name}</p>
                      <p className="ci-price">{(price * quantity).toLocaleString('sq-AL')} L</p>
                      {product.discount_percentage && (
                        <p className="ci-discount">−{product.discount_percentage}% applied</p>
                      )}
                    </div>
                    <div className="ci-controls">
                      <button onClick={() => onUpdateQty(product.id, -1)}><Minus size={14} /></button>
                      <span>{quantity}</span>
                      <button onClick={() => onUpdateQty(product.id, 1)}><Plus size={14} /></button>
                      <button className="ci-remove" onClick={() => onRemove(product.id)}><Trash2 size={14} /></button>
                    </div>
                  </div>
                );
              })}
            </div>

            <div className="cart-footer">
              <div className="cart-total-row">
                <span>Total</span>
                <span className="cart-total-price">{total.toLocaleString('sq-AL')} L</span>
              </div>
              <p className="cart-note">Proceed to individual product checkout using Pre Pay or Post Pay</p>
            </div>
          </>
        )}
      </aside>
    </>
  );
}
