export default {
  namespaced: true,
  
  state: {
    salons: [],
    featuredSalons: [],
    currentSalon: null,
    favorites: [],
    searchResults: [],
    filters: {
      category: '',
      rating: 0,
      priceRange: '',
      services: [],
      sortBy: 'rating'
    },
    pagination: {
      currentPage: 1,
      totalPages: 1,
      totalItems: 0,
      itemsPerPage: 12
    },
    isLoading: false,
    error: null
  },
  
  mutations: {
    SET_SALONS(state, salons) {
      state.salons = salons;
    },
    
    SET_FEATURED_SALONS(state, salons) {
      state.featuredSalons = salons;
    },
    
    SET_CURRENT_SALON(state, salon) {
      state.currentSalon = salon;
    },
    
    SET_FAVORITES(state, favorites) {
      state.favorites = favorites;
    },
    
    ADD_FAVORITE(state, salon) {
      if (!state.favorites.find(f => f.id === salon.id)) {
        state.favorites.push(salon);
      }
    },
    
    REMOVE_FAVORITE(state, salonId) {
      state.favorites = state.favorites.filter(f => f.id !== salonId);
    },
    
    SET_SEARCH_RESULTS(state, results) {
      state.searchResults = results;
    },
    
    SET_FILTERS(state, filters) {
      state.filters = { ...state.filters, ...filters };
    },
    
    SET_PAGINATION(state, pagination) {
      state.pagination = { ...state.pagination, ...pagination };
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
      state.salons = [];
      state.featuredSalons = [];
      state.currentSalon = null;
      state.favorites = [];
      state.searchResults = [];
      state.filters = {
        category: '',
        rating: 0,
        priceRange: '',
        services: [],
        sortBy: 'rating'
      };
      state.pagination = {
        currentPage: 1,
        totalPages: 1,
        totalItems: 0,
        itemsPerPage: 12
      };
      state.isLoading = false;
      state.error = null;
    }
  },
  
  getters: {
    salons: (state) => state.salons,
    featuredSalons: (state) => state.featuredSalons,
    currentSalon: (state) => state.currentSalon,
    favorites: (state) => state.favorites,
    searchResults: (state) => state.searchResults,
    filters: (state) => state.filters,
    pagination: (state) => state.pagination,
    isLoading: (state) => state.isLoading,
    error: (state) => state.error,
    
    // Filtered salons based on current filters
    filteredSalons: (state) => {
      let filtered = state.salons;
      
      // Apply category filter
      if (state.filters.category) {
        filtered = filtered.filter(salon => salon.category === state.filters.category);
      }
      
      // Apply rating filter
      if (state.filters.rating > 0) {
        filtered = filtered.filter(salon => salon.rating >= state.filters.rating);
      }
      
      // Apply price range filter
      if (state.filters.priceRange) {
        filtered = filtered.filter(salon => {
          if (state.filters.priceRange === 'low') return salon.priceRange === 1;
          if (state.filters.priceRange === 'medium') return salon.priceRange === 2;
          if (state.filters.priceRange === 'high') return salon.priceRange === 3;
          return true;
        });
      }
      
      // Apply services filter
      if (state.filters.services.length > 0) {
        filtered = filtered.filter(salon =>
          state.filters.services.every(service =>
            salon.services.includes(service)
          )
        );
      }
      
      // Apply sorting
      switch (state.filters.sortBy) {
        case 'rating':
          filtered.sort((a, b) => b.rating - a.rating);
          break;
        case 'name':
          filtered.sort((a, b) => a.name.localeCompare(b.name));
          break;
        case 'distance':
          filtered.sort((a, b) => a.distance - b.distance);
          break;
      }
      
      return filtered;
    },
    
    // Check if a salon is in favorites
    isFavorite: (state) => (salonId) => {
      return state.favorites.some(favorite => favorite.id === salonId);
    }
  },
  
  actions: {
    async loadSalons({ commit, state }, params = {}) {
      try {
        commit('SET_LOADING', true);
        
        const queryParams = {
          page: params.page || state.pagination.currentPage,
          limit: params.limit || state.pagination.itemsPerPage,
          ...state.filters,
          ...params
        };
        
        const response = await apiService.salons.getAll(queryParams);
        const { data, pagination } = response.data;
        
        commit('SET_SALONS', data);
        commit('SET_PAGINATION', pagination);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al cargar peluquerías';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async loadFeaturedSalons({ commit }) {
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.salons.getFeatured();
        commit('SET_FEATURED_SALONS', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al cargar peluquerías destacadas';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async loadSalonDetail({ commit }, salonId) {
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.salons.getById(salonId);
        commit('SET_CURRENT_SALON', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al cargar detalles de la peluquería';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async searchSalons({ commit }, { query, params = {} }) {
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.salons.search(query, params);
        commit('SET_SEARCH_RESULTS', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al buscar peluquerías';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async loadFavorites({ commit }) {
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.favorites.getAll();
        commit('SET_FAVORITES', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al cargar favoritos';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async addToFavorites({ commit }, salonId) {
      try {
        const response = await apiService.favorites.add(salonId);
        
        // If we have the salon data, add it to favorites
        const salon = response.data;
        commit('ADD_FAVORITE', salon);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al agregar a favoritos';
        commit('SET_ERROR', errorMessage);
        throw error;
      }
    },
    
    async removeFromFavorites({ commit }, salonId) {
      try {
        const response = await apiService.favorites.remove(salonId);
        commit('REMOVE_FAVORITE', salonId);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al eliminar de favoritos';
        commit('SET_ERROR', errorMessage);
        throw error;
      }
    },
    
    async toggleFavorite({ commit, getters, dispatch }, salon) {
      const isFavorite = getters.isFavorite(salon.id);
      
      if (isFavorite) {
        await dispatch('removeFromFavorites', salon.id);
      } else {
        await dispatch('addToFavorites', salon.id);
      }
      
      return !isFavorite;
    },
    
    updateFilters({ commit, dispatch }, filters) {
      commit('SET_FILTERS', filters);
      // Reload salons with new filters
      return dispatch('loadSalons', { page: 1 });
    },
    
    clearFilters({ commit, dispatch }) {
      commit('SET_FILTERS', {
        category: '',
        rating: 0,
        priceRange: '',
        services: [],
        sortBy: 'rating'
      });
      // Reload salons without filters
      return dispatch('loadSalons', { page: 1 });
    },
    
    setPagination({ commit }, pagination) {
      commit('SET_PAGINATION', pagination);
    },
    
    clearError({ commit }) {
      commit('CLEAR_ERROR');
    },
    
    resetState({ commit }) {
      commit('RESET');
    }
  }
};