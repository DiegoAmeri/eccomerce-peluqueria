<template>
  <div class="barber-dashboard">
    <!-- Header Section -->
    <div class="dashboard-header">
      <div class="header-content">
        <h1>Mi Panel de Trabajo</h1>
        <p class="text-muted">Bienvenido de vuelta, {{ barberName }}</p>
        <div class="status-indicator">
          <span class="status-dot" :class="currentStatus"></span>
          <span class="status-text">{{ statusText }}</span>
          <button class="btn btn-sm btn-outline-secondary ms-2" @click="toggleStatus">
            Cambiar estado
          </button>
        </div>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" @click="quickAction('new-appointment')">
          <i class="fas fa-plus me-2"></i> Nuevo Turno
        </button>
        <button class="btn btn-outline-primary" @click="refreshData">
          <i class="fas fa-sync-alt" :class="{ 'fa-spin': refreshing }"></i>
        </button>
      </div>
    </div>

    <!-- Statistics Grid -->
    <div class="stats-grid">
      <div class="row">
        <div class="col-xl-3 col-md-6 mb-4">
          <StatCard 
            layout="icon"
            :value="todayStats.appointments"
            label="Turnos Hoy"
            icon="fas fa-calendar-day"
            icon-color="#6f42c1"
            icon-bg-color="rgba(111, 66, 193, 0.1)"
            :trend="todayStats.trend"
            theme="primary"
            clickable
            @click="viewTodayAppointments"
          />
        </div>
        <div class="col-xl-3 col-md-6 mb-4">
          <StatCard 
            layout="icon"
            :value="weeklyStats.completed"
            label="Completados (Semana)"
            icon="fas fa-check-circle"
            icon-color="#20c997"
            icon-bg-color="rgba(32, 201, 151, 0.1)"
            :trend="weeklyStats.trend"
            theme="success"
          />
        </div>
        <div class="col-xl-3 col-md-6 mb-4">
          <StatCard 
            layout="icon"
            :value="averageRating"
            label="Calificación Promedio"
            icon="fas fa-star"
            icon-color="#ffc107"
            icon-bg-color="rgba(255, 193, 7, 0.1)"
            :trend="ratingTrend"
            format="decimal"
            :decimals="1"
            theme="warning"
          />
        </div>
        <div class="col-xl-3 col-md-6 mb-4">
          <StatCard 
            layout="icon"
            :value="weeklyEarnings"
            label="Ganancias (Semana)"
            icon="fas fa-money-bill-wave"
            icon-color="#dc3545"
            icon-bg-color="rgba(220, 53, 69, 0.1)"
            :trend="earningsTrend"
            format="currency"
            theme="danger"
            clickable
            @click="viewEarnings"
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
          <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Agenda de Hoy</h5>
            <div>
              <span class="badge bg-primary me-2">{{ currentDate }}</span>
              <span class="badge" :class="`bg-${getAvailabilityBadgeClass()}`">
                {{ availabilityStatus }}
              </span>
            </div>
          </div>
          <div class="card-body">
            <div v-if="loadingAppointments" class="text-center py-4">
              <LoadingSpinner message="Cargando turnos..." />
            </div>
            <div v-else-if="todayAppointments.length === 0" class="text-center py-4">
              <i class="fas fa-calendar-times fa-3x text-muted mb-3"></i>
              <h5>No hay turnos programados para hoy</h5>
              <p class="text-muted">Puedes agregar nuevos turnos o descansar un poco</p>
              <button class="btn btn-primary" @click="quickAction('new-appointment')">
                <i class="fas fa-plus me-2"></i> Agregar Turno
              </button>
            </div>
            <div v-else>
              <div class="timeline">
                <div v-for="appointment in todayAppointments" :key="appointment.id" 
                     class="timeline-item" :class="appointment.status">
                  <div class="timeline-time">
                    {{ appointment.time }}
                  </div>
                  <div class="timeline-content">
                    <div class="appointment-card">
                      <div class="appointment-header">
                        <div class="client-info">
                          <img :src="appointment.clientAvatar" :alt="appointment.clientName" class="client-avatar">
                          <div>
                            <h6 class="client-name">{{ appointment.clientName }}</h6>
                            <p class="service-name">{{ appointment.service }}</p>
                          </div>
                        </div>
                        <span class="status-badge" :class="`bg-${appointment.statusClass}`">
                          {{ appointment.statusText }}
                        </span>
                      </div>
                      <div class="appointment-details">
                        <div class="detail">
                          <i class="fas fa-clock"></i>
                          <span>{{ appointment.duration }} min</span>
                        </div>
                        <div class="detail">
                          <i class="fas fa-dollar-sign"></i>
                          <span>${{ appointment.price }}</span>
                        </div>
                        <div class="detail">
                          <i class="fas fa-info-circle"></i>
                          <span>{{ appointment.notes || 'Sin notas' }}</span>
                        </div>
                      </div>
                      <div class="appointment-actions">
                        <button class="btn btn-sm btn-outline-success" 
                                @click="updateAppointmentStatus(appointment.id, 'completed')"
                                v-if="appointment.status === 'confirmed'">
                          <i class="fas fa-check me-1"></i> Completar
                        </button>
                        <button class="btn btn-sm btn-outline-warning" 
                                @click="updateAppointmentStatus(appointment.id, 'confirmed')"
                                v-if="appointment.status === 'pending'">
                          <i class="fas fa-check me-1"></i> Confirmar
                        </button>
                        <button class="btn btn-sm btn-outline-danger" 
                                @click="updateAppointmentStatus(appointment.id, 'cancelled')"
                                v-if="appointment.status !== 'cancelled' && appointment.status !== 'completed'">
                          <i class="fas fa-times me-1"></i> Cancelar
                        </button>
                        <button class="btn btn-sm btn-outline-primary" 
                                @click="viewAppointmentDetails(appointment.id)">
                          <i class="fas fa-eye me-1"></i> Ver
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Weekly Performance -->
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
            <h5 class="mb-0">Resumen Rápido</h5>
          </div>
          <div class="card-body">
            <div class="quick-stats">
              <div class="stat-item">
                <div class="stat-icon">
                  <i class="fas fa-users"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ clientStats.total }}</div>
                  <div class="stat-label">Clientes Totales</div>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon">
                  <i class="fas fa-user-clock"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ clientStats.newThisMonth }}</div>
                  <div class="stat-label">Nuevos este Mes</div>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon">
                  <i class="fas fa-redo"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ clientStats.returning }}%</div>
                  <div class="stat-label">Clientes Recurrentes</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Upcoming Appointments -->
        <div class="card mb-4">
          <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Próximos Turnos</h5>
            <router-link to="/barber/appointments" class="btn btn-sm btn-outline-primary">
              Ver todos
            </router-link>
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
                  <div class="upcoming-month">{{ formatMonth(appointment.date) }}</div>
                </div>
                <div class="upcoming-info">
                  <h6 class="upcoming-time">{{ appointment.time }}</h6>
                  <p class="upcoming-client">{{ appointment.clientName }}</p>
                  <span class="upcoming-service">{{ appointment.service }}</span>
                </div>
                <button class="upcoming-action" @click="viewAppointmentDetails(appointment.id)">
                  <i class="fas fa-chevron-right"></i>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Recent Reviews -->
        <div class="card">
          <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Reseñas Recientes</h5>
            <router-link to="/barber/reviews" class="btn btn-sm btn-outline-primary">
              Ver todas
            </router-link>
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
                      <h6 class="reviewer-name">{{ review.clientName }}</h6>
                      <div class="review-rating">
                        <i v-for="star in 5" :key="star" 
                           :class="star <= review.rating ? 'fas fa-star' : 'far fa-star'"
                           class="text-warning"></i>
                      </div>
                    </div>
                  </div>
                  <span class="review-date">{{ formatReviewDate(review.date) }}</span>
                </div>
                <p class="review-comment">{{ review.comment }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Status Modal -->
    <ModalDialog 
      :show="showStatusModal" 
      title="Cambiar Estado"
      @update:show="showStatusModal = false"
    >
      <div class="status-modal">
        <h6>Selecciona tu estado actual:</h6>
        <div class="status-options">
          <div v-for="status in statusOptions" :key="status.value" 
               class="status-option" :class="{ selected: selectedStatus === status.value }"
               @click="selectedStatus = status.value">
            <div class="status-icon">
              <i :class="status.icon"></i>
            </div>
            <div class="status-info">
              <div class="status-title">{{ status.label }}</div>
              <div class="status-description">{{ status.description }}</div>
            </div>
            <div class="status-check">
              <i class="fas fa-check" v-if="selectedStatus === status.value"></i>
            </div>
          </div>
        </div>
        <div class="status-actions">
          <button class="btn btn-secondary" @click="showStatusModal = false">
            Cancelar
          </button>
          <button class="btn btn-primary" @click="updateStatus">
            Actualizar Estado
          </button>
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
import { ref, onMounted, computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
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
    const store = useStore();
    const router = useRouter();
    const performanceChart = ref(null);
    const chartInstance = ref(null);
    
    const loading = ref(true);
    const refreshing = ref(false);
    const loadingAppointments = ref(true);
    const showStatusModal = ref(false);
    const selectedStatus = ref('available');
    
    const currentStatus = ref('available'); // available, busy, break, offline
    const barberName = computed(() => store.getters.userName);
    
    const statusOptions = [
      {
        value: 'available',
        label: 'Disponible',
        icon: 'fas fa-check-circle',
        description: 'Aceptando nuevos turnos'
      },
      {
        value: 'busy',
        label: 'Ocupado',
        icon: 'fas fa-user-clock',
        description: 'En medio de un servicio'
      },
      {
        value: 'break',
        label: 'En Descanso',
        icon: 'fas fa-coffee',
        description: 'Volveré pronto'
      },
      {
        value: 'offline',
        label: 'No Disponible',
        icon: 'fas fa-times-circle',
        description: 'No aceptando turnos'
      }
    ];
    
    const statusText = computed(() => {
      const statusMap = {
        'available': 'Disponible',
        'busy': 'Ocupado',
        'break': 'En Descanso',
        'offline': 'No Disponible'
      };
      return statusMap[currentStatus.value] || 'Desconocido';
    });
    
    const availabilityStatus = computed(() => {
      const statusMap = {
        'available': 'Disponible',
        'busy': 'Ocupado - No molestar',
        'break': 'En pausa',
        'offline': 'Fuera de servicio'
      };
      return statusMap[currentStatus.value] || 'Desconocido';
    });
    
    const todayStats = ref({
      appointments: 0,
      completed: 0,
      pending: 0,
      trend: 0
    });
    
    const weeklyStats = ref({
      completed: 0,
      cancelled: 0,
      revenue: 0,
      trend: 0
    });
    
    const averageRating = ref(0);
    const ratingTrend = ref(0);
    const weeklyEarnings = ref(0);
    const earningsTrend = ref(0);
    
    const todayAppointments = ref([]);
    const upcomingAppointments = ref([]);
    const recentReviews = ref([]);
    
    const clientStats = ref({
      total: 0,
      newThisMonth: 0,
      returning: 0
    });
    
    const currentDate = ref('');
    
    const loadDashboardData = async () => {
      try {
        loading.value = true;
        loadingAppointments.value = true;
        
        // Simular carga de datos desde API
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Datos de ejemplo
        todayStats.value = {
          appointments: 8,
          completed: 3,
          pending: 2,
          trend: 12
        };
        
        weeklyStats.value = {
          completed: 24,
          cancelled: 2,
          revenue: 12500,
          trend: 8
        };
        
        averageRating.value = 4.8;
        ratingTrend.value = 2.5;
        weeklyEarnings.value = 18500;
        earningsTrend.value = 15;
        
        clientStats.value = {
          total: 124,
          newThisMonth: 12,
          returning: 68
        };
        
        todayAppointments.value = [
          {
            id: 1,
            clientName: "María González",
            clientAvatar: "https://ui-avatars.com/api/?name=Maria+Gonzalez&background=6f42c1&color=fff",
            service: "Corte + Color",
            time: "10:00",
            duration: 90,
            price: 1200,
            status: "completed",
            statusText: "Completado",
            statusClass: "success",
            notes: "Cliente puntual, muy satisfecha"
          },
          {
            id: 2,
            clientName: "Juan Pérez",
            clientAvatar: "https://ui-avatars.com/api/?name=Juan+Perez&background=20c997&color=fff",
            service: "Corte de Cabello",
            time: "11:30",
            duration: 30,
            price: 500,
            status: "confirmed",
            statusText: "Confirmado",
            statusClass: "primary",
            notes: "Preferencia: laterales cortos"
          },
          {
            id: 3,
            clientName: "Laura Sánchez",
            clientAvatar: "https://ui-avatars.com/api/?name=Laura+Sanchez&background=dc3545&color=fff",
            service: "Tratamiento Capilar",
            time: "12:30",
            duration: 45,
            price: 800,
            status: "pending",
            statusText: "Pendiente",
            statusClass: "warning",
            notes: "Primera vez en la peluquería"
          },
          {
            id: 4,
            clientName: "Carlos López",
            clientAvatar: "https://ui-avatars.com/api/?name=Carlos+Lopez&background=ffc107&color=000",
            service: "Corte + Barba",
            time: "14:00",
            duration: 45,
            price: 700,
            status: "confirmed",
            statusText: "Confirmado",
            statusClass: "primary",
            notes: "Cliente habitual"
          }
        ];
        
        upcomingAppointments.value = [
          {
            id: 5,
            clientName: "Ana Martínez",
            service: "Tintura",
            date: new Date(Date.now() + 86400000),
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
            service: "Reflejos",
            date: new Date(Date.now() + 172800000),
            time: "09:00"
          }
        ];
        
        recentReviews.value = [
          {
            id: 1,
            clientName: "María González",
            clientAvatar: "https://ui-avatars.com/api/?name=Maria+Gonzalez&background=6f42c1&color=fff",
            rating: 5,
            comment: "Excelente servicio! Carlos es muy profesional y el resultado superó mis expectativas.",
            date: new Date(Date.now() - 86400000)
          },
          {
            id: 2,
            clientName: "Juan Pérez",
            clientAvatar: "https://ui-avatars.com/api/?name=Juan+Perez&background=20c997&color=fff",
            rating: 4,
            comment: "Muy buen corte, atención personalizada. Volveré pronto!",
            date: new Date(Date.now() - 172800000)
          }
        ];
        
        // Formatear fecha actual
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        currentDate.value = new Date().toLocaleDateString('es-ES', options);
        
        // Inicializar gráfico
        initPerformanceChart();
        
      } catch (error) {
        console.error('Error loading dashboard data:', error);
      } finally {
        loading.value = false;
        loadingAppointments.value = false;
      }
    };
    
    const initPerformanceChart = () => {
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
              label: 'Turnos Completados',
              data: [12, 19, 15, 17, 22, 18, 14],
              backgroundColor: 'rgba(111, 66, 193, 0.7)',
              borderColor: 'rgba(111, 66, 193, 1)',
              borderWidth: 1
            },
            {
              label: 'Ingresos ($)',
              data: [4500, 5200, 4800, 5500, 7200, 6800, 5300],
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
                text: 'Turnos Completados'
              }
            },
            y1: {
              position: 'right',
              beginAtZero: true,
              title: {
                display: true,
                text: 'Ingresos ($)'
              },
              grid: {
                drawOnChartArea: false
              }
            }
          },
          plugins: {
            tooltip: {
              callbacks: {
                label: function(context) {
                  let label = context.dataset.label || '';
                  if (label.includes('Ingresos')) {
                    label += ': $' + context.raw.toLocaleString();
                  } else {
                    label += ': ' + context.raw;
                  }
                  return label;
                }
              }
            }
          }
        }
      });
    };
    
    const refreshData = async () => {
      refreshing.value = true;
      await loadDashboardData();
      refreshing.value = false;
    };
    
    const toggleStatus = () => {
      selectedStatus.value = currentStatus.value;
      showStatusModal.value = true;
    };
    
    const updateStatus = () => {
      currentStatus.value = selectedStatus.value;
      showStatusModal.value = false;
      
      // En una app real, actualizaríamos el estado en el backend
      console.log('Estado actualizado a:', currentStatus.value);
    };
    
    const updateAppointmentStatus = (appointmentId, newStatus) => {
      // En una app real, haríamos una llamada a la API
      const appointment = todayAppointments.value.find(a => a.id === appointmentId);
      if (appointment) {
        appointment.status = newStatus;
        appointment.statusText = newStatus === 'completed' ? 'Completado' : 
                               newStatus === 'confirmed' ? 'Confirmado' : 
                               newStatus === 'cancelled' ? 'Cancelado' : 'Pendiente';
        appointment.statusClass = newStatus === 'completed' ? 'success' : 
                                newStatus === 'confirmed' ? 'primary' : 
                                newStatus === 'cancelled' ? 'danger' : 'warning';
        
        console.log(`Turno ${appointmentId} actualizado a: ${newStatus}`);
      }
    };
    
    const viewAppointmentDetails = (appointmentId) => {
      router.push(`/barber/appointments/${appointmentId}`);
    };
    
    const viewTodayAppointments = () => {
      router.push('/barber/appointments?filter=today');
    };
    
    const viewEarnings = () => {
      router.push('/barber/earnings');
    };
    
    const quickAction = (action) => {
      const actions = {
        'new-appointment': () => router.push('/barber/appointments/new'),
        'view-schedule': () => router.push('/barber/schedule'),
        'manage-services': () => router.push('/barber/services')
      };
      
      if (actions[action]) {
        actions[action]();
      }
    };
    
    const formatDay = (date) => {
      return new Date(date).getDate();
    };
    
    const formatMonth = (date) => {
      return new Date(date).toLocaleDateString('es-ES', { month: 'short' }).toUpperCase();
    };
    
    const formatReviewDate = (date) => {
      return new Date(date).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    };
    
    const getAvailabilityBadgeClass = () => {
      const statusMap = {
        'available': 'success',
        'busy': 'warning',
        'break': 'info',
        'offline': 'secondary'
      };
      return statusMap[currentStatus.value] || 'secondary';
    };
    
    onMounted(() => {
      loadDashboardData();
    });
    
    return {
      // Refs
      performanceChart,
      loading,
      refreshing,
      loadingAppointments,
      showStatusModal,
      selectedStatus,
      currentStatus,
      
      // Computed
      barberName,
      statusText,
      availabilityStatus,
      currentDate,
      
      // Data
      statusOptions,
      todayStats,
      weeklyStats,
      averageRating,
      ratingTrend,
      weeklyEarnings,
      earningsTrend,
      todayAppointments,
      upcomingAppointments,
      recentReviews,
      clientStats,
      
      // Methods
      refreshData,
      toggleStatus,
      updateStatus,
      updateAppointmentStatus,
      viewAppointmentDetails,
      viewTodayAppointments,
      viewEarnings,
      quickAction,
      formatDay,
      formatMonth,
      formatReviewDate,
      getAvailabilityBadgeClass
    };
  }
};
</script>

<style scoped>
.barber-dashboard {
  padding: 20px;
  position: relative;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
}

.header-content h1 {
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 5px;
}

.status-indicator {
  display: flex;
  align-items: center;
  margin-top: 10px;
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 8px;
}

.status-dot.available {
  background: #28a745;
  box-shadow: 0 0 10px rgba(40, 167, 69, 0.5);
}

.status-dot.busy {
  background: #ffc107;
  box-shadow: 0 0 10px rgba(255, 193, 7, 0.5);
}

.status-dot.break {
  background: #17a2b8;
  box-shadow: 0 0 10px rgba(23, 162, 184, 0.5);
}

.status-dot.offline {
  background: #6c757d;
  box-shadow: 0 0 10px rgba(108, 117, 125, 0.5);
}

.status-text {
  font-weight: 500;
  color: #495057;
}

.stats-grid {
  margin-bottom: 30px;
}

/* Timeline Styles */
.timeline {
  position: relative;
  padding-left: 30px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 15px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e9ecef;
}

.timeline-item {
  position: relative;
  margin-bottom: 20px;
}

.timeline-item::before {
  content: '';
  position: absolute;
  left: -30px;
  top: 15px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #6f42c1;
  border: 3px solid white;
  z-index: 2;
}

.timeline-item.completed::before {
  background: #28a745;
}

.timeline-item.cancelled::before {
  background: #dc3545;
}

.timeline-item.pending::before {
  background: #ffc107;
}

.timeline-time {
  position: absolute;
  left: -80px;
  top: 10px;
  font-weight: 600;
  color: #6f42c1;
  font-size: 0.9rem;
}

.timeline-content {
  background: white;
  border-radius: 12px;
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.appointment-card {
  padding: 15px;
}

.appointment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.client-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.client-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  object-fit: cover;
}

.client-name {
  margin: 0;
  font-weight: 600;
  color: #2c3e50;
}

.service-name {
  margin: 4px 0 0 0;
  color: #6c757d;
  font-size: 0.9rem;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.appointment-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 10px;
  margin-bottom: 15px;
}

.detail {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.85rem;
  color: #6c757d;
}

.detail i {
  width: 14px;
  text-align: center;
}

.appointment-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.appointment-actions .btn {
  border-radius: 20px;
  font-size: 0.8rem;
  padding: 4px 10px;
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
  padding: 12px;
  background: #f8f9fa;
  border-radius: 10px;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.2rem;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-weight: 700;
  color: #2c3e50;
  font-size: 1.3rem;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  color: #6c757d;
  font-size: 0.9rem;
  font-weight: 500;
}

/* Upcoming Appointments */
.upcoming-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f1f3f4;
}

.upcoming-item:last-child {
  border-bottom: none;
}

.upcoming-date {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  color: white;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  flex-shrink: 0;
}

.upcoming-day {
  font-size: 1.2rem;
  font-weight: 700;
  line-height: 1;
}

.upcoming-month {
  font-size: 0.7rem;
  text-transform: uppercase;
  font-weight: 500;
  margin-top: 2px;
}

.upcoming-info {
  flex: 1;
}

.upcoming-time {
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
  font-size: 0.95rem;
}

.upcoming-client {
  margin: 0 0 4px 0;
  font-size: 0.9rem;
  color: #495057;
}

.upcoming-service {
  font-size: 0.8rem;
  color: #6c757d;
  background: #f8f9fa;
  padding: 2px 8px;
  border-radius: 10px;
}

.upcoming-action {
  background: none;
  border: none;
  color: #6c757d;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.upcoming-action:hover {
  background: #6f42c1;
  color: white;
}

/* Reviews */
.review-item {
  padding: 12px 0;
  border-bottom: 1px solid #f1f3f4;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.reviewer-info {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.reviewer-avatar {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  object-fit: cover;
}

.reviewer-name {
  margin: 0;
  font-weight: 600;
  color: #2c3e50;
  font-size: 0.95rem;
}

.review-rating {
  margin-top: 2px;
}

.review-rating i {
  font-size: 0.8rem;
}

.review-date {
  color: #6c757d;
  font-size: 0.8rem;
}

.review-comment {
  margin: 0;
  color: #495057;
  font-size: 0.9rem;
  line-height: 1.4;
}

/* Status Modal */
.status-modal {
  padding: 10px;
}

.status-options {
  margin: 20px 0;
}

.status-option {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.status-option:hover {
  border-color: #6f42c1;
  background: #f8f9fa;
}

.status-option.selected {
  border-color: #6f42c1;
  background: rgba(111, 66, 193, 0.1);
}

.status-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #6f42c1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.2rem;
  margin-right: 15px;
  flex-shrink: 0;
}

.status-info {
  flex: 1;
}

.status-title {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.status-description {
  color: #6c757d;
  font-size: 0.9rem;
}

.status-check {
  color: #6f42c1;
  font-size: 1.1rem;
}

.status-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

/* Chart */
.chart-container {
  position: relative;
  height: 250px;
}

/* Loading Overlay */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  border-radius: 16px;
}

/* Responsive */
@media (max-width: 992px) {
  .dashboard-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .timeline {
    padding-left: 20px;
  }
  
  .timeline::before {
    left: 10px;
  }
  
  .timeline-item::before {
    left: -20px;
  }
  
  .timeline-time {
    position: relative;
    left: 0;
    margin-bottom: 5px;
    font-size: 0.85rem;
  }
}

@media (max-width: 768px) {
  .barber-dashboard {
    padding: 15px;
  }
  
  .appointment-header {
    flex-direction: column;
    gap: 10px;
  }
  
  .appointment-details {
    grid-template-columns: 1fr;
  }
  
  .appointment-actions {
    flex-direction: column;
  }
  
  .status-option {
    flex-direction: column;
    text-align: center;
    gap: 10px;
  }
  
  .status-icon {
    margin-right: 0;
  }
}

@media (max-width: 576px) {
  .header-content h1 {
    font-size: 1.5rem;
  }
  
  .stats-grid .row {
    margin: 0 -5px;
  }
  
  .stats-grid .col-xl-3,
  .stats-grid .col-md-6 {
    padding: 0 5px;
  }
  
  .status-indicator {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>