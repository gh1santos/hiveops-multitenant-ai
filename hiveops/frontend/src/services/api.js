import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
});

// Interceptor para anexar o Token JWT em todas as chamadas
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('hiveops_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;