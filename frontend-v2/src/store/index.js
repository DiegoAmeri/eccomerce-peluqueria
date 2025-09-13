import { createStore } from 'vuex';

// Usuarios de prueba predefinidos
const testUsers = {
  client: {
    id: 1,
    email: 'cliente@stylecut.com',
    password: 'cliente123',
    nombre: 'Juan',
    apellido: 'Pérez',
    tipo: 'client',
    telefono: '+1234567890',
    avatar: null
  },
  barber: {
    id: 2,
    email: 'barbero@stylecut.com',
    password: 'barbero123',
    nombre: 'Carlos',
    apellido: 'Gómez',
    tipo: 'barber',
    telefono: '+1234567891',
    especialidad: 'Cortes modernos',
    experiencia: 5,
    peluqueriaId: 1,
    avatar: null
  },
  owner: {
    id: 3,
    email: 'propietario@stylecut.com',
    password: 'propietario123',
    nombre: 'María',
    apellido: 'López',
    tipo: 'owner',
    telefono: '+1234567892',
    nombrePeluqueria: 'StyleCut Premium',
    categoria: 'peluqueria',
    direccion: 'Av. Principal 123',
    avatar: null
  }
};

export default createStore({
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
    },
    
    // Auth state (moved from module for simplicity)
    user: null,
    token: null,
    isAuthenticated: false
  },

  getters: {
    isLoading: (state) => state.loading,
    getError: (state) => state.error,
    getConfig: (state) => state.config,
    getFeatures: (state) => state.features,
    
    // Auth getters
    currentUser: (state) => state.user,
    isAuthenticated: (state) => state.isAuthenticated,
    userType: (state) => state.user ? state.user.tipo : null,
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
    
    // Auth mutations
    SET_USER(state, user) {
      state.user = user;
      state.isAuthenticated = !!user;
    },
    
    SET_TOKEN(state, token) {
      state.token = token;
    },
    
    LOGOUT(state) {
      state.user = null;
      state.token = null;
      state.isAuthenticated = false;
    },
    
    // Global reset mutation
    RESET_STATE(state) {
      // Reset all state except config and features
      state.loading = false;
      state.error = null;
      state.user = null;
      state.token = null;
      state.isAuthenticated = false;
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
    
    // Auth actions
    async login({ commit, dispatch }, credentials) {
      try {
        dispatch('setLoading', true);
        
        // Simular tiempo de respuesta del servidor
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Buscar usuario que coincida
        const userKey = Object.keys(testUsers).find(key => 
          testUsers[key].email === credentials.email && 
          testUsers[key].password === credentials.password
        );
        
        if (userKey) {
          const user = { ...testUsers[userKey] };
          commit('SET_USER', user);
          commit('SET_TOKEN', 'fake-jwt-token-' + user.id);
          
          // Guardar en localStorage si "recordarme" está activado
          if (credentials.remember) {
            localStorage.setItem('user', JSON.stringify(user));
            localStorage.setItem('token', 'fake-jwt-token-' + user.id);
          }
          
          dispatch('setError', null);
          return user;
        } else {
          throw new Error('Credenciales inválidas');
        }
      } catch (error) {
        dispatch('setError', error.message);
        throw error;
      } finally {
        dispatch('setLoading', false);
      }
    },
    
    async logout({ commit, dispatch }) {
      try {
        commit('LOGOUT');
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        dispatch('setError', null);
      } catch (error) {
        dispatch('setError', 'Error al cerrar sesión');
        throw error;
      }
    },
    
    verifyToken({ commit, dispatch }) {
      const user = localStorage.getItem('user');
      const token = localStorage.getItem('token');
      
      if (user && token) {
        commit('SET_USER', JSON.parse(user));
        commit('SET_TOKEN', token);
      }
    },
    
    // Initialize app
    async initializeApp({ dispatch }) {
      try {
        dispatch('setLoading', true);
        
        // Load saved configuration
        dispatch('loadConfig');
        
        // Initialize auth
        dispatch('verifyToken');
        
        // Load initial data if authenticated
        if (this.getters.isAuthenticated) {
          // Aquí podrías cargar datos adicionales del usuario
          console.log('Usuario autenticado:', this.getters.currentUser);
        }
        
      } catch (error) {
        console.error('Failed to initialize app:', error);
        dispatch('setError', 'Error al inicializar la aplicación');
      } finally {
        dispatch('setLoading', false);
      }
    },
    
    // Global data refresh
    async refreshData({ dispatch, getters }) {
      if (!getters.isAuthenticated) return;
      
      try {
        // Aquí podrías actualizar datos del usuario
        console.log('Refrescando datos para:', getters.currentUser);
      } catch (error) {
        console.error('Data refresh error:', error);
      }
    }
  }
});