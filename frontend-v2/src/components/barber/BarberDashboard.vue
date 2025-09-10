<template>
  <div class="dashboard">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h3>Dashboard</h3>
      <div>
        <button class="btn btn-outline-primary me-2" @click="syncData">
          <i class="fas fa-sync-alt me-1" :class="{ 'fa-spin': syncing }"></i> Sincronizar
        </button>
        <button class="btn btn-primary" @click="$router.push('/barber/appointments/new')">
          <i class="fas fa-plus me-1"></i> Nuevo Turno
        </button>
      </div>
    </div>

    <div class="row mb-4">
      <div class="col-md-3">
        <StatCard icon="fas fa-clock" :number="todayAppointments" 
                  description="Turnos Hoy" color="primary" />
      </div>
      <div class="col-md-3">
        <StatCard icon="fas fa-check-circle" :number="completedAppointments" 
                  description="Completados (Semana)" color="success" />
      </div>
      <div class="col-md-3">
        <StatCard icon="fas fa-star" :number="averageRating" 
                  description="Calificación Promedio" color="warning" />
      </div>
      <div class="col-md-3">
        <StatCard icon="fas fa-money-bill-wave" :number="weeklyEarnings" 
                  description="Ganancias (Semana)" color="info" />
      </div>
    </div>

    <div class="row">
      <div class="col-md-8">
        <div class="card">
          <div class="card-header d-flex justify-content-between align-items-center">
            <span>Turnos de Hoy</span>
            <span class="badge bg-primary">{{ currentDate }}</span>
          </div>
          <div class="card-body">
            <div v-if="loading" class="text-center py-4">
              <LoadingSpinner />
              <p class="mt-2">Cargando turnos...</p>
            </div>
            <div v-else-if="appointments.length === 0" class="text-center py-4">
              <i class="fas fa-calendar-times fa-2x text-muted"></i>
              <p class="mt-2">No tienes turnos programados para hoy</p>
            </div>
            <div v-else>
              <AppointmentItem 
                v-for="appointment in appointments" 
                :key="appointment.id" 
                :appointment="appointment" 
                @status-change="handleStatusChange"
              />
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-4">
        <div class="card mb-4">
          <div class="card-header">
            <span>Próximos Turnos</span>
          </div>
          <div class="card-body">
            <div v-if="upcomingLoading" class="text-center py-3">
              <LoadingSpinner />
            </div>
            <div v-else-if="upcomingAppointments.length === 0" class="text-center py-3">
              <p class="text-muted">No hay turnos próximos</p>
            </div>
            <div v-else>
              <div v-for="appointment in upcomingAppointments" :key="appointment.id" class="p-2 border-bottom">
                <div class="fw-bold">{{ formatDate(appointment.date) }} - {{ appointment.time }}</div>
                <div>{{ appointment.clientName }} - {{ appointment.service }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="card">
          <div class="card-header">
            <span>Reseñas Recientes</span>
          </div>
          <div class="card-body">
            <div v-if="reviewsLoading" class="text-center py-3">
              <LoadingSpinner />
            </div>
            <div v-else-if="reviews.length === 0" class="text-center py-3">
              <p class="text-muted">No hay reseñas aún</p>
            </div>
            <div v-else>
              <ReviewItem 
                v-for="review in reviews" 
                :key="review.id" 
                :review="review" 
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useStore } from 'vuex';
import StatCard from '@/components/common/StatCard.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import AppointmentItem from '@/components/barber/AppointmentItem.vue';
import ReviewItem from '@/components/barber/ReviewItem.vue';

export default {
  name: 'BarberDashboard',
  components: {
    StatCard,
    LoadingSpinner,
    AppointmentItem,
    ReviewItem
  },
  setup() {
    const store = useStore();
    
    const todayAppointments = ref(0);
    const completedAppointments = ref(0);
    const averageRating = ref(0);
    const weeklyEarnings = ref(0);
    const appointments = ref([]);
    const upcomingAppointments = ref([]);
    const reviews = ref([]);
    const loading = ref(true);
    const upcomingLoading = ref(true);
    const reviewsLoading = ref(true);
    const syncing = ref(false);
    const currentDate = ref('');
    
    const loadDashboardData = async () => {
      try {
        // En una app real, haríamos una llamada a la API
        // const response = await api.get('/barber/dashboard');
        // const data = response.data;
        
        // Simulamos datos de respuesta
        todayAppointments.value = 5;
        completedAppointments.value = 12;
        averageRating.value = 4.8;
        weeklyEarnings.value = 42500;
        
        appointments.value = [
          {
            id: 1,
            clientName: "Juan Pérez",
            clientImage: "https://ui-avatars.com/api/?name=Juan+Perez&background=6f42c1&color=fff",
            service: "Corte de Cabello",
            duration: 30,
            time: "09:00",
            status: "confirmed",
            statusText: "Confirmado"
          },
          {
            id: 2,
            clientName: "María García",
            clientImage: "https://ui-avatars.com/api/?name=Maria+Garcia&background=d63384&color=fff",
            service: "Tintura",
            duration: 120,
            time: "10:00",
            status: "confirmed",
            statusText: "Confirmado"
          },
          {
            id: 3,
            clientName: "Carlos López",
            clientImage: "https://ui-avatars.com/api/?name=Carlos+Lopez&background=20c997&color=fff",
            service: "Corte + Barba",
            duration: 45,
            time: "12:30",
            status: "pending",
            statusText: "Pendiente"
          }
        ];
        
        upcomingAppointments.value = [
          {
            id: 4,
            clientName: "Luis Martínez",
            service: "Corte",
            date: new Date(Date.now() + 86400000), // Mañana
            time: "09:00"
          },
          {
            id: 5,
            clientName: "Sofía Fernández",
            service: "Tintura",
            date: new Date(Date.now() + 86400000),
            time: "10:30"
          },
          {
            id: 6,
            clientName: "Roberto Sánchez",
            service: "Corte + Barba",
            date: new Date(Date.now() + 86400000),
            time: "11:45"
          }
        ];
        
        reviews.value = [
          {
            id: 1,
            clientName: "Juan Pérez",
            rating: 5,
            comment: "Excelente servicio, muy profesional!",
            date: new Date(Date.now() - 86400000) // Ayer
          },
          {
            id: 2,
            clientName: "María García",
            rating: 4.5,
            comment: "Muy contenta con el color, volveré pronto.",
            date: new Date(Date.now() - 172800000) // Hace 2 días
          }
        ];
        
        // Formatear fecha actual
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        currentDate.value = new Date().toLocaleDateString('es-ES', options);
      } catch (error) {
        console.error('Error loading dashboard data:', error);
      } finally {
        loading.value = false;
        upcomingLoading.value = false;
        reviewsLoading.value = false;
      }
    };
    
    const syncData = async () => {
      syncing.value = true;
      try {
        // En una app real, haríamos una llamada a la API para sincronizar
        // await api.post('/barber/sync');
        await new Promise(resolve => setTimeout(resolve, 1000)); // Simular delay
        await loadDashboardData();
        alert('Datos sincronizados correctamente!');
      } catch (error) {
        console.error('Error syncing data:', error);
        alert('Error al sincronizar los datos');
      } finally {
        syncing.value = false;
      }
    };
    
    const handleStatusChange = (appointmentId, newStatus) => {
      // En una app real, haríamos una llamada a la API para actualizar el estado
      console.log(`Cambiando estado del turno ${appointmentId} a ${newStatus}`);
      // Actualizar el estado localmente
      const appointment = appointments.value.find(a => a.id === appointmentId);
      if (appointment) {
        appointment.status = newStatus;
        appointment.statusText = newStatus === 'confirmed' ? 'Confirmado' : 
                                newStatus === 'completed' ? 'Completado' : 'Cancelado';
      }
    };
    
    const formatDate = (date) => {
      return new Date(date).toLocaleDateString('es-ES', { 
        weekday: 'short', 
        day: 'numeric', 
        month: 'short' 
      });
    };
    
    onMounted(() => {
      loadDashboardData();
    });
    
    return {
      todayAppointments,
      completedAppointments,
      averageRating,
      weeklyEarnings,
      appointments,
      upcomingAppointments,
      reviews,
      loading,
      upcomingLoading,
      reviewsLoading,
      syncing,
      currentDate,
      syncData,
      handleStatusChange,
      formatDate
    };
  }
};
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

@media (max-width: 768px) {
  .dashboard {
    padding: 15px;
  }
}
</style>