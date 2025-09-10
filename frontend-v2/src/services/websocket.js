import { useStore } from 'vuex';

class WebSocketService {
  constructor() {
    this.socket = null;
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 5;
    this.reconnectInterval = 3000;
    this.store = useStore();
  }

  connect() {
    if (this.socket) return;

    const token = this.store.getters['auth/token'];
    if (!token) return;

    try {
      const wsUrl = process.env.VUE_APP_WS_URL || 'ws://localhost:8080/ws';
      this.socket = new WebSocket(`${wsUrl}?token=${token}`);

      this.socket.onopen = () => {
        console.log('WebSocket connected');
        this.reconnectAttempts = 0;
        this.store.dispatch('notification/loadUnreadCount');
      };

      this.socket.onmessage = (event) => {
        this.handleMessage(JSON.parse(event.data));
      };

      this.socket.onclose = () => {
        console.log('WebSocket disconnected');
        this.handleReconnect();
      };

      this.socket.onerror = (error) => {
        console.error('WebSocket error:', error);
      };

    } catch (error) {
      console.error('WebSocket connection failed:', error);
    }
  }

  handleMessage(message) {
    switch (message.type) {
      case 'NOTIFICATION':
        this.store.dispatch('notification/addNotification', message.data);
        this.store.dispatch('notification/loadUnreadCount');
        break;
      
      case 'APPOINTMENT_UPDATE':
        this.store.dispatch('appointment/updateAppointment', message.data);
        break;
      
      case 'APPOINTMENT_CREATED':
        this.store.dispatch('appointment/addAppointment', message.data);
        break;
      
      case 'APPOINTMENT_CANCELLED':
        this.store.dispatch('appointment/removeAppointment', message.data.id);
        break;
      
      default:
        console.log('Unknown message type:', message.type);
    }
  }

  handleReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      setTimeout(() => {
        console.log(`Reconnecting... Attempt ${this.reconnectAttempts}`);
        this.connect();
      }, this.reconnectInterval);
    }
  }

  disconnect() {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  }

  send(message) {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify(message));
    }
  }
}

// Singleton instance
let instance = null;

export const useWebSocket = () => {
  if (!instance) {
    instance = new WebSocketService();
  }
  return instance;
};

export default useWebSocket;