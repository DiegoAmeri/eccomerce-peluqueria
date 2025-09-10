<template>
  <div class="modal-backdrop" v-if="show" @click.self="close">
    <div class="modal" :class="size">
      <div class="modal-header">
        <h5>{{ title }}</h5>
        <button type="button" class="btn-close" @click="close" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <slot></slot>
      </div>
      <div class="modal-footer" v-if="showFooter">
        <button type="button" class="btn btn-secondary" @click="close">Cancelar</button>
        <button type="button" class="btn btn-primary" @click="confirm">{{ confirmText }}</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ModalDialog',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: ''
    },
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large', 'xlarge'].includes(value)
    },
    confirmText: {
      type: String,
      default: 'Confirmar'
    },
    showFooter: {
      type: Boolean,
      default: true
    }
  },
  emits: ['update:show', 'confirm', 'close'],
  methods: {
    close() {
      this.$emit('update:show', false);
      this.$emit('close');
    },
    confirm() {
      this.$emit('confirm');
      this.close();
    }
  },
  watch: {
    show(newVal) {
      if (newVal) {
        document.body.style.overflow = 'hidden';
      } else {
        document.body.style.overflow = '';
      }
    }
  }
};
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
  padding: 20px;
}

.modal {
  background: white;
  border-radius: 15px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  animation: modal-appear 0.3s ease;
}

.modal.small {
  width: 400px;
  max-width: 100%;
}

.modal.medium {
  width: 600px;
  max-width: 100%;
}

.modal.large {
  width: 800px;
  max-width: 100%;
}

.modal.xlarge {
  width: 1000px;
  max-width: 100%;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #dee2e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h5 {
  margin: 0;
  color: #212529;
}

.btn-close {
  background: transparent;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  opacity: 0.7;
}

.btn-close:hover {
  opacity: 1;
}

.modal-body {
  padding: 20px;
  max-height: 70vh;
  overflow-y: auto;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #dee2e6;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@keyframes modal-appear {
  from {
    opacity: 0;
    transform: translateY(-50px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@media (max-width: 768px) {
  .modal-backdrop {
    padding: 10px;
  }
  
  .modal.small,
  .modal.medium,
  .modal.large,
  .modal.xlarge {
    width: 100%;
    margin: 10px;
  }
  
  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 15px;
  }
}
</style>