<template>
  <div class="owner-dashboard">
    <!-- Header Section -->
    <div class="dashboard-header">
      <div class="header-content">
        <h1>Panel de Control</h1>
        <p class="text-muted">Bienvenido de vuelta, {{ userName }}</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" @click="quickAction('add-salon')">
          <i class="fas fa-plus me-2"></i> Nueva Peluquería
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
            value="5"
            label="Peluquerías"
            icon="fas fa-store"
            icon-color="#6f42c1"
            icon-bg-color="rgba(111, 66, 193, 0.1)"
            trend="12"
            theme="primary"
            clickable
            @click="$router.push('/owner/salons')"
          />
        </div>
        <div class="col-xl-3 col-md-6 mb-4">
          <StatCard 
            layout="icon"
            value="24"
            label="Empleados"
            icon="fas fa-users"
            icon-color="#20c997"
            icon-bg-color="rgba(32, 201, 151, 0.1)"
            trend="8"
            theme="success"
            clickable
            @click="$router.push('/owner/employees')"
          />
        </div>
        <div class="col-xl-3 col-md-6 mb-4">
          <StatCard 
            layout="icon"
            value="12,458"
            label="Ingresos Mensuales"
            icon="fas fa-dollar-sign"
            icon-color="#ffc107"
            icon-bg-color="rgba(255, 193, 7, 0.1)"
            trend="-3"
            format="currency"
            theme="warning"
            clickable
            @click="$router.push('/owner/finances')"
          />
        </div>
        <div class="col-xl-3 col-md-6 mb-4">
          <StatCard 
            layout="icon"
            value="94%"
            label="Satisfacción Clientes"
            icon="fas fa-star"
            icon-color="#dc3545"
            icon-bg-color="rgba(220, 53, 69, 0.1)"
            trend="2"
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
        <!-- Revenue Chart -->
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="mb-0">Ingresos Mensuales</h5>
          </div>
          <div class="card-body">
            <div class="chart-container">
              <canvas ref="revenueChart" height="250"></canvas>
            </div>
          </div>
        </div>

        <!-- Recent Appointments -->
        <div class="card">
          <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Turnos Recientes</h5>
            <router-link to="/owner/appointments" class="btn btn-sm btn-outline-primary">
              Ver todos
            </router-link>
          </div>
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover">
                <thead>
                  <tr>
                    <th>Cliente</th>
                    <th>Servicio</th>
                    <th>Peluquería</th>
                    <th>Empleado</th>
                    <th>Monto</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="appointment in recentAppointments" :key="appointment.id">
                    <td>
                      <div class="d-flex align-items-center">
                        <img :src="appointment.clientAvatar" class="avatar-sm me-2">
                        {{ appointment.clientName }}
                      </div>
                    </td>
                    <td>{{ appointment.service }}</td>
                    <td>{{ appointment.salon }}</td>
                    <td>{{ appointment.employee }}</td>
                    <td>${{ appointment.amount }}</td>
                    <td>
                      <span class="badge" :class="`bg-${appointment.statusClass}`">
                        {{ appointment.statusText }}
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column -->
      <div class="col-lg-4">
        <!-- Quick Actions -->
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="mb-0">Acciones Rápidas</h5>
          </div>
          <div class="card-body">
            <div class="quick-actions-grid">
              <button class="quick-action-btn" @click="quickAction('add-employee')">
                <i class="fas fa-user-plus"></i>
                <span>Agregar Empleado</span>
              </button>
              <button class="quick-action-btn" @click="quickAction('view-reports')">
                <i class="fas fa-chart-bar"></i>
                <span>Ver Reportes</span>
              </button>
              <button class="quick-action-btn" @click="quickAction('manage-services')">
                <i class="fas fa-scissors"></i>
                <span>Gestionar Servicios</span>
              </button>
              <button class="quick-action-btn" @click="quickAction('booking-stats')">
                <i class="fas fa-calendar-alt"></i>
                <span>Estadísticas de Turnos</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Salon Performance -->
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="mb-0">Rendimiento por Peluquería</h5>
          </div>
          <div class="card-body">
            <div v-for="salon in salonPerformance" :key="salon.id" class="performance-item">
              <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="salon-name">{{ salon.name }}</span>
                <span class="revenue">${{ salon.revenue.toLocaleString() }}</span>
              </div>
              <div class="progress mb-3">
                <div class="progress-bar" :style="{ width: salon.percentage + '%' }"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Upcoming Events -->
        <div class="card">
          <div class="card-header">
            <h5 class="mb-0">Próximos Eventos</h5>
          </div>
          <div class="card-body">
            <div v-for="event in upcomingEvents" :key="event.id" class="event-item">
              <div class="event-date">
                <div class="event-day">{{ event.day }}</div>
                <div class="event-month">{{ event.month }}</div>
              </div>
              <div class="event-info">
                <h6 class="event-title">{{ event.title }}</h6>
                <p class="event-time">{{ event.time }}</p>
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
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import StatCard from '@/components/common/StatCard.vue';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

export default {
  name: 'OwnerDashboard',
  components: {
    StatCard
  },
  setup() {
    const store = useStore();
    const router = useRouter();
    const revenueChart = ref(null);
    const chartInstance = ref(null);
    
    const loading = ref(true);
    const refreshing = ref(false);
    
    const userName = computed(() => store.getters.userName);
    
    const recentAppointments = ref([
      {
        id: 1,
        clientName: "María González",
        clientAvatar: "https://ui-avatars.com/api/?name=Maria+Gonzalez&background=6f42c1&color=fff",
        service: "Corte + Color",
        salon: "Estilo Urbano",
        employee: "Carlos Rodríguez",
        amount: 1200,
        status: "completed",
        statusText: "Completado",
        statusClass: "success"
      },
      {
        id: 2,
        clientName: "Juan Pérez",
        clientAvatar: "https://ui-avatars.com/api/?name=Juan+Perez&background=20c997&color=fff",
        service: "Corte de Cabello",
        salon: "Corte Perfecto",
        employee: "Ana Martínez",
        amount: 500,
        status: "confirmed",
        statusText: "Confirmado",
        statusClass: "primary"
      },
      {
        id: 3,
        clientName: "Laura Sánchez",
        clientAvatar: "https://ui-avatars.com/api/?name=Laura+Sanchez&background=dc3545&color=fff",
        service: "Tratamiento Capilar",
        salon: "Belleza Integral",
        employee: "Miguel Ángel",
        amount: 800,
        status: "pending",
        statusText: "Pendiente",
        statusClass: "warning"
      }
    ]);
    
    const salonPerformance = ref([
      { id: 1, name: "Estilo Urbano", revenue: 58400, percentage: 45 },
      { id: 2, name: "Corte Perfecto", revenue: 42200, percentage: 32 },
      { id: 3, name: "Belleza Integral", revenue: 29800, percentage: 23 }
    ]);
    
    const upcomingEvents = ref([
      {
        id: 1,
        title: "Reunión de Staff",
        day: "15",
        month: "Ago",
        time: "10:00 AM"
      },
      {
        id: 2,
        title: "Capacitación Nuevos Servicios",
        day: "18",
        month: "Ago",
        time: "2:00 PM"
      },
      {
        id: 3,
        title: "Inventario Mensual",
        day: "22",
        month: "Ago",
        time: "9:00 AM"
      }
    ]);
    
    const initChart = () => {
      if (chartInstance.value) {
        chartInstance.value.destroy();
      }
      
      const ctx = revenueChart.value.getContext('2d');
      chartInstance.value = new Chart(ctx, {
        type: 'line',
        data: {
          labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
          datasets: [{
            label: 'Ingresos 2023',
            data: [8500, 12500, 9800, 11200, 15600, 18900, 21500, 24500, 19800, 16700, 13400, 15800],
            borderColor: '#6f42c1',
            backgroundColor: 'rgba(111, 66, 193, 0.1)',
            tension: 0.4,
            fill: true
          }, {
            label: 'Ingresos 2024',
            data: [12000, 14500, 13200, 16800, 19500, 22400, 25800, 0, 0, 0, 0, 0],
            borderColor: '#20c997',
            backgroundColor: 'rgba(32, 201, 151, 0.1)',
            tension: 0.4,
            fill: true
          }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              position: 'top',
            },
            tooltip: {
              mode: 'index',
              intersect: false,
              callbacks: {
                label: function(context) {
                  return `$${context.raw.toLocaleString()}`;
                }
              }
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                callback: function(value) {
                  return `$${value.toLocaleString()}`;
                }
              }
            }
          }
        }
      });
    };
    
    const loadDashboardData = async () => {
      try {
        loading.value = true;
        // Simular carga de datos
        await new Promise(resolve => setTimeout(resolve, 1000));
        initChart();
      } catch (error) {
        console.error('Error loading dashboard data:', error);
      } finally {
        loading.value = false;
      }
    };
    
    const refreshData = async () => {
      refreshing.value = true;
      await loadDashboardData();
      refreshing.value = false;
    };
    
    const quickAction = (action) => {
      const actions = {
        'add-salon': () => router.push('/owner/salons/new'),
        'add-employee': () => router.push('/owner/employees/new'),
        'view-reports': () => router.push('/owner/reports'),
        'manage-services': () => router.push('/owner/services'),
        'booking-stats': () => router.push('/owner/statistics')
      };
      
      if (actions[action]) {
        actions[action]();
      }
    };
    
    onMounted(() => {
      loadDashboardData();
    });
    
    return {
      userName,
      recentAppointments,
      salonPerformance,
      upcomingEvents,
      revenueChart,
      loading,
      refreshing,
      refreshData,
      quickAction
    };
  }
};
</script>

<style scoped>
.owner-dashboard {
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

.stats-grid {
  margin-bottom: 30px;
}

.chart-container {
  position: relative;
  height: 250px;
}

.avatar-sm {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.quick-action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
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
  font-size: 0.9rem;
  font-weight: 500;
}

.performance-item {
  margin-bottom: 15px;
}

.salon-name {
  font-weight: 500;
  color: #2c3e50;
}

.revenue {
  font-weight: 600;
  color: #6f42c1;
}

.progress {
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
}

.progress-bar {
  background: linear-gradient(90deg, #6f42c1 0%, #d63384 100%);
  transition: width 0.3s ease;
}

.event-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f1f3f4;
}

.event-item:last-child {
  border-bottom: none;
}

.event-date {
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

.event-day {
  font-size: 1.2rem;
  font-weight: 700;
  line-height: 1;
}

.event-month {
  font-size: 0.8rem;
  text-transform: uppercase;
  font-weight: 500;
}

.event-info {
  flex: 1;
}

.event-title {
  font-weight: 600;
  margin: 0 0 4px 0;
  color: #2c3e50;
}

.event-time {
  margin: 0;
  color: #6c757d;
  font-size: 0.9rem;
}

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

@media (max-width: 768px) {
  .owner-dashboard {
    padding: 15px;
  }
  
  .dashboard-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .quick-actions-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid .row {
    margin: 0 -5px;
  }
  
  .stats-grid .col-xl-3,
  .stats-grid .col-md-6 {
    padding: 0 5px;
  }
}

@media (max-width: 576px) {
  .header-content h1 {
    font-size: 1.5rem;
  }
  
  .chart-container {
    height: 200px;
  }
}
</style>