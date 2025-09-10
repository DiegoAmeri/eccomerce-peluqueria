export default {
  namespaced: true,
  
  state: {
    profile: null,
    preferences: {},
    statistics: null,
    isLoading: false,
    error: null
  },
  
  mutations: {
    SET_PROFILE(state, profile) {
      state.profile = profile;
    },
    
    SET_PREFERENCES(state, preferences) {
      state.preferences = preferences;
    },
    
    SET_STATISTICS(state, statistics) {
      state.statistics = statistics;
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
      state.profile = null;
      state.preferences = {};
      state.statistics = null;
      state.isLoading = false;
      state.error = null;
    }
  },
  
  getters: {
    profile: (state) => state.profile,
    preferences: (state) => state.preferences,
    statistics: (state) => state.statistics,
    isLoading: (state) => state.isLoading,
    error: (state) => state.error
  },
  
  actions: {
    async loadProfile({ commit, rootGetters }) {
      if (!rootGetters.isAuthenticated) return;
      
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.users.getProfile();
        commit('SET_PROFILE', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al cargar perfil';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async updateProfile({ commit }, profileData) {
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.users.updateProfile(profileData);
        commit('SET_PROFILE', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al actualizar perfil';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async changePassword({ commit }, passwordData) {
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.users.changePassword(passwordData);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al cambiar contraseña';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async uploadAvatar({ commit }, file) {
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.users.uploadAvatar(file);
        commit('SET_PROFILE', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al subir avatar';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async loadStatistics({ commit, rootGetters }) {
      if (!rootGetters.isAuthenticated) return;
      
      try {
        commit('SET_LOADING', true);
        
        let endpoint;
        const userType = rootGetters.userType;
        
        switch (userType) {
          case 'client':
            endpoint = '/users/statistics';
            break;
          case 'barber':
            endpoint = '/barbers/statistics';
            break;
          case 'owner':
            endpoint = '/owners/statistics';
            break;
          default:
            return;
        }
        
        const response = await api.get(endpoint);
        commit('SET_STATISTICS', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al cargar estadísticas';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async updatePreferences({ commit, state }, preferences) {
      try {
        const newPreferences = { ...state.preferences, ...preferences };
        commit('SET_PREFERENCES', newPreferences);
        
        // Persist to localStorage
        localStorage.setItem('user_preferences', JSON.stringify(newPreferences));
        
        // Optionally sync with API
        if (rootGetters.isAuthenticated) {
          await apiService.users.updateProfile({ preferences: newPreferences });
        }
      } catch (error) {
        console.error('Error updating preferences:', error);
      }
    },
    
    loadPreferences({ commit }) {
      const savedPreferences = localStorage.getItem('user_preferences');
      if (savedPreferences) {
        commit('SET_PREFERENCES', JSON.parse(savedPreferences));
      }
    },
    
    clearError({ commit }) {
      commit('CLEAR_ERROR');
    },
    
    reset({ commit }) {
      commit('RESET');
    }
  }
};