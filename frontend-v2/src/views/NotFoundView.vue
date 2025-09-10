<template>
  <div class="not-found-view">
    <div class="error-container">
      <h1>404</h1>
      <h2>Página no encontrada</h2>
      <p>La página que buscas no existe o ha sido movida.</p>
      <router-link to="/" class="btn-primary">Volver al Inicio</router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFoundView'
}
</script>

<style scoped>
.not-found-view {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  text-align: center;
}

.error-container h1 {
  font-size: 6rem;
  color: var(--primary-color);
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
</style>