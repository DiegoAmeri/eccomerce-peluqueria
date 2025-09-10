<template>
  <div class="forgot-password-view">
    <h2>Recuperar Contraseña</h2>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>Email</label>
        <input type="email" v-model="email" required placeholder="Tu email registrado" />
      </div>
      <button type="submit" class="btn-primary">Enviar enlace de recuperación</button>
    </form>
    <router-link to="/auth/login" class="back-link">← Volver al login</router-link>
  </div>
</template>

<script>
export default {
  name: 'ForgotPasswordView',
  data() {
    return {
      email: ''
    }
  },
  methods: {
    handleSubmit() {
      console.log('Sending recovery email to:', this.email)
      // Aquí implementarías la lógica de recuperación
    }
  }
}
</script>

<style scoped>
.forgot-password-view {
  text-align: center;
}

.form-group {
  margin-bottom: 20px;
  text-align: left;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
}

.back-link {
  display: block;
  margin-top: 20px;
  color: var(--primary-color);
  text-decoration: none;
}
</style>