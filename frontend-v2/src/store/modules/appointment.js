export default {
  namespaced: true,
  
  state: {
    appointments: [],
    upcomingAppointments: [],
    pastAppointments: [],
    currentAppointment: null,
    filters: {
      status: '',
      dateFrom: '',
      dateTo: '',
      salon: '',
      service: ''
    },
    pagination: {
      currentPage: 1,
      totalPages: 1,
      totalItems: 0,
      itemsPerPage: 10
    },
    isLoading: false,
    error: null
  },
  
  mutations: {
    SET_APPOINTMENTS(state, appointments) {
      state.appointments = appointments;
    },
    
    SET_UPCOMING_APPOINTMENTS(state, appointments) {
      state.upcomingAppointments = appointments;
    },
    
    SET_PAST_APPOINTMENTS(state, appointments) {
      state.pastAppointments = appointments;
    },
    
    SET_CURRENT_APPOINTMENT(state, appointment) {
      state.currentAppointment = appointment;
    },
    
    ADD_APPOINTMENT(state, appointment) {
      state.appointments.unshift(appointment);
      state.upcomingAppointments.unshift(appointment);
    },
    
    UPDATE_APPOINTMENT(state, updatedAppointment) {
      const index = state.appointments.findIndex(a => a.id === updatedAppointment.id);
      if (index !== -1) {
        state.appointments.splice(index, 1, updatedAppointment);
      }
      
      const upcomingIndex = state.upcomingAppointments.findIndex(a => a.id === updatedAppointment.id);
      if (upcomingIndex !== -1) {
        state.upcomingAppointments.splice(upcomingIndex, 1, updatedAppointment);
      }
      
      const pastIndex = state.pastAppointments.findIndex(a => a.id === updatedAppointment.id);
      if (pastIndex !== -1) {
        state.pastAppointments.splice(pastIndex, 1, updatedAppointment);
      }
    },
    
    REMOVE_APPOINTMENT(state, appointmentId) {
      state.appointments = state.appointments.filter(a => a.id !== appointmentId);
      state.upcomingAppointments = state.upcomingAppointments.filter(a => a.id !== appointmentId);
      state.pastAppointments = state.pastAppointments.filter(a => a.id !== appointmentId);
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
      state.appointments = [];
      state.upcomingAppointments = [];
      state.pastAppointments = [];
      state.currentAppointment = null;
      state.filters = {
        status: '',
        dateFrom: '',
        dateTo: '',
        salon: '',
        service: ''
      };
      state.pagination = {
        currentPage: 1,
        totalPages: 1,
        totalItems: 0,
        itemsPerPage: 10
      };
      state.isLoading = false;
      state.error = null;
    }
  },
  
  getters: {
    appointments: (state) => state.appointments,
    upcomingAppointments: (state) => state.upcomingAppointments,
    pastAppointments: (state) => state.pastAppointments,
    currentAppointment: (state) => state.currentAppointment,
    filters: (state) => state.filters,
    pagination: (state) => state.pagination,
    isLoading: (state) => state.isLoading,
    error: (state) => state.error,
    
    // Filtered appointments based on current filters
    filteredAppointments: (state) => {
      let filtered = state.appointments;
      
      // Apply status filter
      if (state.filters.status) {
        filtered = filtered.filter(appointment => appointment.status === state.filters.status);
      }
      
      // Apply date range filter
      if (state.filters.dateFrom) {
        filtered = filtered.filter(appointment => 
          new Date(appointment.date) >= new Date(state.filters.dateFrom)
        );
      }
      
      if (state.filters.dateTo) {
        filtered = filtered.filter(appointment => 
          new Date(appointment.date) <= new Date(state.filters.dateTo)
        );
      }
      
      // Apply salon filter
      if (state.filters.salon) {
        filtered = filtered.filter(appointment => 
          appointment.salonId.toString() === state.filters.salon
        );
      }
      
      // Apply service filter
      if (state.filters.service) {
        filtered = filtered.filter(appointment => 
          appointment.service.toLowerCase().includes(state.filters.service.toLowerCase())
        );
      }
      
      return filtered;
    }
  },
  
  actions: {
    async loadAppointments({ commit, state }, params = {}) {
      try {
        commit('SET_LOADING', true);
        
        const queryParams = {
          page: params.page || state.pagination.currentPage,
          limit: params.limit || state.pagination.itemsPerPage,
          ...state.filters,
          ...params
        };
        
        const response = await apiService.appointments.getAll(queryParams);
        const { data, pagination } = response.data;
        
        commit('SET_APPOINTMENTS', data);
        commit('SET_PAGINATION', pagination);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al cargar turnos';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async loadUpcomingAppointments({ commit }) {
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.appointments.getUpcoming();
        commit('SET_UPCOMING_APPOINTMENTS', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al cargar próximos turnos';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async loadPastAppointments({ commit }, params = {}) {
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.appointments.getHistory(params);
        commit('SET_PAST_APPOINTMENTS', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al cargar historial de turnos';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async loadAppointmentDetail({ commit }, appointmentId) {
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.appointments.getById(appointmentId);
        commit('SET_CURRENT_APPOINTMENT', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al cargar detalles del turno';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async createAppointment({ commit }, appointmentData) {
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.appointments.create(appointmentData);
        commit('ADD_APPOINTMENT', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al crear turno';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async updateAppointment({ commit }, { id, data }) {
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.appointments.update(id, data);
        commit('UPDATE_APPOINTMENT', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al actualizar turno';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async cancelAppointment({ commit }, appointmentId) {
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.appointments.cancel(appointmentId);
        commit('REMOVE_APPOINTMENT', appointmentId);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al cancelar turno';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async confirmAppointment({ commit }, appointmentId) {
      try {
        const response = await apiService.appointments.confirm(appointmentId);
        commit('UPDATE_APPOINTMENT', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al confirmar turno';
        commit('SET_ERROR', errorMessage);
        throw error;
      }
    },
    
    async completeAppointment({ commit }, appointmentId) {
      try {
        const response = await apiService.appointments.complete(appointmentId);
        commit('UPDATE_APPOINTMENT', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al completar turno';
        commit('SET_ERROR', errorMessage);
        throw error;
      }
    },
    
    updateFilters({ commit }, filters) {
      commit('SET_FILTERS', filters);
    },
    
    clearError({ commit }) {
      commit('CLEAR_ERROR');
    },
    
    reset({ commit }) {
      commit('RESET');
    }
  }
};