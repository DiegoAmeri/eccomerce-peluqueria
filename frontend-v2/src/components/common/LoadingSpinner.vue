<template>
  <div class="loading-spinner" :class="size">
    <div class="spinner"></div>
    <p v-if="message" class="message">{{ message }}</p>
  </div>
</template>

<script>
export default {
  name: 'LoadingSpinner',
  props: {
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value)
    },
    message: {
      type: String,
      default: ''
    }
  }
};
</script>

<style scoped>
.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.spinner {
  border: 3px solid #f3f3f3;
  border-top: 3px solid #6f42c1;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-spinner.small .spinner {
  width: 20px;
  height: 20px;
}

.loading-spinner.medium .spinner {
  width: 40px;
  height: 40px;
}

.loading-spinner.large .spinner {
  width: 60px;
  height: 60px;
}

.message {
  margin-top: 10px;
  color: #6c757d;
  text-align: center;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>