import axios from './axios.config';

export const ordersApi = {
  create: async (orderData) => {
    const response = await axios.post('/orders', orderData);
    return response.data;
  },

  createFromCart: async (shippingAddress, paymentMethod) => {
    const response = await axios.post(
      `/orders/from-cart?shippingAddress=${encodeURIComponent(shippingAddress)}&paymentMethod=${paymentMethod}`
    );
    return response.data;
  },

  getMyOrders: async () => {
    const response = await axios.get('/orders');
    return response.data;
  },

  getById: async (id) => {
    const response = await axios.get(`/orders/${id}`);
    return response.data;
  },

  getByOrderNumber: async (orderNumber) => {
    const response = await axios.get(`/orders/number/${orderNumber}`);
    return response.data;
  },

  cancel: async (id) => {
    const response = await axios.patch(`/orders/${id}/cancel`);
    return response.data;
  },
};

export default ordersApi;
