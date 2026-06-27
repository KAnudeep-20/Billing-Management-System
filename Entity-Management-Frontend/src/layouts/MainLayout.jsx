import React, { useState } from 'react';
import { Compass, Menu, X, Users, LayoutDashboard, CreditCard, TrendingUp } from 'lucide-react';
import './MainLayout.css';

export default function MainLayout({ children }) {
  const [isNavOpen, setIsNavOpen] = useState(false);

  return (
    <div className="main-layout">
      {/* App Header */}
      <header className="app-header">
        <div className="logo-section">
          <span className="logo-icon">
            <Compass size={28} strokeWidth={2.5} />
          </span>
          <span className="logo-text">Billing Management System</span>
        </div>

        <div className="header-actions">
          <button
            className="nav-toggle-btn"
            onClick={() => setIsNavOpen(true)}
            aria-label="Toggle Navigation Navigator"
            title="Open Application Navigator"
          >
            <Menu size={20} />
          </button>
        </div>
      </header>

      {/* Backdrop when navigator drawer is open */}
      {isNavOpen && (
        <div
          className="navigator-backdrop"
          onClick={() => setIsNavOpen(false)}
        />
      )}

      {/* Right Side Application Navigator */}
      <aside className={`right-navigator ${isNavOpen ? 'open' : ''}`}>
        <div className="navigator-header">
          <h3>Applications</h3>
          <button
            className="navigator-close-btn"
            onClick={() => setIsNavOpen(false)}
            aria-label="Close Navigator"
          >
            <X size={20} />
          </button>
        </div>

        <ul className="navigator-menu">
          <li className="navigator-item">
            <span className="navigator-item-icon"><LayoutDashboard size={18} /></span>
            <span>Dashboard</span>
          </li>
          <li className="navigator-item active" onClick={() => setIsNavOpen(false)}>
            <span className="navigator-item-icon"><Users size={18} /></span>
            <span>Manage Entities</span>
          </li>
          <li className="navigator-item">
            <span className="navigator-item-icon"><CreditCard size={18} /></span>
            <span>Billing & Invoices</span>
          </li>
          <li className="navigator-item">
            <span className="navigator-item-icon"><TrendingUp size={18} /></span>
            <span>AI Insights</span>
          </li>
        </ul>

        <div className="navigator-footer">
          <p>© 2026 Billing Management System Inc.</p>
          <p>v1.0.0 (Stable)</p>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="app-content animate-fade-in">
        {children}
      </main>
    </div>
  );
}
