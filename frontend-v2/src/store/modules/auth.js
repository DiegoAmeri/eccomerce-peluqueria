export default {
  namespaced: true,
  
  state: {
    user: null,
    token: null,
    isLoading: false,
    error: null
  },
  
  mutations: {
    SET_USER(state, user) {
      state.user = user;
    },
    
    SET_TOKEN(state, token) {
      state.token = token;
    },
    
    SET_LOADING(state, loading) {
      state.isLoading = loading;
    },
    
    SET_ERROR(state, error) {
      state.error = error;
    },
    
    CLEAR_ERROR(state) {
      state.error = null;
    },
    
    RESET(state) {
      state.user = null;
      state.token = null;
      state.isLoading = false;
      state.error = null;
    }
  },
  
  getters: {
    user: (state) => state.user,
    token: (state) => state.token,
    isLoading: (state) => state.isLoading,
    error: (state) => state.error,
    isAuthenticated: (state) => !!state.token,
    userType: (state) => state.user?.type,
    userId: (state) => state.user?.id
  },
  
  actions: {
    async login({ commit, dispatch }, credentials) {
      try {
        commit('SET_LOADING', true);
        commit('CLEAR_ERROR');
        
        const auth = useAuth();
        const response = await auth.login(credentials);
        
        commit('SET_USER', response.data.user);
        commit('SET_TOKEN', response.data.token);
        
        // Load initial user data
        await dispatch('loadInitialData');
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al iniciar sesión';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async register({ commit }, userData) {
      try {
        commit('SET_LOADING', true);
        commit('CLEAR_ERROR');
        
        const auth = useAuth();
        const response = await auth.register(userData);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al registrar usuario';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async logout({ commit }) {
      const auth = useAuth();
      await auth.logout();
      commit('RESET');
    },
    
    async loadInitialData({ dispatch }) {
      await Promise.all([
        dispatch('user/loadProfile', null, { root: true }),
        dispatch('notification/loadNotifications', null, { root: true })
      ]);
    },
    
    clearError({ commit }) {
      commit('CLEAR_ERROR');
    },
    
    reset({ commit }) {
      commit('RESET');
    }
  }
};