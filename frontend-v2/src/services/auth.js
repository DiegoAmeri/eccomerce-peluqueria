import { apiService } from './api';
import { useStore } from 'vuex';

// Token management
const TOKEN_KEY = 'auth_token';
const REFRESH_TOKEN_KEY = 'refresh_token';
const USER_DATA_KEY = 'user_data';

export const useAuth = () => {
  const store = useStore();

  const getToken = () => {
    return localStorage.getItem(TOKEN_KEY);
  };

  const getRefreshToken = () => {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  };

  const setTokens = (token, refreshToken) => {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  };

  const clearTokens = () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_DATA_KEY);
  };

  const getUserData = () => {
    const userData = localStorage.getItem(USER_DATA_KEY);
    return userData ? JSON.parse(userData) : null;
  };

  const setUserData = (userData) => {
    localStorage.setItem(USER_DATA_KEY, JSON.stringify(userData));
  };

  const clearUserData = () => {
    localStorage.removeItem(USER_DATA_KEY);
  };

  // Auth methods
  const login = async (credentials) => {
    try {
      const response = await apiService.auth.login(credentials);
      const { token, refreshToken, user } = response.data;
      
      setTokens(token, refreshToken);
      setUserData(user);
      
      // Update store
      store.commit('SET_USER', user);
      store.commit('SET_TOKEN', token);
      
      return response;
    } catch (error) {
      clearTokens();
      clearUserData();
      throw error;
    }
  };

  const register = async (userData) => {
    try {
      const response = await apiService.auth.register(userData);
      return response;
    } catch (error) {
      throw error;
    }
  };

  const logout = async () => {
    try {
      // Call logout API if token exists
      if (getToken()) {
        await apiService.auth.logout();
      }
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      // Always clear local storage
      clearTokens();
      clearUserData();
      store.commit('LOGOUT');
    }
  };

  const refreshToken = async () => {
    try {
      const refreshToken = getRefreshToken();
      if (!refreshToken) {
        throw new Error('No refresh token available');
      }

      const response = await apiService.auth.refresh();
      const { token, refreshToken: newRefreshToken } = response.data;
      
      setTokens(token, newRefreshToken);
      store.commit('SET_TOKEN', token);
      
      return response;
    } catch (error) {
      clearTokens();
      clearUserData();
      store.commit('LOGOUT');
      throw error;
    }
  };

  const forgotPassword = async (email) => {
    try {
      const response = await apiService.auth.forgotPassword(email);
      return response;
    } catch (error) {
      throw error;
    }
  };

  const resetPassword = async (data) => {
    try {
      const response = await apiService.auth.resetPassword(data);
      return response;
    } catch (error) {
      throw error;
    }
  };

  const verifyEmail = async (token) => {
    try {
      const response = await apiService.auth.verifyEmail(token);
      return response;
    } catch (error) {
      throw error;
    }
  };

  const isAuthenticated = () => {
    const token = getToken();
    if (!token) return false;

    // Check if token is expired
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 > Date.now();
    } catch (error) {
      return false;
    }
  };

  const getCurrentUser = () => {
    return getUserData();
  };

  const updateUserProfile = async (data) => {
    try {
      const response = await apiService.users.updateProfile(data);
      const user = response.data;
      
      setUserData(user);
      store.commit('SET_USER', user);
      
      return response;
    } catch (error) {
      throw error;
    }
  };

  const changePassword = async (data) => {
    try {
      const response = await apiService.users.changePassword(data);
      return response;
    } catch (error) {
      throw error;
    }
  };

  const uploadAvatar = async (file) => {
    try {
      const response = await apiService.users.uploadAvatar(file);
      const user = response.data;
      
      setUserData(user);
      store.commit('SET_USER', user);
      
      return response;
    } catch (error) {
      throw error;
    }
  };

  // Initialize auth state from localStorage
  const initializeAuth = () => {
    const token = getToken();
    const userData = getUserData();
    
    if (token && userData && isAuthenticated()) {
      store.commit('SET_USER', userData);
      store.commit('SET_TOKEN', token);
    } else {
      clearTokens();
      clearUserData();
    }
  };

  return {
    // Token management
    getToken,
    getRefreshToken,
    setTokens,
    clearTokens,
    getUserData,
    setUserData,
    clearUserData,
    
    // Auth methods
    login,
    register,
    logout,
    refreshToken,
    forgotPassword,
    resetPassword,
    verifyEmail,
    isAuthenticated,
    getCurrentUser,
    updateUserProfile,
    changePassword,
    uploadAvatar,
    initializeAuth,
  };
};

export default useAuth;