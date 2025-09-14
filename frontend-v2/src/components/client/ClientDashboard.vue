<template>
  <div class="dashboard">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h3>Dashboard</h3>
      <button class="btn btn-primary" @click="$router.push('/client/salons')">
        <i class="fas fa-plus me-1"></i> Reservar Turno
      </button>
    </div>

    <div class="row mb-4">
      <div class="col-md-3">
        <StatCard icon="fas fa-calendar-check" :number="upcomingAppointments" 
                  description="Próximos Turnos" color="primary" />
      </div>
      <div class="col-md-3">
        <StatCard icon="fas fa-history" :number="completedAppointments" 
                  description="Turnos Completados" color="success" />
      </div>
      <div class="col-md-3">
        <StatCard icon="fas fa-star" :number="averageRating" 
                  description="Calificación Promedio" color="warning" />
      </div>
      <div class="col-md-3">
        <StatCard icon="fas fa-heart" :number="favoriteSalons" 
                  description="Favoritos" color="danger" />
      </div>
    </div>

    <div class="row">
      <div class="col-md-8">
        <div class="card">
          <div class="card-header d-flex justify-content-between align-items-center">
            <span>Próximos Turnos</span>
            <span class="badge bg-primary">{{ currentDate }}</span>
          </div>
          <div class="card-body">
            <div v-if="loading" class="text-center py-4">
              <LoadingSpinner />
              <p class="mt-2">Cargando turnos...</p>
            </div>
            <div v-else-if="appointments.length === 0" class="text-center py-4">
              <i class="fas fa-calendar-times fa-2x text-muted"></i>
              <p class="mt-2">No tienes turnos programados</p>
              <button class="btn btn-primary mt-2" @click="$router.push('/client/salons')">
                Reservar mi primer turno
              </button>
            </div>
            <div v-else>
              <AppointmentCard 
                v-for="appointment in appointments" 
                :key="appointment.id" 
                :appointment="appointment" 
              />
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-4">
        <FavoriteSalons />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useStore } from 'vuex';
import StatCard from '@/components/common/StatCard.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import AppointmentCard from '@/components/client/AppointmentCard.vue';
import FavoriteSalons from '@/components/client/FavoriteSalons.vue';

export default {
  name: 'ClientDashboard',
  components: {
    StatCard,
    LoadingSpinner,
    AppointmentCard,
    FavoriteSalons
  },
  setup() {
    const store = useStore();
    
    const upcomingAppointments = ref(0);
    const completedAppointments = ref(0);
    const averageRating = ref(0);
    const favoriteSalons = ref(0);
    const appointments = ref([]);
    const loading = ref(true);
    const currentDate = ref('');
    
    onMounted(async () => {
      try {
        await store.dispatch('fetchClientDashboard');
        const dashboardData = store.getters.clientDashboard;
        
        upcomingAppointments.value = dashboardData.upcomingAppointments;
        completedAppointments.value = dashboardData.completedAppointments;
        averageRating.value = dashboardData.averageRating;
        favoriteSalons.value = dashboardData.favoriteSalons;
        appointments.value = dashboardData.appointments;
        
        // Formatear fecha actual
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        currentDate.value = new Date().toLocaleDateString('es-ES', options);
      } catch (error) {
        console.error('Error loading dashboard data:', error);
      } finally {
        loading.value = false;
      }
    });
    
    return {
      upcomingAppointments,
      completedAppointments,
      averageRating,
      favoriteSalons,
      appointments,
      loading,
      currentDate
    };
  }
};
</script>