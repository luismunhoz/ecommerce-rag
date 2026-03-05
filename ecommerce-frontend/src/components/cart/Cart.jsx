import React from 'react';
import { Link } from 'react-router-dom';
import { useCart } from '../../hooks/useCart';
import CartItem from './CartItem';
import LoadingSpinner from '../common/LoadingSpinner';
import { formatCurrency } from '../../utils/formatters';
import './Cart.css';

const Cart = () => {
  const { cart, loading, error, clearCart } = useCart();

  if (loading && cart.items.length === 0) {
    return <LoadingSpinner message="Loading cart..." />;
  }

  return (
    <div className="cart-page">
      <h1 className="page-title">Shopping Cart</h1>

      {error && <div className="alert alert-error">{error}</div>}

      {cart.items.length === 0 ? (
        <div className="empty-cart">
          <p>Your cart is empty.</p>
          <Link to="/products" className="btn btn-primary">
            Continue Shopping
          </Link>
        </div>
      ) : (
        <div className="cart-content">
          <div className="cart-items">
            {cart.items.map((item) => (
              <CartItem key={item.id} item={item} />
            ))}
          </div>

          <div className="cart-summary card">
            <div className="card-header">
              <h3>Order Summary</h3>
            </div>
            <div className="card-body">
              <div className="summary-row">
                <span>Items ({cart.totalItems})</span>
                <span>{formatCurrency(cart.totalAmount)}</span>
              </div>
              <div className="summary-row">
                <span>Shipping</span>
                <span>Free</span>
              </div>
              <hr />
              <div className="summary-row total">
                <span>Total</span>
                <span>{formatCurrency(cart.totalAmount)}</span>
              </div>

              <button className="btn btn-success btn-block mt-3">
                Proceed to Checkout
              </button>

              <button
                onClick={clearCart}
                className="btn btn-secondary btn-block mt-2"
                disabled={loading}
              >
                Clear Cart
              </button>

              <Link to="/products" className="btn btn-outline btn-block mt-2">
                Continue Shopping
              </Link>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Cart;
