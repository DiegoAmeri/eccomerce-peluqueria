<template>
  <div class="availability-view">
    <SideBar />
    <div class="main-content">
      <NavBar />
      <div class="content-wrapper">
        <div class="card">
          <div class="card-header">
            <h4 class="mb-0">Gestión de Disponibilidad</h4>
          </div>
          <div class="card-body">
            <AvailabilityCalendar />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import SideBar from '@/components/common/SideBar.vue'
import NavBar from '@/components/common/NavBar.vue'
import AvailabilityCalendar from '@/components/barber/AvailabilityCalendar.vue'

export default {
  name: 'AvailabilityView',
  components: {
    SideBar,
    NavBar,
    AvailabilityCalendar
  },
  beforeCreate() {
    // Verificar autenticación y rol
    if (!this.$store.getters.isAuthenticated || this.$store.getters.userRole !== 'barber') {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.availability-view {
  display: flex;
  min-height: 100vh;
  background-color: #f8f9fa;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.content-wrapper {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .content-wrapper {
    padding: 15px;
  }
}
</style>