import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';
import { formatCurrency, truncateText } from '../../utils/formatters';
import './Products.css';

const ProductCard = ({ product }) => {
  const { isAuthenticated } = useAuth();
  const { addToCart, loading } = useCart();

  const handleAddToCart = async (e) => {
    e.preventDefault();
    e.stopPropagation();

    if (!isAuthenticated) {
      return;
    }

    try {
      await addToCart(product.id, 1);
    } catch (err) {
      // Error handling is done in the cart context
    }
  };

  return (
    <div className="product-card card">
      <Link to={`/products/${product.id}`} className="product-link">
        <div className="product-image">
          {product.imageUrl ? (
            <img src={product.imageUrl} alt={product.name} />
          ) : (
            <div className="product-image-placeholder">No Image</div>
          )}
        </div>

        <div className="product-info">
          <h3 className="product-name">{product.name}</h3>
          <p className="product-description">
            {truncateText(product.description, 80)}
          </p>

          <div className="product-meta">
            <span className="product-price">{formatCurrency(product.price)}</span>
            <span
              className={`product-stock ${product.inStock ? 'in-stock' : 'out-of-stock'}`}
            >
              {product.inStock ? 'In Stock' : 'Out of Stock'}
            </span>
          </div>

          {product.categoryName && (
            <span className="product-category">{product.categoryName}</span>
          )}
        </div>
      </Link>

      <div className="product-actions">
        {isAuthenticated ? (
          <button
            onClick={handleAddToCart}
            disabled={!product.inStock || loading}
            className="btn btn-primary btn-block"
          >
            {loading ? 'Adding...' : 'Add to Cart'}
          </button>
        ) : (
          <Link to="/login" className="btn btn-secondary btn-block">
            Login to Buy
          </Link>
        )}
      </div>
    </div>
  );
};

export default ProductCard;
