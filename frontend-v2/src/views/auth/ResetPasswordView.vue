<template>
  <div class="reset-password-view">
    <h2>Restablecer Contraseña</h2>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>Nueva Contraseña</label>
        <input type="password" v-model="password" required placeholder="Nueva contraseña" />
      </div>
      <div class="form-group">
        <label>Confirmar Contraseña</label>
        <input type="password" v-model="confirmPassword" required placeholder="Confirmar contraseña" />
      </div>
      <button type="submit" class="btn-primary">Restablecer Contraseña</button>
    </form>
  </div>
</template>

<script>
export default {
  name: 'ResetPasswordView',
  data() {
    return {
      password: '',
      confirmPassword: ''
    }
  },
  methods: {
    handleSubmit() {
      if (this.password !== this.confirmPassword) {
        alert('Las contraseñas no coinciden')
        return
      }
      console.log('Resetting password with token:', this.$route.params.token)
      // Aquí implementarías la lógica de restablecimiento
    }
  }
}
</script>

<style scoped>
.reset-password-view {
  text-align: center;
}
</style>