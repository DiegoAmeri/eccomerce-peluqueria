<template>
  <div class="auth-view">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-4">
          <LoginForm />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import LoginForm from '@/components/auth/LoginForm.vue'

export default {
  name: 'LoginView',
  components: {
    LoginForm
  },
  beforeCreate() {
    // Redirigir si ya está autenticado
    if (this.$store.getters.isAuthenticated) {
      this.$router.push('/dashboard')
    }
  }
}
</script>

<style scoped>
.auth-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  padding: 20px;
}
</style>