<template>
  <div class="barber-dashboard">
    <!-- Welcome Section -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h2>¡Buen día, {{ userName }}! ✂️</h2>
        <p class="text-muted">Prepárate para un gran día de trabajo</p>
        <div class="welcome-stats">
          <span class="stat-badge">
            <i class="fas fa-clock"></i>
            {{ todayAppointments }} turnos hoy
          </span>
          <span class="stat-badge" :class="statusClass">
            <i class="fas fa-circle"></i>
            {{ statusText }}
          </span>
        </div>
      </div>
      <div class="welcome-actions">
        <button class="btn btn-primary" @click="changeStatus">
          <i class="fas fa-power-off me-2"></i>
          {{ statusButtonText }}
        </button>
      </div>
    </div>

    <!-- Statistics Grid -->
    <div class="stats-grid">
      <div class="row">
        <div class="col-xl-3 col-md-6 mb-4">
          <StatCard 
            layout="icon"
            :value="todayAppointments"
            label="Turnos Hoy"
            icon="fas fa-calendar-day"
            icon-color="#6f42c1"
            icon-bg-color="rgba(111, 66, 193, 0.1)"
            theme="primary"
            clickable
            @click="$router.push('/barber/appointments')"
          />
        </div>
        <div class="col-xl-3 col-md-6 mb-4">
          <StatCard 
            layout="icon"
            :value="weeklyEarnings"
            label="Ganancias Semanales"
            icon="fas fa-money-bill-wave"
            icon-color="#20c997"
            icon-bg-color="rgba(32, 201, 151, 0.1)"
            format="currency"
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
        <div class="col-xl-3 col-md-6 mb-4">
          <StatCard 
            layout="icon"
            :value="clientSatisfaction"
            label="Satisfacción Clientes"
            icon="fas fa-smile"
            icon-color="#dc3545"
            icon-bg-color="rgba(220, 53, 69, 0.1)"
            format="percent"
            theme="danger"
          />
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="row">
      <!-- Left Column -->
      <div class="col-lg-8">
        <!-- Today's Schedule -->
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="mb-0">Agenda de Hoy</h5>
            <span class="badge bg-primary">{{ currentDate }}</span>
          </div>
          <div class="card-body">
            <div v-if="loadingSchedule" class="text-center py-4">
              <LoadingSpinner message="Cargando agenda..." />
            </div>
            <div v-else-if="todaysAppointments.length === 0" class="text-center py-4">
              <i class="fas fa-calendar-times fa-3x text-muted mb-3"></i>
              <h5>No hay turnos programados para hoy</h5>
              <p class="text-muted">¡Disfruta de un día tranquilo!</p>
            </div>
            <div v-else>
              <div class="schedule-timeline">
                <div v-for="appointment in todaysAppointments" :key="appointment.id" 
                     class="timeline-item">
                  <div class="timeline-time">
                    {{ appointment.time }}
                  </div>
                  <div class="timeline-content" :class="appointment.status">
                    <div class="appointment-info">
                      <div class="client-info">
                        <img :src="appointment.clientAvatar" :alt="appointment.clientName" class="client-avatar">
                        <div>
                          <h6>{{ appointment.clientName }}</h6>
                          <p class="service-name">{{ appointment.service }}</p>
                        </div>
                      </div>
                      <div class="appointment-actions">
                        <span class="status-badge" :class="appointment.status">
                          {{ appointment.statusText }}
                        </span>
                        <div class="action-buttons">
                          <button class="btn btn-sm btn-outline-primary" 
                                  @click="viewAppointment(appointment.id)">
                            <i class="fas fa-eye"></i>
                          </button>
                          <button class="btn btn-sm btn-outline-success" 
                                  @click="startAppointment(appointment.id)"
                                  v-if="appointment.status === 'confirmed'">
                            <i class="fas fa-play"></i>
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Performance Chart -->
        <div class="card">
          <div class="card-header">
            <h5 class="mb-0">Rendimiento Semanal</h5>
          </div>
          <div class="card-body">
            <div class="chart-container">
              <canvas ref="performanceChart" height="250"></canvas>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column -->
      <div class="col-lg-4">
        <!-- Quick Stats -->
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="mb-0">Estadísticas Rápidas</h5>
          </div>
          <div class="card-body">
            <div class="quick-stats">
              <div class="stat-item">
                <div class="stat-icon">
                  <i class="fas fa-users"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ totalClients }}</div>
                  <div class="stat-label">Clientes Totales</div>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon">
                  <i class="fas fa-scissors"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ servicesProvided }}</div>
                  <div class="stat-label">Servicios Realizados</div>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon">
                  <i class="fas fa-clock"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ totalHours }}</div>
                  <div class="stat-label">Horas Trabajadas</div>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon">
                  <i class="fas fa-trophy"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ rankingPosition }}°</div>
                  <div class="stat-label">Posición en Ranking</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Upcoming Appointments -->
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="mb-0">Próximos Turnos</h5>
          </div>
          <div class="card-body">
            <div v-if="upcomingAppointments.length === 0" class="text-center py-3">
              <p class="text-muted">No hay turnos próximos</p>
            </div>
            <div v-else>
              <div v-for="appointment in upcomingAppointments" :key="appointment.id" 
                   class="upcoming-item">
                <div class="upcoming-date">
                  <div class="upcoming-day">{{ formatDay(appointment.date) }}</div>
                  <div class="upcoming-time">{{ appointment.time }}</div>
                </div>
                <div class="upcoming-info">
                  <h6>{{ appointment.clientName }}</h6>
                  <p class="upcoming-service">{{ appointment.service }}</p>
                </div>
                <button class="btn btn-sm btn-outline-primary" 
                        @click="viewAppointment(appointment.id)">
                  <i class="fas fa-eye"></i>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Recent Reviews -->
        <div class="card">
          <div class="card-header">
            <h5 class="mb-0">Reseñas Recientes</h5>
          </div>
          <div class="card-body">
            <div v-if="recentReviews.length === 0" class="text-center py-3">
              <p class="text-muted">No hay reseñas recientes</p>
            </div>
            <div v-else>
              <div v-for="review in recentReviews" :key="review.id" class="review-item">
                <div class="review-header">
                  <div class="reviewer-info">
                    <img :src="review.clientAvatar" :alt="review.clientName" class="reviewer-avatar">
                    <div>
                      <h6>{{ review.clientName }}</h6>
                      <div class="review-rating">
                        <i v-for="star in 5" :key="star" 
                           :class="star <= review.rating ? 'fas fa-star text-warning' : 'far fa-star text-warning'"></i>
                      </div>
                    </div>
                  </div>
                  <span class="review-time">{{ review.time }}</span>
                </div>
                <p class="review-comment">{{ review.comment }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Status Modal -->
    <ModalDialog :show="showStatusModal" title="Cambiar Estado" @update:show="showStatusModal = false">
      <div class="status-modal">
        <p>Selecciona tu nuevo estado:</p>
        <div class="status-options">
          <button class="status-option" :class="{ active: newStatus === 'online' }" 
                  @click="newStatus = 'online'">
            <i class="fas fa-circle text-success"></i>
            <span>En línea</span>
          </button>
          <button class="status-option" :class="{ active: newStatus === 'busy' }" 
                  @click="newStatus = 'busy'">
            <i class="fas fa-circle text-warning"></i>
            <span>Ocupado</span>
          </button>
          <button class="status-option" :class="{ active: newStatus === 'offline' }" 
                  @click="newStatus = 'offline'">
            <i class="fas fa-circle text-secondary"></i>
            <span>Desconectado</span>
          </button>
        </div>
        <div class="modal-actions">
          <button class="btn btn-secondary" @click="showStatusModal = false">Cancelar</button>
          <button class="btn btn-primary" @click="updateStatus">Actualizar</button>
        </div>
      </div>
    </ModalDialog>

    <!-- Loading Overlay -->
    <div v-if="loading" class="loading-overlay">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { Chart, registerables } from 'chart.js';
import StatCard from '@/components/common/StatCard.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import ModalDialog from '@/components/common/ModalDialog.vue';

Chart.register(...registerables);

export default {
  name: 'BarberDashboard',
  components: {
    StatCard,
    LoadingSpinner,
    ModalDialog
  },
  setup() {
    const router = useRouter();
    const store = useStore();
    
    const loading = ref(true);
    const loadingSchedule = ref(true);
    const showStatusModal = ref(false);
    const newStatus = ref('online');
    const performanceChart = ref(null);
    const chartInstance = ref(null);
    
    const userName = computed(() => store.getters.userName || 'Carlos');
    const currentStatus = ref('online');
    
    const todayAppointments = ref(5);
    const weeklyEarnings = ref(12500);
    const averageRating = ref(4.8);
    const clientSatisfaction = ref(0.95);
    const totalClients = ref(45);
    const servicesProvided = ref(156);
    const totalHours = ref(84);
    const rankingPosition = ref(3);
    
    const statusText = computed(() => {
      const statusMap = {
        'online': 'En línea',
        'busy': 'Ocupado',
        'offline': 'Desconectado'
      };
      return statusMap[currentStatus.value] || 'Desconectado';
    });
    
    const statusClass = computed(() => {
      return currentStatus.value;
    });
    
    const statusButtonText = computed(() => {
      return currentStatus.value === 'offline' ? 'Conectarse' : 'Desconectarse';
    });
    
    const currentDate = computed(() => {
      return new Date().toLocaleDateString('es-ES', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    });
    
    const todaysAppointments = ref([
      {
        id: 1,
        clientName: "María González",
        clientAvatar: "https://ui-avatars.com/api/?name=Maria+Gonzalez&background=6f42c1&color=fff",
        service: "Corte + Color",
        time: "09:00",
        duration: 90,
        status: "completed",
        statusText: "Completado"
      },
      {
        id: 2,
        clientName: "Juan Pérez",
        clientAvatar: "https://ui-avatars.com/api/?name=Juan+Perez&background=20c997&color=fff",
        service: "Corte de Cabello",
        time: "10:30",
        duration: 45,
        status: "in-progress",
        statusText: "En progreso"
      },
      {
        id: 3,
        clientName: "Laura Sánchez",
        clientAvatar: "https://ui-avatars.com/api/?name=Laura+Sanchez&background=dc3545&color=fff",
        service: "Tratamiento Capilar",
        time: "11:30",
        duration: 60,
        status: "confirmed",
        statusText: "Confirmado"
      },
      {
        id: 4,
        clientName: "Carlos López",
        clientAvatar: "https://ui-avatars.com/api/?name=Carlos+Lopez&background=ffc107&color=fff",
        service: "Barba",
        time: "13:00",
        duration: 30,
        status: "confirmed",
        statusText: "Confirmado"
      }
    ]);
    
    const upcomingAppointments = ref([
      {
        id: 5,
        clientName: "Ana Martínez",
        service: "Corte + Peinado",
        date: new Date(Date.now() + 86400000), // Mañana
        time: "10:00"
      },
      {
        id: 6,
        clientName: "Roberto Díaz",
        service: "Corte de Cabello",
        date: new Date(Date.now() + 86400000),
        time: "11:30"
      },
      {
        id: 7,
        clientName: "Sofía Fernández",
        service: "Tintura",
        date: new Date(Date.now() + 172800000), // En 2 días
        time: "15:00"
      }
    ]);
    
    const recentReviews = ref([
      {
        id: 1,
        clientName: "María González",
        clientAvatar: "https://ui-avatars.com/api/?name=Maria+Gonzalez&background=6f42c1&color=fff",
        rating: 5,
        comment: "Excelente servicio, muy profesional! Carlos sabe exactamente lo que quiero.",
        time: "Hace 2 horas"
      },
      {
        id: 2,
        clientName: "Juan Pérez",
        clientAvatar: "https://ui-avatars.com/api/?name=Juan+Perez&background=20c997&color=fff",
        rating: 4,
        comment: "Buen trabajo, muy atento a los detalles. Volveré pronto.",
        time: "Hace 1 día"
      },
      {
        id: 3,
        clientName: "Laura Sánchez",
        clientAvatar: "https://ui-avatars.com/api/?name=Laura+Sanchez&background=dc3545&color=fff",
        rating: 5,
        comment: "Me encantó el tratamiento, mi cabello luce increíble. Totalmente recomendado.",
        time: "Hace 3 días"
      }
    ]);

    // Métodos
    const changeStatus = () => {
      newStatus.value = currentStatus.value;
      showStatusModal.value = true;
    };

    const updateStatus = () => {
      currentStatus.value = newStatus.value;
      showStatusModal.value = false;
      // Aquí podrías enviar el nuevo estado al backend
    };

    const viewAppointment = (id) => {
      router.push(`/barber/appointments/${id}`);
    };

    const startAppointment = (id) => {
      // Encontrar la cita y cambiar su estado
      const appointment = todaysAppointments.value.find(a => a.id === id);
      if (appointment) {
        appointment.status = 'in-progress';
        appointment.statusText = 'En progreso';
      }
    };

    const formatDay = (date) => {
      return date.toLocaleDateString('es-ES', { weekday: 'short', day: 'numeric' });
    };

    const initChart = () => {
      if (chartInstance.value) {
        chartInstance.value.destroy();
      }

      const ctx = performanceChart.value.getContext('2d');
      chartInstance.value = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
          datasets: [
            {
              label: 'Servicios Realizados',
              data: [8, 12, 10, 15, 18, 14, 6],
              backgroundColor: 'rgba(111, 66, 193, 0.7)',
              borderColor: 'rgba(111, 66, 193, 1)',
              borderWidth: 1
            },
            {
              label: 'Ganancias ($)',
              data: [2000, 3000, 2500, 3750, 4500, 3500, 1500],
              backgroundColor: 'rgba(32, 201, 151, 0.7)',
              borderColor: 'rgba(32, 201, 151, 1)',
              borderWidth: 1,
              type: 'line',
              yAxisID: 'y1',
              fill: false
            }
          ]
        },
        options: {
          responsive: true,
          scales: {
            y: {
              beginAtZero: true,
              title: {
                display: true,
                text: 'Servicios'
              }
            },
            y1: {
              beginAtZero: true,
              position: 'right',
              grid: {
                drawOnChartArea: false
              },
              title: {
                display: true,
                text: 'Ganancias ($)'
              }
            }
          }
        }
      });
    };

    // Ciclo de vida
    onMounted(() => {
      // Simular carga de datos
      setTimeout(() => {
        loading.value = false;
        loadingSchedule.value = false;
        
        // Inicializar gráfico después de que el DOM esté renderizado
        setTimeout(initChart, 100);
      }, 1500);
    });

    // Watcher para el elemento del gráfico
    watch(performanceChart, (newValue) => {
      if (newValue) {
        initChart();
      }
    });

    return {
      loading,
      loadingSchedule,
      showStatusModal,
      newStatus,
      performanceChart,
      userName,
      currentStatus,
      todayAppointments,
      weeklyEarnings,
      averageRating,
      clientSatisfaction,
      totalClients,
      servicesProvided,
      totalHours,
      rankingPosition,
      statusText,
      statusClass,
      statusButtonText,
      currentDate,
      todaysAppointments,
      upcomingAppointments,
      recentReviews,
      changeStatus,
      updateStatus,
      viewAppointment,
      startAppointment,
      formatDay
    };
  }
}
</script>

<style scoped>
.barber-dashboard {
  position: relative;
  padding: 20px;
}

.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #6f42c1 0%, #6610f2 100%);
  color: white;
  padding: 25px;
  border-radius: 10px;
  margin-bottom: 24px;
}

.welcome-content h2 {
  margin-bottom: 5px;
}

.welcome-stats {
  margin-top: 15px;
  display: flex;
  gap: 15px;
}

.stat-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 12px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  font-size: 14px;
}

.stat-badge.online i {
  color: #20c997;
}

.stat-badge.busy i {
  color: #ffc107;
}

.stat-badge.offline i {
  color: #6c757d;
}

.welcome-actions .btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.welcome-actions .btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* Schedule Timeline */
.schedule-timeline {
  position: relative;
  padding-left: 60px;
}

.timeline-item {
  display: flex;
  margin-bottom: 20px;
  position: relative;
}

.timeline-item:before {
  content: '';
  position: absolute;
  left: -30px;
  top: 15px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #6f42c1;
  z-index: 1;
}

.timeline-item:after {
  content: '';
  position: absolute;
  left: -25px;
  top: 21px;
  width: 2px;
  height: calc(100% + 20px);
  background: #dee2e6;
}

.timeline-item:last-child:after {
  display: none;
}

.timeline-time {
  width: 80px;
  flex-shrink: 0;
  font-weight: bold;
  color: #6c757d;
}

.timeline-content {
  flex: 1;
  padding: 15px;
  border-radius: 8px;
  background: #f8f9fa;
  border-left: 4px solid #6f42c1;
}

.timeline-content.completed {
  border-left-color: #20c997;
}

.timeline-content.in-progress {
  border-left-color: #ffc107;
}

.timeline-content.confirmed {
  border-left-color: #6f42c1;
}

.appointment-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.client-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.client-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.service-name {
  margin: 0;
  font-size: 14px;
  color: #6c757d;
}

.appointment-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.completed {
  background: rgba(32, 201, 151, 0.1);
  color: #20c997;
}

.status-badge.in-progress {
  background: rgba(255, 193, 7, 0.1);
  color: #ffc107;
}

.status-badge.confirmed {
  background: rgba(111, 66, 193, 0.1);
  color: #6f42c1;
}

.action-buttons {
  display: flex;
  gap: 5px;
}

/* Quick Stats */
.quick-stats {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px 0;
}

.stat-item:not(:last-child) {
  border-bottom: 1px solid #e9ecef;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: rgba(111, 66, 193, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6f42c1;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 2px;
}

.stat-label {
  font-size: 14px;
  color: #6c757d;
}

/* Upcoming Appointments */
.upcoming-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  gap: 15px;
}

.upcoming-item:not(:last-child) {
  border-bottom: 1px solid #e9ecef;
}

.upcoming-date {
  text-align: center;
  flex-shrink: 0;
  width: 50px;
}

.upcoming-day {
  font-weight: bold;
  font-size: 14px;
}

.upcoming-time {
  font-size: 12px;
  color: #6c757d;
}

.upcoming-info {
  flex: 1;
}

.upcoming-service {
  margin: 0;
  font-size: 14px;
  color: #6c757d;
}

/* Reviews */
.review-item {
  padding: 15px 0;
}

.review-item:not(:last-child) {
  border-bottom: 1px solid #e9ecef;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.review-rating {
  font-size: 12px;
}

.review-time {
  font-size: 12px;
  color: #6c757d;
}

.review-comment {
  margin: 0;
  font-size: 14px;
  color: #495057;
}

/* Status Modal */
.status-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin: 20px 0;
}

.status-option {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 15px;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  background: white;
  transition: all 0.2s;
}

.status-option:hover {
  background: #f8f9fa;
}

.status-option.active {
  border-color: #6f42c1;
  background: rgba(111, 66, 193, 0.05);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

/* Loading Overlay */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

/* Responsive */
@media (max-width: 992px) {
  .welcome-section {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .welcome-stats {
    justify-content: center;
  }
  
  .appointment-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .appointment-actions {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 768px) {
  .schedule-timeline {
    padding-left: 40px;
  }
  
  .timeline-item:before {
    left: -20px;
  }
  
  .timeline-item:after {
    left: -15px;
  }
  
  .timeline-time {
    width: 60px;
  }
}
</style>