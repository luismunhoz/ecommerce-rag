import axios from './axios.config';

export const cartApi = {
  get: async () => {
    const response = await axios.get('/cart');
    return response.data;
  },

  addItem: async (productId, quantity) => {
    const response = await axios.post('/cart/items', { productId, quantity });
    return response.data;
  },

  updateItem: async (productId, quantity) => {
    const response = await axios.put(`/cart/items/${productId}?quantity=${quantity}`);
    return response.data;
  },

  removeItem: async (productId) => {
    const response = await axios.delete(`/cart/items/${productId}`);
    return response.data;
  },

  clear: async () => {
    const response = await axios.delete('/cart');
    return response.data;
  },
};

export default cartApi;
