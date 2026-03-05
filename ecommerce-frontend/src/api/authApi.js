import axios from './axios.config';

export const authApi = {
  login: async (email, password) => {
    const response = await axios.post('/auth/login', { email, password });
    return response.data;
  },

  register: async (userData) => {
    const response = await axios.post('/auth/register', userData);
    return response.data;
  },

  logout: async () => {
    await axios.post('/auth/logout');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
};

export default authApi;
