<template>
  <div class="server-error-view">
    <div class="error-container">
      <h1>500</h1>
      <h2>Error del Servidor</h2>
      <p>Algo salió mal en nuestro servidor. Por favor, intenta nuevamente.</p>
      <button @click="reloadPage" class="btn-primary">Reintentar</button>
      <router-link to="/" class="btn-secondary">Volver al Inicio</router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ServerErrorView',
  methods: {
    reloadPage() {
      window.location.reload()
    }
  }
}
</script>

<style scoped>
.server-error-view {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  text-align: center;
}

.error-container h1 {
  font-size: 6rem;
  color: var(--danger-color);
  margin: 0;
}

.error-container h2 {
  font-size: 2rem;
  margin: 10px 0;
}

.error-container p {
  color: var(--gray-color);
  margin-bottom: 30px;
}

.btn-secondary {
  margin-left: 10px;
  background: var(--gray-color);
}
</style>