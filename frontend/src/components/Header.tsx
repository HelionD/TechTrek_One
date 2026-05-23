import { useState } from 'react';
import { ShoppingBag, Search, User, ChevronDown } from 'lucide-react';
import type { CartItem } from '../types';
import './Header.css';

interface HeaderProps {
  cartItems: CartItem[];
  onCartClick: () => void;
  onShopClick: () => void;
  isShopPage?: boolean;
}

const NAV_LINKS = ['Mobile', 'Internet & TV', 'E-Shop', 'Customer Care'];

export default function Header({ cartItems, onCartClick, onShopClick, isShopPage }: HeaderProps) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const totalQty = cartItems.reduce((sum, item) => sum + item.quantity, 0);

  return (
    <header className="header">
      <div className="header-topbar">
        <div className="header-inner">
          <div className="topbar-tabs">
            <button className="topbar-tab active">Individual</button>
            <button className="topbar-tab">Business</button>
            <button className="topbar-tab">Company</button>
          </div>
          <div className="topbar-right">
            <span className="topbar-lang">AL ▾</span>
            <Search size={16} />
          </div>
        </div>
      </div>

      <div className="header-main">
        <div className="header-inner">
          <button className="logo" onClick={() => setMobileOpen(false)} aria-label="Home">
            <span className="logo-box">1</span>
            <span className="logo-wordmark">one</span>
          </button>

          <nav className={`main-nav ${mobileOpen ? 'open' : ''}`}>
            {NAV_LINKS.map((label) => (
              <button
                key={label}
                className={`nav-link ${label === 'E-Shop' ? 'nav-link--shop' : ''} ${isShopPage && label === 'E-Shop' ? 'active' : ''}`}
                onClick={label === 'E-Shop' ? onShopClick : undefined}
              >
                {label}
                <ChevronDown size={14} className="nav-chevron" />
              </button>
            ))}
          </nav>

          <div className="header-actions">
            <button className="icon-btn" aria-label="Account">
              <User size={20} />
            </button>
            <button className="icon-btn cart-btn" onClick={onCartClick} aria-label="Cart">
              <ShoppingBag size={20} />
              {totalQty > 0 && <span className="cart-badge">{totalQty}</span>}
            </button>
            <button
              className="mobile-menu-btn"
              onClick={() => setMobileOpen((open) => !open)}
              aria-label="Toggle menu"
            >
              <span className={`hamburger ${mobileOpen ? 'open' : ''}`} />
            </button>
          </div>
        </div>
      </div>
    </header>
  );
}
