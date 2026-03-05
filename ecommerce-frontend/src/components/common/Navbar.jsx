import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';
import './Navbar.css';

const Navbar = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const { cart } = useCart();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  return (
    <nav className="navbar">
      <div className="container navbar-content">
        <Link to="/" className="navbar-brand">
          E-Commerce
        </Link>

        <div className="navbar-links">
          <Link to="/products" className="nav-link">
            Products
          </Link>

          {isAuthenticated ? (
            <>
              <Link to="/cart" className="nav-link cart-link">
                Cart
                {cart.totalItems > 0 && (
                  <span className="cart-badge">{cart.totalItems}</span>
                )}
              </Link>
              <span className="nav-user">
                Hello, {user?.firstName}
              </span>
              <button onClick={handleLogout} className="btn btn-secondary btn-sm">
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-link">
                Login
              </Link>
              <Link to="/register" className="btn btn-primary btn-sm">
                Register
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
