import { useState } from 'react';
import { useLanguage } from '../locales/LanguageContext';
import type { Product } from '../types';
import './ProductCard.css';

interface ProductCardProps {
  product: Product;
  onClick: (product: Product) => void;
}

function PhoneIcon() {
  return (
    <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
      <rect x="5" y="2" width="14" height="20" rx="2" />
      <circle cx="12" cy="17" r="1" />
    </svg>
  );
}

function WatchIcon() {
  return (
    <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
      <rect x="6" y="6" width="12" height="12" rx="6" />
      <path d="M9 3h6l1 3H8L9 3z" />
      <path d="M9 21h6l1-3H8l1 3z" />
    </svg>
  );
}

export default function ProductCard({ product, onClick }: ProductCardProps) {
  const { t } = useLanguage();
  const displayPrice = product.final_price ?? product.price_original;
  const hasDiscount = !!product.discount_percentage && product.discount_percentage > 0;
  const [imgFailed, setImgFailed] = useState(false);
  const showImage = !!product.image_url && !imgFailed;

  return (
    <article className="product-card fade-in" onClick={() => onClick(product)}>
      {hasDiscount && <div className="product-badge">−{product.discount_percentage}%</div>}

      <div className="product-image-area">
        {showImage && product.image_url ? (
          <img
            src={product.image_url}
            alt={product.name}
            className="product-image"
            loading="lazy"
            onError={() => setImgFailed(true)}
          />
        ) : (
          <div className="product-placeholder">
            {product.category === 'telefona' ? <PhoneIcon /> : <WatchIcon />}
          </div>
        )}
      </div>

      <div className="product-info">
        <h3 className="product-name">{product.name}</h3>
        {product.brand && <p className="product-brand">{product.brand}</p>}

        <div className="product-pricing">
          {displayPrice != null && (
            <span className="product-price">{displayPrice.toLocaleString('sq-AL')} L</span>
          )}
          {hasDiscount && product.price_original != null && (
            <span className="product-original-price">{product.price_original.toLocaleString('sq-AL')} L</span>
          )}
        </div>

        {product.reasoning && <p className="product-ai-hint">{t.product.personalised}</p>}

        <button className="product-cta">{t.product.viewDetails}</button>
      </div>
    </article>
  );
}
