<template>
  <div class="client-dashboard">
    <div class="welcome-section">
      <h1>¡Bienvenido de vuelta, {{ userName }}!</h1>
      <p>¿Qué servicio estás buscando hoy?</p>
    </div>

    <div class="quick-actions">
      <div class="row">
        <div class="col-md-4 mb-3">
          <div class="action-card" @click="$router.push('/salons')">
            <i class="fas fa-store"></i>
            <h4>Buscar Peluquerías</h4>
            <p>Encuentra los mejores salones cerca de ti</p>
          </div>
        </div>
        <div class="col-md-4 mb-3">
          <div class="action-card" @click="$router.push('/my-appointments')">
            <i class="fas fa-calendar"></i>
            <h4>Mis Turnos</h4>
            <p>Gestiona tus reservas y citas</p>
          </div>
        </div>
        <div class="col-md-4 mb-3">
          <div class="action-card" @click="$router.push('/favorites')">
            <i class="fas fa-heart"></i>
            <h4>Favoritos</h4>
            <p>Tus peluquerías preferidas</p>
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <!-- Próximos turnos -->
      <div class="col-md-8">
        <div class="card">
          <div class="card-header">
            <h5>Próximos Turnos</h5>
            <router-link to="/my-appointments" class="btn btn-sm btn-outline-primary">
              Ver todos
            </router-link>
          </div>
          <div class="card-body">
            <div v-if="loading" class="text-center py-4">
              <LoadingSpinner />
            </div>
            <div v-else-if="upcomingAppointments.length === 0" class="text-center py-4">
              <i class="fas fa-calendar-times fa-2x text-muted"></i>
              <p class="mt-2">No tienes turnos programados</p>
              <button class="btn btn-primary" @click="$router.push('/salons')">
                Reservar ahora
              </button>
            </div>
            <div v-else>
              <AppointmentCard 
                v-for="appointment in upcomingAppointments" 
                :key="appointment.id" 
                :appointment="appointment" 
              />
            </div>
          </div>
        </div>
      </div>

      <!-- Recomendaciones -->
      <div class="col-md-4">
        <div class="card mb-4">
          <div class="card-header">
            <h5>Recomendados para ti</h5>
          </div>
          <div class="card-body">
            <div v-if="recommendedSalons.length === 0" class="text-center py-3">
              <p class="text-muted">Busca peluquerías para ver recomendaciones</p>
            </div>
            <div v-else>
              <SalonCard 
                v-for="salon in recommendedSalons.slice(0, 2)" 
                :key="salon.id" 
                :salon="salon" 
                compact 
              />
            </div>
          </div>
        </div>

        <!-- Estadísticas rápidas -->
        <div class="card">
          <div class="card-header">
            <h5>Tu Actividad</h5>
          </div>
          <div class="card-body">
            <div class="stat-item">
              <i class="fas fa-calendar-check text-primary"></i>
              <div>
                <div class="stat-value">{{ totalAppointments }}</div>
                <div class="stat-label">Turnos totales</div>
              </div>
            </div>
            <div class="stat-item">
              <i class="fas fa-heart text-danger"></i>
              <div>
                <div class="stat-value">{{ favoriteSalonsCount }}</div>
                <div class="stat-label">Favoritos</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import { useStore } from 'vuex';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import AppointmentCard from '@/components/client/AppointmentCard.vue';
import SalonCard from '@/components/client/SalonCard.vue';

export default {
  name: 'ClientDashboard',
  components: {
    LoadingSpinner,
    AppointmentCard,
    SalonCard
  },
  setup() {
    const store = useStore();
    const loading = ref(true);
    const upcomingAppointments = ref([]);
    const recommendedSalons = ref([]);

    const userName = computed(() => store.getters.userName);
    const totalAppointments = computed(() => store.getters.totalAppointments);
    const favoriteSalonsCount = computed(() => store.getters.favoriteSalonsCount);

    const loadDashboardData = async () => {
      try {
        loading.value = true;
        
        // Simular carga de datos (en producción vendría de la API)
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Datos de ejemplo
        upcomingAppointments.value = [
          {
            id: 1,
            salonName: "Estilo Urbano",
            service: "Corte de Cabello",
            date: new Date(Date.now() + 86400000), // Mañana
            time: "10:00",
            status: "confirmed",
            price: 500
          },
          {
            id: 2,
            salonName: "Corte Perfecto",
            service: "Barba",
            date: new Date(Date.now() + 172800000), // Pasado mañana
            time: "14:30",
            status: "pending",
            price: 300
          }
        ];

        recommendedSalons.value = [
          {
            id: 1,
            name: "Estilo Urbano",
            image: "https://images.unsplash.com/photo-1585747861815-0468562f7d4f?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80",
            address: "Av. Siempre Viva 742",
            rating: 4.8,
            distance: "1.2 km",
            services: ["Corte", "Barba", "Tintura"]
          },
          {
            id: 2,
            name: "Corte Perfecto",
            image: "https://images.unsplash.com/photo-1562322140-8baeececf3df?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80",
            address: "Calle Falsa 123",
            rating: 4.5,
            distance: "0.8 km",
            services: ["Corte", "Alisado", "Manicura"]
          }
        ];

      } catch (error) {
        console.error('Error loading dashboard data:', error);
      } finally {
        loading.value = false;
      }
    };

    onMounted(() => {
      loadDashboardData();
    });

    return {
      userName,
      loading,
      upcomingAppointments,
      recommendedSalons,
      totalAppointments,
      favoriteSalonsCount
    };
  }
};
</script>

<style scoped>
.client-dashboard {
  padding: 20px;
}

.welcome-section {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  color: white;
  border-radius: 15px;
}

.welcome-section h1 {
  font-weight: 700;
  margin-bottom: 10px;
}

.action-card {
  background: white;
  padding: 25px;
  border-radius: 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  height: 100%;
}

.action-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.action-card i {
  font-size: 2.5rem;
  color: #6f42c1;
  margin-bottom: 15px;
}

.action-card h4 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.action-card p {
  color: #6c757d;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f1f3f4;
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-item i {
  font-size: 1.5rem;
  margin-right: 15px;
  width: 40px;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2c3e50;
}

.stat-label {
  color: #6c757d;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .client-dashboard {
    padding: 15px;
  }
  
  .welcome-section {
    padding: 15px;
  }
  
  .action-card {
    padding: 20px;
  }
}
</style>