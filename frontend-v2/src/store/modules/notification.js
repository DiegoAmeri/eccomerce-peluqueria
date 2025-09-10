export default {
  namespaced: true,
  
  state: {
    notifications: [],
    unreadCount: 0,
    isLoading: false,
    error: null
  },
  
  mutations: {
    SET_NOTIFICATIONS(state, notifications) {
      state.notifications = notifications;
    },
    
    SET_UNREAD_COUNT(state, count) {
      state.unreadCount = count;
    },
    
    ADD_NOTIFICATION(state, notification) {
      state.notifications.unshift(notification);
      state.unreadCount += 1;
    },
    
    MARK_AS_READ(state, notificationId) {
      const notification = state.notifications.find(n => n.id === notificationId);
      if (notification && !notification.read) {
        notification.read = true;
        state.unreadCount = Math.max(0, state.unreadCount - 1);
      }
    },
    
    MARK_ALL_AS_READ(state) {
      state.notifications.forEach(notification => {
        notification.read = true;
      });
      state.unreadCount = 0;
    },
    
    REMOVE_NOTIFICATION(state, notificationId) {
      const notification = state.notifications.find(n => n.id === notificationId);
      if (notification && !notification.read) {
        state.unreadCount = Math.max(0, state.unreadCount - 1);
      }
      state.notifications = state.notifications.filter(n => n.id !== notificationId);
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
      state.notifications = [];
      state.unreadCount = 0;
      state.isLoading = false;
      state.error = null;
    }
  },
  
  getters: {
    notifications: (state) => state.notifications,
    unreadCount: (state) => state.unreadCount,
    unreadNotifications: (state) => state.notifications.filter(n => !n.read),
    isLoading: (state) => state.isLoading,
    error: (state) => state.error
  },
  
  actions: {
    async loadNotifications({ commit }, params = {}) {
      try {
        commit('SET_LOADING', true);
        
        const response = await apiService.notifications.getAll(params);
        commit('SET_NOTIFICATIONS', response.data);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al cargar notificaciones';
        commit('SET_ERROR', errorMessage);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async loadUnreadCount({ commit }) {
      try {
        const response = await apiService.notifications.getUnreadCount();
        commit('SET_UNREAD_COUNT', response.data.count);
        
        return response;
      } catch (error) {
        console.error('Error loading unread count:', error);
        throw error;
      }
    },
    
    async markAsRead({ commit }, notificationId) {
      try {
        const response = await apiService.notifications.markAsRead(notificationId);
        commit('MARK_AS_READ', notificationId);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al marcar como leída';
        commit('SET_ERROR', errorMessage);
        throw error;
      }
    },
    
    async markAllAsRead({ commit }) {
      try {
        const response = await apiService.notifications.markAllAsRead();
        commit('MARK_ALL_AS_READ');
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al marcar todas como leídas';
        commit('SET_ERROR', errorMessage);
        throw error;
      }
    },
    
    async deleteNotification({ commit }, notificationId) {
      try {
        const response = await apiService.notifications.delete(notificationId);
        commit('REMOVE_NOTIFICATION', notificationId);
        
        return response;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Error al eliminar notificación';
        commit('SET_ERROR', errorMessage);
        throw error;
      }
    },
    
    addNotification({ commit }, notification) {
      commit('ADD_NOTIFICATION', notification);
    },
    
    clearError({ commit }) {
      commit('CLEAR_ERROR');
    },
    
    reset({ commit }) {
      commit('RESET');
    }
  }
};