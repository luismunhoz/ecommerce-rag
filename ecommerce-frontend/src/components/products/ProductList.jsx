import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import productsApi from '../../api/productsApi';
import ProductCard from './ProductCard';
import LoadingSpinner from '../common/LoadingSpinner';
import { getErrorMessage } from '../../utils/errorHandler';
import './Products.css';

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchParams, setSearchParams] = useSearchParams();
  const [searchQuery, setSearchQuery] = useState(searchParams.get('q') || '');
  const [semanticMode, setSemanticMode] = useState(searchParams.get('mode') === 'ai');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    const fetchProducts = async () => {
      setLoading(true);
      setError('');

      try {
        const query = searchParams.get('q');
        const isAi = searchParams.get('mode') === 'ai';
        let data;

        if (query && isAi) {
          const results = await productsApi.semanticSearch(query);
          setProducts(results || []);
          setTotalPages(0);
          return;
        } else if (query) {
          data = await productsApi.search(query, page);
        } else {
          data = await productsApi.getAll(page);
        }

        setProducts(data.content || []);
        setTotalPages(data.totalPages || 0);
      } catch (err) {
        setError(getErrorMessage(err));
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, [searchParams, page]);

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      const params = { q: searchQuery };
      if (semanticMode) params.mode = 'ai';
      setSearchParams(params);
    } else {
      setSearchParams({});
    }
    setPage(0);
  };

  const handleClearSearch = () => {
    setSearchQuery('');
    setSearchParams({});
    setPage(0);
  };

  const handleModeToggle = () => {
    setSemanticMode((prev) => !prev);
  };

  const query = searchParams.get('q');
  const isAiResults = query && searchParams.get('mode') === 'ai';

  if (loading) {
    return <LoadingSpinner message="Loading products..." />;
  }

  return (
    <div className="products-page">
      <div className="products-header">
        <h1 className="page-title">Products</h1>

        <form onSubmit={handleSearch} className="search-form">
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder={semanticMode ? 'Describe what you are looking for...' : 'Search products...'}
            className="search-input"
          />
          <label className="semantic-toggle" title="Use AI-powered semantic search">
            <input
              type="checkbox"
              checked={semanticMode}
              onChange={handleModeToggle}
            />
            <span className="semantic-toggle-label">AI Search</span>
          </label>
          <button type="submit" className="btn btn-primary">
            Search
          </button>
          {query && (
            <button
              type="button"
              onClick={handleClearSearch}
              className="btn btn-secondary"
            >
              Clear
            </button>
          )}
        </form>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {query && (
        <p className="search-results-info">
          {isAiResults && <span className="ai-badge">AI</span>}
          Showing results for: <strong>{query}</strong>
        </p>
      )}

      {products.length === 0 ? (
        <div className="no-products">
          <p>No products found.</p>
        </div>
      ) : (
        <>
          <div className="products-grid grid grid-4">
            {products.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>

          {!isAiResults && totalPages > 1 && (
            <div className="pagination">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="btn btn-secondary"
              >
                Previous
              </button>
              <span className="page-info">
                Page {page + 1} of {totalPages}
              </span>
              <button
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
                className="btn btn-secondary"
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default ProductList;
