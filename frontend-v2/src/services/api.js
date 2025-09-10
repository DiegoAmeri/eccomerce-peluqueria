import axios from 'axios';
import { useAuth } from './auth';

// Create axios instance with default config
const api = axios.create({
  baseURL: 'http://localhost:8090/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const { getToken } = useAuth();
    const token = getToken();
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Add timestamp to avoid caching
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now(),
      };
    }
    
    console.log(`API Request: ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    console.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => {
    console.log(`API Response: ${response.status} ${response.config.url}`);
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        const { refreshToken } = useAuth();
        await refreshToken();
        
        // Retry the original request with new token
        const { getToken } = useAuth();
        originalRequest.headers.Authorization = `Bearer ${getToken()}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh token failed, logout user
        const { logout } = useAuth();
        await logout();
        return Promise.reject(refreshError);
      }
    }
    
    console.error('API Response Error:', error);
    return Promise.reject(error);
  }
);

// API methods
export const apiService = {
  // Auth endpoints
  auth: {
    login: (credentials) => api.post('/auth/login', credentials),
    register: (userData) => api.post('/auth/register', userData),
    refresh: () => api.post('/auth/refresh'),
    logout: () => api.post('/auth/logout'),
    forgotPassword: (email) => api.post('/auth/forgot-password', { email }),
    resetPassword: (data) => api.post('/auth/reset-password', data),
    verifyEmail: (token) => api.post('/auth/verify-email', { token }),
  },

  // User endpoints
  users: {
    getProfile: () => api.get('/users/profile'),
    updateProfile: (data) => api.put('/users/profile', data),
    changePassword: (data) => api.put('/users/change-password', data),
    uploadAvatar: (file) => {
      const formData = new FormData();
      formData.append('avatar', file);
      return api.post('/users/avatar', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
    },
  },

  // Salons endpoints
  salons: {
    getAll: (params) => api.get('/salons', { params }),
    getById: (id) => api.get(`/salons/${id}`),
    create: (data) => api.post('/salons', data),
    update: (id, data) => api.put(`/salons/${id}`, data),
    delete: (id) => api.delete(`/salons/${id}`),
    search: (query, params) => api.get('/salons/search', { params: { q: query, ...params } }),
    getByCategory: (category, params) => api.get(`/salons/category/${category}`, { params }),
    getFeatured: () => api.get('/salons/featured'),
    getReviews: (id, params) => api.get(`/salons/${id}/reviews`, { params }),
    addReview: (id, data) => api.post(`/salons/${id}/reviews`, data),
  },

  // Appointments endpoints
  appointments: {
    getAll: (params) => api.get('/appointments', { params }),
    getById: (id) => api.get(`/appointments/${id}`),
    create: (data) => api.post('/appointments', data),
    update: (id, data) => api.put(`/appointments/${id}`, data),
    cancel: (id) => api.delete(`/appointments/${id}`),
    getUpcoming: () => api.get('/appointments/upcoming'),
    getHistory: (params) => api.get('/appointments/history', { params }),
    confirm: (id) => api.post(`/appointments/${id}/confirm`),
    complete: (id) => api.post(`/appointments/${id}/complete`),
  },

  // Services endpoints
  services: {
    getAll: (params) => api.get('/services', { params }),
    getById: (id) => api.get(`/services/${id}`),
    create: (data) => api.post('/services', data),
    update: (id, data) => api.put(`/services/${id}`, data),
    delete: (id) => api.delete(`/services/${id}`),
    getBySalon: (salonId, params) => api.get(`/services/salon/${salonId}`, { params }),
    getCategories: () => api.get('/services/categories'),
  },

  // Employees endpoints
  employees: {
    getAll: (params) => api.get('/employees', { params }),
    getById: (id) => api.get(`/employees/${id}`),
    create: (data) => api.post('/employees', data),
    update: (id, data) => api.put(`/employees/${id}`, data),
    delete: (id) => api.delete(`/employees/${id}`),
    getBySalon: (salonId, params) => api.get(`/employees/salon/${salonId}`, { params }),
    getAvailability: (id, params) => api.get(`/employees/${id}/availability`, { params }),
    updateAvailability: (id, data) => api.put(`/employees/${id}/availability`, data),
    getSchedule: (id, params) => api.get(`/employees/${id}/schedule`, { params }),
  },

  // Favorites endpoints
  favorites: {
    getAll: () => api.get('/favorites'),
    add: (salonId) => api.post('/favorites', { salonId }),
    remove: (salonId) => api.delete(`/favorites/${salonId}`),
    check: (salonId) => api.get(`/favorites/check/${salonId}`),
  },

  // Notifications endpoints
  notifications: {
    getAll: (params) => api.get('/notifications', { params }),
    getUnreadCount: () => api.get('/notifications/unread-count'),
    markAsRead: (id) => api.put(`/notifications/${id}/read`),
    markAllAsRead: () => api.put('/notifications/mark-all-read'),
    delete: (id) => api.delete(`/notifications/${id}`),
  },

  // Analytics endpoints (for owners)
  analytics: {
    getDashboardStats: (params) => api.get('/analytics/dashboard', { params }),
    getRevenueReport: (params) => api.get('/analytics/revenue', { params }),
    getAppointmentsReport: (params) => api.get('/analytics/appointments', { params }),
    getEmployeePerformance: (params) => api.get('/analytics/employee-performance', { params }),
    getCustomerMetrics: (params) => api.get('/analytics/customer-metrics', { params }),
  },

  // File upload endpoints
  upload: {
    image: (file) => {
      const formData = new FormData();
      formData.append('file', file);
      return api.post('/upload/image', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
    },
    multiple: (files) => {
      const formData = new FormData();
      files.forEach((file) => formData.append('files', file));
      return api.post('/upload/multiple', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
    },
  },
};

// Utility functions
export const apiUtils = {
  // Handle API errors consistently
  handleError: (error) => {
    if (error.response) {
      // Server responded with error status
      const message = error.response.data?.message || 'Error del servidor';
      const status = error.response.status;
      
      return {
        message,
        status,
        data: error.response.data,
        isNetworkError: false,
      };
    } else if (error.request) {
      // Request made but no response received
      return {
        message: 'Error de conexión. Por favor, verifica tu conexión a internet.',
        status: null,
        data: null,
        isNetworkError: true,
      };
    } else {
      // Something else happened
      return {
        message: error.message || 'Error inesperado',
        status: null,
        data: null,
        isNetworkError: false,
      };
    }
  },

  // Cancel token for aborting requests
  createCancelToken: () => {
    return axios.CancelToken.source();
  },

  // Debounce function for search requests
  debounce: (func, wait) => {
    let timeout;
    return function executedFunction(...args) {
      const later = () => {
        clearTimeout(timeout);
        func(...args);
      };
      clearTimeout(timeout);
      timeout = setTimeout(later, wait);
    };
  },
};

export default api;