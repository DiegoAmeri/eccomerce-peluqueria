<template>
  <div class="client-dashboard">
    <!-- Welcome Section -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h2>¡Hola, {{ userName }}! 👋</h2>
        <p class="text-muted">¿Qué te gustaría hacer hoy?</p>
      </div>
      <div class="welcome-actions">
        <button class="btn btn-primary" @click="bookAppointment">
          <i class="fas fa-calendar-plus me-2"></i>
          Reservar Turno
        </button>
      </div>
    </div>

    <!-- Statistics Grid -->
    <div class="stats-grid">
      <div class="row">
        <div class="col-xl-3 col-md-6 mb-4">
          <StatCard 
            layout="icon"
            :value="upcomingAppointments"
            label="Próximos Turnos"
            icon="fas fa-calendar-check"
            icon-color="#6f42c1"
            icon-bg-color="rgba(111, 66, 193, 0.1)"
            :trend="appointmentsTrend"
            theme="primary"
            clickable
            @click="$router.push('/client/appointments')"
          />
        </div>
        <div class="col-xl-3 col-md-6 mb-4">
          <StatCard 
            layout="icon"
            :value="favoriteSalons"
            label="Favoritos"
            icon="fas fa-heart"
            icon-color="#dc3545"
            icon-bg-color="rgba(220, 53, 69, 0.1)"
            theme="danger"
            clickable
            @click="$router.push('/client/favorites')"
          />
        </div>
        <div class="col-xl-3 col-md-6 mb-4">
          <StatCard 
            layout="icon"
            :value="completedAppointments"
            label="Completados"
            icon="fas fa-check-circle"
            icon-color="#20c997"
            icon-bg-color="rgba(32, 201, 151, 0.1)"
            theme="success"
          />
        </div>
        <div class="col-xl-3 col-md-6 mb-4">
          <StatCard 
            layout="icon"
            :value="averageRating"
            label="Rating Promedio"
            icon="fas fa-star"
            icon-color="#ffc107"
            icon-bg-color="rgba(255, 193, 7, 0.1)"
            format="decimal"
            :decimals="1"
            theme="warning"
          />
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="row">
      <!-- Left Column -->
      <div class="col-lg-8">
        <!-- Upcoming Appointments -->
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="mb-0">Próximos Turnos</h5>
          </div>
          <div class="card-body">
            <div v-if="loadingAppointments" class="text-center py-4">
              <LoadingSpinner message="Cargando turnos..." />
            </div>
            <div v-else-if="appointments.length === 0" class="text-center py-4">
              <i class="fas fa-calendar-times fa-3x text-muted mb-3"></i>
              <h5>No tienes turnos programados</h5>
              <p class="text-muted">¡Reserva tu primer turno ahora!</p>
              <button class="btn btn-primary mt-2" @click="bookAppointment">
                Reservar Turno
              </button>
            </div>
            <div v-else>
              <div v-for="appointment in appointments" :key="appointment.id" class="appointment-item">
                <AppointmentCard 
                  :appointment="appointment"
                  @view-details="viewAppointmentDetails"
                  @confirm="confirmAppointment"
                  @cancel="cancelAppointment"
                />
              </div>
            </div>
          </div>
          <div class="card-footer" v-if="appointments.length > 0">
            <router-link to="/client/appointments" class="btn btn-link w-100 text-decoration-none">
              Ver todos los turnos
            </router-link>
          </div>
        </div>

        <!-- Recent Activity -->
        <div class="card">
          <div class="card-header">
            <h5 class="mb-0">Actividad Reciente</h5>
          </div>
          <div class="card-body">
            <div class="timeline">
              <div v-for="activity in recentActivities" :key="activity.id" class="timeline-item">
                <div class="timeline-icon">
                  <i :class="activity.icon"></i>
                </div>
                <div class="timeline-content">
                  <h6>{{ activity.title }}</h6>
                  <p>{{ activity.description }}</p>
                  <span class="timeline-time">{{ activity.time }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column -->
      <div class="col-lg-4">
        <!-- Favorite Salons -->
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="mb-0">Peluquerías Favoritas</h5>
          </div>
          <div class="card-body">
            <FavoriteSalons />
          </div>
        </div>

        <!-- Quick Actions -->
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="mb-0">Acciones Rápidas</h5>
          </div>
          <div class="card-body">
            <div class="quick-actions">
              <button class="quick-action-btn" @click="bookAppointment">
                <i class="fas fa-calendar-plus"></i>
                <span>Reservar Turno</span>
              </button>
              <button class="quick-action-btn" @click="$router.push('/client/salons')">
                <i class="fas fa-store"></i>
                <span>Buscar Peluquerías</span>
              </button>
              <button class="quick-action-btn" @click="$router.push('/client/favorites')">
                <i class="fas fa-heart"></i>
                <span>Ver Favoritos</span>
              </button>
              <button class="quick-action-btn" @click="$router.push('/client/profile')">
                <i class="fas fa-user-edit"></i>
                <span>Editar Perfil</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Special Offers -->
        <div class="card">
          <div class="card-header">
            <h5 class="mb-0">Ofertas Especiales</h5>
          </div>
          <div class="card-body">
            <div class="offers-list">
              <div v-for="offer in specialOffers" :key="offer.id" class="offer-item">
                <div class="offer-badge">-{{ offer.discount }}%</div>
                <div class="offer-content">
                  <h6>{{ offer.title }}</h6>
                  <p class="offer-description">{{ offer.description }}</p>
                  <div class="offer-meta">
                    <span class="offer-salon">{{ offer.salon }}</span>
                    <span class="offer-valid">Válido hasta {{ offer.validUntil }}</span>
                  </div>
                </div>
                <button class="btn btn-sm btn-outline-primary" @click="viewOffer(offer)">
                  Ver
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading Overlay -->
    <div v-if="loading" class="loading-overlay">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
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
    const router = useRouter();
    const store = useStore();
    
    const loading = ref(true);
    const loadingAppointments = ref(true);
    
    const userName = computed(() => store.getters.userName);
    
    const upcomingAppointments = ref(3);
    const favoriteSalons = ref(2);
    const completedAppointments = ref(12);
    const averageRating = ref(4.8);
    const appointmentsTrend = ref(25);
    
    const appointments = ref([
      {
        id: 1,
        salonName: "Estilo Urbano",
        salonImage: "https://images.unsplash.com/photo-1585747861815-0468562f7d4f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        service: "Corte + Barba",
        duration: 45,
        date: new Date(Date.now() + 86400000), // Mañana
        time: "10:00",
        status: "confirmed",
        statusText: "Confirmado",
        price: 700,
        barberName: "Carlos Rodríguez",
        barberSpecialty: "Especialista en cortes modernos"
      },
      {
        id: 2,
        salonName: "Corte Perfecto",
        salonImage: "https://images.unsplash.com/photo-1562322140-8baeececf3df?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        service: "Tintura",
        duration: 120,
        date: new Date(Date.now() + 172800000), // En 2 días
        time: "14:30",
        status: "pending",
        statusText: "Pendiente",
        price: 1200,
        barberName: "Ana Martínez",
        barberSpecialty: "Especialista en color"
      }
    ]);
    
    const recentActivities = ref([
      {
        id: 1,
        icon: "fas fa-calendar-check text-success",
        title: "Turno Confirmado",
        description: "Tu turno en Estilo Urbano ha sido confirmado",
        time: "Hace 2 horas"
      },
      {
        id: 2,
        icon: "fas fa-star text-warning",
        title: "Reseña Publicada",
        description: "Has calificado tu experiencia en Corte Perfecto",
        time: "Hace 1 día"
      },
      {
        id: 3,
        icon: "fas fa-heart text-danger",
        title: "Peluquería Favorita",
        description: "Has agregado Belleza Integral a tus favoritos",
        time: "Hace 2 días"
      },
      {
        id: 4,
        icon: "fas fa-money-bill-wave text-info",
        title: "Pago Procesado",
        description: "Pago realizado por servicio de manicura",
        time: "Hace 3 días"
      }
    ]);
    
    const specialOffers = ref([
      {
        id: 1,
        discount: 20,
        title: "Corte + Lavado Gratis",
        description: "Disfruta de un corte profesional con lavado incluido",
        salon: "Estilo Urbano",
        validUntil: "25/08/2023"
      },
      {
        id: 2,
        discount: 15,
        title: "Promo Barba Completa",
        description: "Arreglo de barba con tratamiento premium",
        salon: "Corte Perfecto",
        validUntil: "31/08/2023"
      }
    ]);
    
    const loadDashboardData = async () => {
      try {
        loading.value = true;
        loadingAppointments.value = true;
        
        // Simular carga de datos
        await new Promise(resolve => setTimeout(resolve, 1500));
        
        // Aquí irían las llamadas reales a la API
        // const dashboardData = await api.get('/client/dashboard');
        // Actualizar los datos con la respuesta
        
      } catch (error) {
        console.error('Error loading dashboard data:', error);
      } finally {
        loading.value = false;
        loadingAppointments.value = false;
      }
    };
    
    const bookAppointment = () => {
      router.push('/client/salons');
    };
    
    const viewAppointmentDetails = (appointmentId) => {
      router.push(`/client/appointments/${appointmentId}`);
    };
    
    const confirmAppointment = async (appointmentId) => {
      // Lógica para confirmar turno
      console.log('Confirmar turno:', appointmentId);
    };
    
    const cancelAppointment = async (appointmentId) => {
      // Lógica para cancelar turno
      console.log('Cancelar turno:', appointmentId);
    };
    
    const viewOffer = (offer) => {
      router.push('/client/salons?offer=' + offer.id);
    };
    
    onMounted(() => {
      loadDashboardData();
    });
    
    return {
      loading,
      loadingAppointments,
      userName,
      upcomingAppointments,
      favoriteSalons,
      completedAppointments,
      averageRating,
      appointmentsTrend,
      appointments,
      recentActivities,
      specialOffers,
      bookAppointment,
      viewAppointmentDetails,
      confirmAppointment,
      cancelAppointment,
      viewOffer
    };
  }
};
</script>

<style scoped>
.client-dashboard {
  padding: 20px;
}

.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 25px;
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  color: white;
  border-radius: 16px;
}

.welcome-content h2 {
  margin: 0;
  font-weight: 700;
  font-size: 1.8rem;
}

.welcome-content p {
  margin: 8px 0 0 0;
  opacity: 0.9;
}

.stats-grid {
  margin-bottom: 30px;
}

.appointment-item {
  margin-bottom: 15px;
}

.appointment-item:last-child {
  margin-bottom: 0;
}

.timeline {
  position: relative;
  padding-left: 30px;
}

.timeline-item {
  position: relative;
  padding: 15px 0;
  border-left: 2px solid #e9ecef;
}

.timeline-item:last-child {
  border-left: 2px solid transparent;
}

.timeline-icon {
  position: absolute;
  left: -36px;
  top: 15px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #e9ecef;
}

.timeline-content {
  margin-left: 20px;
}

.timeline-content h6 {
  margin: 0 0 5px 0;
  font-weight: 600;
  color: #2c3e50;
}

.timeline-content p {
  margin: 0 0 8px 0;
  color: #6c757d;
  font-size: 0.9rem;
}

.timeline-time {
  font-size: 0.8rem;
  color: #6c757d;
}

.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.quick-action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
  text-align: center;
}

.quick-action-btn:hover {
  background: #6f42c1;
  color: white;
  transform: translateY(-2px);
  border-color: #6f42c1;
}

.quick-action-btn i {
  font-size: 1.5rem;
  margin-bottom: 8px;
}

.quick-action-btn span {
  font-weight: 500;
  font-size: 0.9rem;
}

.offers-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.offer-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e9ecef;
  position: relative;
}

.offer-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #dc3545;
  color: white;
  padding: 4px 8px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 700;
}

.offer-content {
  flex: 1;
  margin-right: 15px;
}

.offer-content h6 {
  margin: 0 0 5px 0;
  font-weight: 600;
  color: #2c3e50;
}

.offer-description {
  margin: 0 0 8px 0;
  color: #6c757d;
  font-size: 0.9rem;
}

.offer-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.offer-salon {
  font-size: 0.8rem;
  color: #6f42c1;
  font-weight: 500;
}

.offer-valid {
  font-size: 0.8rem;
  color: #6c757d;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

@media (max-width: 768px) {
  .client-dashboard {
    padding: 15px;
  }
  
  .welcome-section {
    flex-direction: column;
    text-align: center;
    gap: 15px;
    padding: 20px;
  }
  
  .welcome-content h2 {
    font-size: 1.5rem;
  }
  
  .quick-actions {
    grid-template-columns: 1fr;
  }
  
  .offer-item {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
  
  .offer-content {
    margin-right: 0;
    margin-bottom: 12px;
  }
  
  .offer-meta {
    flex-direction: column;
    gap: 6px;
  }
}

@media (max-width: 576px) {
  .stats-grid .row {
    margin: 0 -5px;
  }
  
  .stats-grid .col-xl-3,
  .stats-grid .col-md-6 {
    padding: 0 5px;
  }
  
  .timeline {
    padding-left: 20px;
  }
  
  .timeline-icon {
    left: -26px;
    width: 20px;
    height: 20px;
  }
  
  .timeline-icon i {
    font-size: 0.8rem;
  }
}
</style>