<template>
  <div class="dashboard-view">
    <SideBar />
    <div class="main-content">
      <NavBar />
      <div class="content-wrapper">
        <OwnerDashboard />
      </div>
    </div>
  </div>
</template>

<script>
import SideBar from '@/components/common/SideBar.vue'
import NavBar from '@/components/common/NavBar.vue'
import OwnerDashboard from '@/components/owner/OwnerDashboard.vue'

export default {
  name: 'OwnerDashboardView',
  components: {
    SideBar,
    NavBar,
    OwnerDashboard
  },
  beforeCreate() {
    // Verificar autenticación y rol
    if (!this.$store.getters.isAuthenticated || this.$store.getters.userRole !== 'owner') {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.dashboard-view {
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