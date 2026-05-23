import { useState } from 'react';
import { ShoppingBag, Search, User, ChevronDown } from 'lucide-react';
import { useLanguage } from '../locales/LanguageContext';
import type { CartItem } from '../types';
import './Header.css';

interface HeaderProps {
  cartItems: CartItem[];
  onCartClick: () => void;
  onShopClick: () => void;
  isShopPage?: boolean;
}

export default function Header({ cartItems, onCartClick, onShopClick, isShopPage }: HeaderProps) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [langOpen, setLangOpen] = useState(false);
  const { language, setLanguage, t } = useLanguage();
  const totalQty = cartItems.reduce((sum, item) => sum + item.quantity, 0);
  const logoUrl = new URL('/image-removebg-preview.png', import.meta.url).href;

  const NAV_LINKS = [t.nav.mobile, t.nav.internetTV, t.nav.eShop, t.nav.customerCare];

  const handleLanguageChange = (lang: 'AL' | 'EN') => {
    setLanguage(lang);
    setLangOpen(false);
  };

  return (
    <header className="header">
      <div className="header-topbar">
        <div className="header-inner">
          <div className="topbar-tabs">
            <button className="topbar-tab active">{t.header.individual}</button>
            <button className="topbar-tab">{t.header.business}</button>
            <button className="topbar-tab">{t.header.company}</button>
          </div>
          <div className="topbar-right">
            <div className="lang-selector">
              <button className="topbar-lang" onClick={() => setLangOpen(!langOpen)}>
                {language} ▾
              </button>
              {langOpen && (
                <div className="lang-dropdown">
                  <button className={`lang-option ${language === 'AL' ? 'active' : ''}`} onClick={() => handleLanguageChange('AL')}>
                    AL
                  </button>
                  <button className={`lang-option ${language === 'EN' ? 'active' : ''}`} onClick={() => handleLanguageChange('EN')}>
                    EN
                  </button>
                </div>
              )}
            </div>
            <Search size={16} />
          </div>
        </div>
      </div>

      <div className="header-main">
        <div className="header-inner">
          <button className="logo" onClick={() => setMobileOpen(false)} aria-label="Home">
            <img src={logoUrl} alt="One" className="logo-image" />
          </button>

          <nav className={`main-nav ${mobileOpen ? 'open' : ''}`}>
            {NAV_LINKS.map((label, idx) => (
              <button
                key={label}
                className={`nav-link ${idx === 2 ? 'nav-link--shop' : ''} ${isShopPage && idx === 2 ? 'active' : ''}`}
                onClick={idx === 2 ? onShopClick : undefined}
              >
                {label}
                <ChevronDown size={14} className="nav-chevron" />
              </button>
            ))}
          </nav>

          <div className="header-actions">
            <button className="icon-btn" aria-label={t.header.account}>
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
