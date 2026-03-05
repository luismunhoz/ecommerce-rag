import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import productsApi from '../../api/productsApi';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';
import { formatCurrency } from '../../utils/formatters';
import LoadingSpinner from '../common/LoadingSpinner';
import { getErrorMessage } from '../../utils/errorHandler';
import './Products.css';

const ProductDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const { addToCart, loading: cartLoading } = useCart();

  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [addedToCart, setAddedToCart] = useState(false);

  useEffect(() => {
    const fetchProduct = async () => {
      setLoading(true);
      setError('');

      try {
        const data = await productsApi.getById(id);
        setProduct(data);
      } catch (err) {
        setError(getErrorMessage(err));
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    try {
      await addToCart(product.id, quantity);
      setAddedToCart(true);
      setTimeout(() => setAddedToCart(false), 3000);
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  if (loading) {
    return <LoadingSpinner message="Loading product details..." />;
  }

  if (error) {
    return (
      <div className="product-error">
        <div className="alert alert-error">{error}</div>
        <Link to="/products" className="btn btn-secondary">
          Back to Products
        </Link>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="product-not-found">
        <h2>Product not found</h2>
        <Link to="/products" className="btn btn-secondary">
          Back to Products
        </Link>
      </div>
    );
  }

  return (
    <div className="product-details">
      <Link to="/products" className="back-link">
        &larr; Back to Products
      </Link>

      <div className="product-details-content">
        <div className="product-details-image">
          {product.imageUrl ? (
            <img src={product.imageUrl} alt={product.name} />
          ) : (
            <div className="product-image-placeholder large">No Image</div>
          )}
        </div>

        <div className="product-details-info">
          <h1 className="product-title">{product.name}</h1>

          {product.categoryName && (
            <span className="product-category-badge">{product.categoryName}</span>
          )}

          <p className="product-price-large">{formatCurrency(product.price)}</p>

          <div className={`stock-status ${product.inStock ? 'in-stock' : 'out-of-stock'}`}>
            {product.inStock ? (
              <span>In Stock ({product.stockQuantity} available)</span>
            ) : (
              <span>Out of Stock</span>
            )}
          </div>

          {product.sku && <p className="product-sku">SKU: {product.sku}</p>}

          <div className="product-description-full">
            <h3>Description</h3>
            <p>{product.description || 'No description available.'}</p>
          </div>

          {product.inStock && (
            <div className="add-to-cart-section">
              <div className="quantity-selector">
                <label htmlFor="quantity">Quantity:</label>
                <select
                  id="quantity"
                  value={quantity}
                  onChange={(e) => setQuantity(Number(e.target.value))}
                >
                  {[...Array(Math.min(10, product.stockQuantity))].map((_, i) => (
                    <option key={i + 1} value={i + 1}>
                      {i + 1}
                    </option>
                  ))}
                </select>
              </div>

              <button
                onClick={handleAddToCart}
                disabled={cartLoading}
                className="btn btn-primary btn-lg"
              >
                {cartLoading ? 'Adding...' : 'Add to Cart'}
              </button>

              {addedToCart && (
                <p className="success-message">Added to cart!</p>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProductDetails;
