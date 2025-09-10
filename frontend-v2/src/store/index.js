import { createStore } from 'vuex';
import { apiService } from '@/services/api';
import useAuth from '@/services/auth';

// Importar módulos
import auth from './modules/auth';
import user from './modules/user';
import salon from './modules/salon';
import appointment from './modules/appointment';
import notification from './modules/notification';

export default createStore({
  modules: {
    auth,
    user,
    salon,
    appointment,
    notification
  },

  state: {
    // Global loading state
    loading: false,
    
    // Global error state
    error: null,
    
    // App configuration
    config: {
      theme: 'light',
      language: 'es',
      currency: 'ARS',
      timezone: 'America/Argentina/Buenos_Aires'
    },
    
    // Feature flags
    features: {
      onlineBooking: true,
      payments: true,
      notifications: true,
      reviews: true,
      analytics: true
    }
  },

  getters: {
    isLoading: (state) => state.loading,
    getError: (state) => state.error,
    getConfig: (state) => state.config,
    getFeatures: (state) => state.features,
    
    // Combined getters that work across modules
    currentUser: (state, getters) => getters['auth/user'],
    isAuthenticated: (state, getters) => getters['auth/isAuthenticated'],
    userType: (state, getters) => getters['auth/userType'],
  },

  mutations: {
    SET_LOADING(state, loading) {
      state.loading = loading;
    },
    
    SET_ERROR(state, error) {
      state.error = error;
    },
    
    CLEAR_ERROR(state) {
      state.error = null;
    },
    
    SET_CONFIG(state, config) {
      state.config = { ...state.config, ...config };
    },
    
    SET_FEATURES(state, features) {
      state.features = { ...state.features, ...features };
    },
    
    // Global reset mutation
    RESET_STATE(state) {
      // Reset all modules
      Object.keys(state).forEach(key => {
        if (key !== 'config' && key !== 'features') {
          state[key] = null;
        }
      });
    }
  },

  actions: {
    // Global loading actions
    setLoading({ commit }, loading) {
      commit('SET_LOADING', loading);
    },
    
    // Global error handling
    setError({ commit }, error) {
      commit('SET_ERROR', error);
      
      // Auto-clear error after 5 seconds
      setTimeout(() => {
        commit('CLEAR_ERROR');
      }, 5000);
    },
    
    clearError({ commit }) {
      commit('CLEAR_ERROR');
    },
    
    // Configuration actions
    updateConfig({ commit }, config) {
      commit('SET_CONFIG', config);
      
      // Persist to localStorage
      localStorage.setItem('app_config', JSON.stringify(config));
    },
    
    loadConfig({ commit }) {
      const savedConfig = localStorage.getItem('app_config');
      if (savedConfig) {
        commit('SET_CONFIG', JSON.parse(savedConfig));
      }
    },
    
    // Feature flag actions
    updateFeatures({ commit }, features) {
      commit('SET_FEATURES', features);
    },
    
    // Initialize app
    async initializeApp({ dispatch }) {
      try {
        dispatch('setLoading', true);
        
        // Load saved configuration
        dispatch('loadConfig');
        
        // Initialize auth
        const auth = useAuth();
        auth.initializeAuth();
        
        // Load initial data if authenticated
        if (this.getters.isAuthenticated) {
          await Promise.all([
            dispatch('user/loadProfile'),
            dispatch('notification/loadNotifications'),
            dispatch('appointment/loadUpcomingAppointments')
          ]);
        }
        
      } catch (error) {
        console.error('Failed to initialize app:', error);
        dispatch('setError', 'Error al inicializar la aplicación');
      } finally {
        dispatch('setLoading', false);
      }
    },
    
    // Global logout
    async logout({ dispatch }) {
      try {
        const auth = useAuth();
        await auth.logout();
        
        // Reset all module states
        dispatch('auth/reset');
        dispatch('user/reset');
        dispatch('salon/reset');
        dispatch('appointment/reset');
        dispatch('notification/reset');
        
        // Clear any stored data
        localStorage.removeItem('app_filters');
        localStorage.removeItem('app_preferences');
        
      } catch (error) {
        console.error('Logout error:', error);
        dispatch('setError', 'Error al cerrar sesión');
      }
    },
    
    // Global data refresh
    async refreshData({ dispatch, getters }) {
      if (!getters.isAuthenticated) return;
      
      try {
        await Promise.all([
          dispatch('user/loadProfile'),
          dispatch('notification/loadNotifications'),
          dispatch('appointment/loadUpcomingAppointments'),
          dispatch('salon/loadFavorites')
        ]);
      } catch (error) {
        console.error('Data refresh error:', error);
      }
    }
  }
});