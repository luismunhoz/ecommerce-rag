import axios from './axios.config';

export const productsApi = {
  getAll: async (page = 0, size = 20) => {
    const response = await axios.get(`/products?page=${page}&size=${size}`);
    return response.data;
  },

  getById: async (id) => {
    const response = await axios.get(`/products/${id}`);
    return response.data;
  },

  getByCategory: async (categoryId, page = 0, size = 20) => {
    const response = await axios.get(`/products/category/${categoryId}?page=${page}&size=${size}`);
    return response.data;
  },

  search: async (query, page = 0, size = 20) => {
    const response = await axios.get(`/products/search?q=${encodeURIComponent(query)}&page=${page}&size=${size}`);
    return response.data;
  },

  semanticSearch: async (query, limit = 10) => {
    const response = await axios.get(`/products/semantic-search?q=${encodeURIComponent(query)}&limit=${limit}`);
    return response.data;
  },

  create: async (productData) => {
    const response = await axios.post('/products', productData);
    return response.data;
  },

  update: async (id, productData) => {
    const response = await axios.put(`/products/${id}`, productData);
    return response.data;
  },

  delete: async (id) => {
    await axios.delete(`/products/${id}`);
  },
};

export default productsApi;
