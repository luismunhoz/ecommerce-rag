import React, { createContext, useState, useCallback, useEffect, useContext } from 'react';
import cartApi from '../api/cartApi';
import { AuthContext } from './AuthContext';

export const CartContext = createContext(null);

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState({ items: [], totalAmount: 0, totalItems: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const { isAuthenticated } = useContext(AuthContext);

  const fetchCart = useCallback(async () => {
    if (!isAuthenticated) {
      setCart({ items: [], totalAmount: 0, totalItems: 0 });
      return;
    }

    try {
      setLoading(true);
      const data = await cartApi.get();
      setCart(data);
    } catch (err) {
      if (err.response?.status !== 401) {
        setError('Failed to fetch cart');
      }
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated]);

  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  const addToCart = useCallback(async (productId, quantity = 1) => {
    try {
      setLoading(true);
      setError(null);
      const updatedCart = await cartApi.addItem(productId, quantity);
      setCart(updatedCart);
      return updatedCart;
    } catch (err) {
      const message = err.response?.data?.message || 'Failed to add item to cart';
      setError(message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const updateQuantity = useCallback(async (productId, quantity) => {
    try {
      setLoading(true);
      setError(null);
      const updatedCart = await cartApi.updateItem(productId, quantity);
      setCart(updatedCart);
      return updatedCart;
    } catch (err) {
      const message = err.response?.data?.message || 'Failed to update quantity';
      setError(message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const removeFromCart = useCallback(async (productId) => {
    try {
      setLoading(true);
      setError(null);
      const updatedCart = await cartApi.removeItem(productId);
      setCart(updatedCart);
      return updatedCart;
    } catch (err) {
      const message = err.response?.data?.message || 'Failed to remove item';
      setError(message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const clearCart = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      await cartApi.clear();
      setCart({ items: [], totalAmount: 0, totalItems: 0 });
    } catch (err) {
      const message = err.response?.data?.message || 'Failed to clear cart';
      setError(message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  const value = {
    cart,
    loading,
    error,
    addToCart,
    updateQuantity,
    removeFromCart,
    clearCart,
    refreshCart: fetchCart,
    clearError,
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};

export default CartContext;
