import React from 'react';
import { Link } from 'react-router-dom';
import { useCart } from '../../hooks/useCart';
import { formatCurrency } from '../../utils/formatters';
import './Cart.css';

const CartItem = ({ item }) => {
  const { updateQuantity, removeFromCart, loading } = useCart();

  const handleQuantityChange = async (e) => {
    const newQuantity = parseInt(e.target.value, 10);
    if (newQuantity > 0) {
      await updateQuantity(item.productId, newQuantity);
    }
  };

  const handleRemove = async () => {
    await removeFromCart(item.productId);
  };

  return (
    <div className="cart-item card">
      <div className="cart-item-content">
        <div className="cart-item-image">
          {item.productImageUrl ? (
            <img src={item.productImageUrl} alt={item.productName} />
          ) : (
            <div className="cart-item-image-placeholder">No Image</div>
          )}
        </div>

        <div className="cart-item-details">
          <Link to={`/products/${item.productId}`} className="cart-item-name">
            {item.productName}
          </Link>
          <p className="cart-item-price">{formatCurrency(item.unitPrice)}</p>
        </div>

        <div className="cart-item-quantity">
          <label htmlFor={`quantity-${item.id}`}>Qty:</label>
          <select
            id={`quantity-${item.id}`}
            value={item.quantity}
            onChange={handleQuantityChange}
            disabled={loading}
          >
            {[...Array(10)].map((_, i) => (
              <option key={i + 1} value={i + 1}>
                {i + 1}
              </option>
            ))}
          </select>
        </div>

        <div className="cart-item-subtotal">
          <span className="subtotal-label">Subtotal:</span>
          <span className="subtotal-amount">{formatCurrency(item.subtotal)}</span>
        </div>

        <button
          onClick={handleRemove}
          className="btn btn-danger cart-item-remove"
          disabled={loading}
        >
          Remove
        </button>
      </div>
    </div>
  );
};

export default CartItem;
