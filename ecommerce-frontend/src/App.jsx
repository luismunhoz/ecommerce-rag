import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/common/Navbar';
import ErrorBoundary from './components/common/ErrorBoundary';
import PrivateRoute from './components/auth/PrivateRoute';
import Login from './components/auth/Login';
import Register from './components/auth/Register';
import ProductList from './components/products/ProductList';
import ProductDetails from './components/products/ProductDetails';
import Cart from './components/cart/Cart';
import './App.css';

function App() {
  return (
    <ErrorBoundary>
      <div className="app">
        <Navbar />
        <main className="main-content">
          <div className="container">
            <Routes>
              <Route path="/" element={<ProductList />} />
              <Route path="/products" element={<ProductList />} />
              <Route path="/products/:id" element={<ProductDetails />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route
                path="/cart"
                element={
                  <PrivateRoute>
                    <Cart />
                  </PrivateRoute>
                }
              />
            </Routes>
          </div>
        </main>
      </div>
    </ErrorBoundary>
  );
}

export default App;
