<template>
  <div class="notification-toast" :class="type" v-if="visible">
    <div class="toast-content">
      <div class="toast-icon">
        <i :class="iconClass"></i>
      </div>
      <div class="toast-message">
        <h6>{{ title }}</h6>
        <p>{{ message }}</p>
      </div>
      <button class="toast-close" @click="close">
        <i class="fas fa-times"></i>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotificationToast',
  props: {
    type: {
      type: String,
      default: 'info',
      validator: (value) => ['success', 'error', 'warning', 'info'].includes(value)
    },
    title: {
      type: String,
      default: ''
    },
    message: {
      type: String,
      default: ''
    },
    duration: {
      type: Number,
      default: 5000
    },
    show: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:show', 'close'],
  data() {
    return {
      visible: this.show,
      timeout: null
    };
  },
  computed: {
    iconClass() {
      const icons = {
        success: 'fas fa-check-circle',
        error: 'fas fa-exclamation-circle',
        warning: 'fas fa-exclamation-triangle',
        info: 'fas fa-info-circle'
      };
      return icons[this.type];
    }
  },
  watch: {
    show(newVal) {
      this.visible = newVal;
      if (newVal) {
        this.setAutoClose();
      }
    }
  },
  methods: {
    close() {
      this.visible = false;
      this.$emit('update:show', false);
      this.$emit('close');
      if (this.timeout) {
        clearTimeout(this.timeout);
      }
    },
    setAutoClose() {
      if (this.timeout) {
        clearTimeout(this.timeout);
      }
      this.timeout = setTimeout(() => {
        this.close();
      }, this.duration);
    }
  },
  mounted() {
    if (this.show) {
      this.setAutoClose();
    }
  },
  beforeUnmount() {
    if (this.timeout) {
      clearTimeout(this.timeout);
    }
  }
};
</script>

<style scoped>
.notification-toast {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1060;
  min-width: 300px;
  max-width: 400px;
  animation: toast-slide-in 0.3s ease;
}

.toast-content {
  background: white;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
  padding: 15px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.notification-toast.success .toast-content {
  border-left: 4px solid #198754;
}

.notification-toast.error .toast-content {
  border-left: 4px solid #dc3545;
}

.notification-toast.warning .toast-content {
  border-left: 4px solid #ffc107;
}

.notification-toast.info .toast-content {
  border-left: 4px solid #0dcaf0;
}

.toast-icon {
  font-size: 1.2rem;
  flex-shrink: 0;
}

.notification-toast.success .toast-icon {
  color: #198754;
}

.notification-toast.error .toast-icon {
  color: #dc3545;
}

.notification-toast.warning .toast-icon {
  color: #ffc107;
}

.notification-toast.info .toast-icon {
  color: #0dcaf0;
}

.toast-message {
  flex: 1;
}

.toast-message h6 {
  margin: 0 0 5px 0;
  font-weight: 600;
}

.toast-message p {
  margin: 0;
  font-size: 0.9rem;
  color: #6c757d;
}

.toast-close {
  background: none;
  border: none;
  color: #6c757d;
  cursor: pointer;
  padding: 0;
  font-size: 0.9rem;
  flex-shrink: 0;
}

.toast-close:hover {
  color: #212529;
}

@keyframes toast-slide-in {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@media (max-width: 768px) {
  .notification-toast {
    top: 10px;
    right: 10px;
    left: 10px;
    min-width: auto;
    max-width: none;
  }
}
</style>