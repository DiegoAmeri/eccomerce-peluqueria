<template>
  <div class="barber-appointments">
    <!-- Header -->
    <div class="appointments-header">
      <div class="header-content">
        <h1>Gestión de Turnos</h1>
        <p class="text-muted">Administra tus turnos y disponibilidad</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" @click="showNewAppointmentModal">
          <i class="fas fa-plus me-2"></i> Nuevo Turno
        </button>
      </div>
    </div>

    <!-- View Controls -->
    <div class="view-controls">
      <div class="row">
        <div class="col-md-6">
          <div class="btn-group" role="group">
            <button 
              v-for="view in viewOptions" 
              :key="view.value"
              class="btn" 
              :class="currentView === view.value ? 'btn-primary' : 'btn-outline-primary'"
              @click="currentView = view.value"
            >
              <i :class="view.icon" class="me-2"></i>
              {{ view.label }}
            </button>
          </div>
        </div>
        <div class="col-md-6">
          <div class="d-flex gap-2">
            <div class="filter-control">
              <select v-model="statusFilter" class="form-select">
                <option value="">Todos los estados</option>
                <option value="pending">Pendientes</option>
                <option value="confirmed">Confirmados</option>
                <option value="completed">Completados</option>
                <option value="cancelled">Cancelados</option>
              </select>
            </div>
            <div class="date-control">
              <input 
                type="date" 
                class="form-control" 
                v-model="selectedDate"
              >
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Calendar View -->
    <div v-if="currentView === 'calendar'" class="calendar-view">
      <div class="card">
        <div class="card-header">
          <div class="d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Calendario de Turnos</h5>
            <div class="calendar-nav">
              <button class="btn btn-sm btn-outline-secondary" @click="prevMonth">
                <i class="fas fa-chevron-left"></i>
              </button>
              <span class="mx-3 fw-bold">{{ currentMonth }}</span>
              <button class="btn btn-sm btn-outline-secondary" @click="nextMonth">
                <i class="fas fa-chevron-right"></i>
              </button>
            </div>
          </div>
        </div>
        <div class="card-body">
          <div class="calendar-container">
            <div class="calendar-header">
              <div v-for="day in weekDays" :key="day" class="calendar-day-header">
                {{ day }}
              </div>
            </div>
            <div class="calendar-grid">
              <div 
                v-for="day in calendarDays" 
                :key="day.date"
                class="calendar-day"
                :class="{
                  'current-month': day.isCurrentMonth,
                  'today': day.isToday,
                  'has-appointments': day.hasAppointments
                }"
                @click="selectDate(day.date)"
              >
                <div class="day-number">{{ day.day }}</div>
                <div class="appointments-count" v-if="day.appointmentCount > 0">
                  <span class="badge bg-primary">{{ day.appointmentCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- List View -->
    <div v-else class="list-view">
      <div class="card">
        <div class="card-header">
          <h5 class="mb-0">Lista de Turnos</h5>
        </div>
        <div class="card-body">
          <div v-if="loading" class="text-center py-5">
            <LoadingSpinner message="Cargando turnos..." />
          </div>

          <div v-else-if="filteredAppointments.length === 0" class="text-center py-5">
            <i class="fas fa-calendar-times fa-3x text-muted mb-3"></i>
            <h5>No hay turnos programados</h5>
            <p class="text-muted">No tienes turnos para la fecha seleccionada</p>
            <button class="btn btn-primary" @click="showNewAppointmentModal">
              <i class="fas fa-plus me-2"></i> Crear primer turno
            </button>
          </div>

          <div v-else class="appointments-list">
            <div 
              v-for="appointment in filteredAppointments" 
              :key="appointment.id"
              class="appointment-item"
              :class="appointment.status"
            >
              <div class="appointment-time">
                <div class="time">{{ appointment.time }}</div>
                <div class="duration">{{ appointment.duration }} min</div>
              </div>
              
              <div class="appointment-info">
                <div class="client-info">
                  <img :src="appointment.clientAvatar" :alt="appointment.clientName" class="client-avatar">
                  <div>
                    <h6 class="client-name">{{ appointment.clientName }}</h6>
                    <p class="service-name">{{ appointment.service }}</p>
                  </div>
                </div>
                
                <div class="appointment-meta">
                  <span class="status-badge" :class="appointment.status">
                    {{ appointment.statusText }}
                  </span>
                  <span class="price">${{ appointment.price }}</span>
                </div>
              </div>
              
              <div class="appointment-actions">
                <button 
                  class="btn btn-sm btn-outline-primary"
                  @click="editAppointment(appointment)"
                  title="Editar turno"
                >
                  <i class="fas fa-edit"></i>
                </button>
                <button 
                  class="btn btn-sm"
                  :class="appointment.status === 'confirmed' ? 'btn-outline-success' : 'btn-outline-warning'"
                  @click="toggleAppointmentStatus(appointment)"
                  :title="appointment.status === 'confirmed' ? 'Completar' : 'Confirmar'"
                >
                  <i :class="appointment.status === 'confirmed' ? 'fas fa-check' : 'fas fa-check-double'"></i>
                </button>
                <button 
                  class="btn btn-sm btn-outline-danger"
                  @click="cancelAppointment(appointment)"
                  title="Cancelar turno"
                >
                  <i class="fas fa-times"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- New Appointment Modal -->
    <ModalDialog 
      :show="showModal" 
      :title="modalTitle"
      size="large"
      @update:show="showModal = false"
    >
      <AppointmentForm 
        :appointment="editingAppointment"
        @submit="handleAppointmentSubmit"
        @cancel="showModal = false"
      />
    </ModalDialog>

    <!-- Appointment Details Modal -->
    <ModalDialog 
      :show="showDetailsModal" 
      title="Detalles del Turno"
      @update:show="showDetailsModal = false"
    >
      <AppointmentDetails 
        :appointment="selectedAppointment"
        @edit="editSelectedAppointment"
        @status-change="handleStatusChange"
        @close="showDetailsModal = false"
      />
    </ModalDialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import ModalDialog from '@/components/common/ModalDialog.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import AppointmentForm from '@/components/barber/AppointmentForm.vue';
import AppointmentDetails from '@/components/barber/AppointmentDetails.vue';

export default {
  name: 'BarberAppointmentsView',
  components: {
    ModalDialog,
    LoadingSpinner,
    AppointmentForm,
    AppointmentDetails
  },
  setup() {
    const store = useStore();
    
    const loading = ref(true);
    const currentView = ref('list');
    const statusFilter = ref('');
    const selectedDate = ref(new Date().toISOString().split('T')[0]);
    const currentMonth = ref(new Date());
    const showModal = ref(false);
    const showDetailsModal = ref(false);
    const editingAppointment = ref(null);
    const selectedAppointment = ref(null);
    
    const viewOptions = [
      { value: 'list', label: 'Lista', icon: 'fas fa-list' },
      { value: 'calendar', label: 'Calendario', icon: 'fas fa-calendar' }
    ];
    
    const weekDays = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
    
    const appointments = ref([
      {
        id: 1,
        clientName: "María González",
        clientAvatar: "https://ui-avatars.com/api/?name=Maria+Gonzalez&background=6f42c1&color=fff",
        service: "Corte + Color",
        date: new Date().toISOString().split('T')[0],
        time: "10:00",
        duration: 90,
        price: 1200,
        status: "confirmed",
        statusText: "Confirmado",
        notes: "Cliente preferente - Llegar 15 min antes"
      },
      {
        id: 2,
        clientName: "Juan Pérez",
        clientAvatar: "https://ui-avatars.com/api/?name=Juan+Perez&background=20c997&color=fff",
        service: "Corte de Cabello",
        date: new Date().toISOString().split('T')[0],
        time: "11:30",
        duration: 45,
        price: 500,
        status: "pending",
        statusText: "Pendiente",
        notes: "Primera visita - Consultar por alergias"
      },
      {
        id: 3,
        clientName: "Laura Sánchez",
        clientAvatar: "https://ui-avatars.com/api/?name=Laura+Sanchez&background=dc3545&color=fff",
        service: "Tratamiento Capilar",
        date: new Date(Date.now() + 86400000).toISOString().split('T')[0],
        time: "09:00",
        duration: 60,
        price: 800,
        status: "confirmed",
        statusText: "Confirmado",
        notes: "Traer muestras de productos"
      }
    ]);
    
    const filteredAppointments = computed(() => {
      let filtered = appointments.value;
      
      // Filtrar por fecha
      if (selectedDate.value) {
        filtered = filtered.filter(app => app.date === selectedDate.value);
      }
      
      // Filtrar por estado
      if (statusFilter.value) {
        filtered = filtered.filter(app => app.status === statusFilter.value);
      }
      
      // Ordenar por hora
      return filtered.sort((a, b) => a.time.localeCompare(b.time));
    });
    
    const calendarDays = computed(() => {
      const year = currentMonth.value.getFullYear();
      const month = currentMonth.value.getMonth();
      
      // Primer día del mes
      const firstDay = new Date(year, month, 1);
      // Último día del mes
      const lastDay = new Date(year, month + 1, 0);
      // Días en el mes
      const daysInMonth = lastDay.getDate();
      // Día de la semana del primer día (0 = Domingo, 1 = Lunes, etc.)
      const firstDayOfWeek = firstDay.getDay();
      
      const days = [];
      const today = new Date();
      
      // Días del mes anterior
      for (let i = 0; i < firstDayOfWeek; i++) {
        const date = new Date(year, month, -i);
        days.unshift({
          date: date.toISOString().split('T')[0],
          day: date.getDate(),
          isCurrentMonth: false,
          isToday: false,
          hasAppointments: false,
          appointmentCount: 0
        });
      }
      
      // Días del mes actual
      for (let i = 1; i <= daysInMonth; i++) {
        const date = new Date(year, month, i);
        const dateStr = date.toISOString().split('T')[0];
        const dayAppointments = appointments.value.filter(app => app.date === dateStr);
        
        days.push({
          date: dateStr,
          day: i,
          isCurrentMonth: true,
          isToday: date.toDateString() === today.toDateString(),
          hasAppointments: dayAppointments.length > 0,
          appointmentCount: dayAppointments.length
        });
      }
      
      // Días del próximo mes para completar la cuadrícula
      const totalCells = 42; // 6 semanas * 7 días
      const remainingCells = totalCells - days.length;
      
      for (let i = 1; i <= remainingCells; i++) {
        const date = new Date(year, month + 1, i);
        days.push({
          date: date.toISOString().split('T')[0],
          day: i,
          isCurrentMonth: false,
          isToday: false,
          hasAppointments: false,
          appointmentCount: 0
        });
      }
      
      return days;
    });
    
    const modalTitle = computed(() => 
      editingAppointment.value ? 'Editar Turno' : 'Nuevo Turno'
    );
    
    const loadAppointments = async () => {
      try {
        loading.value = true;
        // Simular carga de datos
        await new Promise(resolve => setTimeout(resolve, 1000));
      } catch (error) {
        console.error('Error loading appointments:', error);
      } finally {
        loading.value = false;
      }
    };
    
    const showNewAppointmentModal = () => {
      editingAppointment.value = null;
      showModal.value = true;
    };
    
    const editAppointment = (appointment) => {
      editingAppointment.value = { ...appointment };
      showModal.value = true;
    };
    
    const handleAppointmentSubmit = (appointmentData) => {
      if (editingAppointment.value) {
        // Editar turno existente
        const index = appointments.value.findIndex(a => a.id === editingAppointment.value.id);
        if (index !== -1) {
          appointments.value[index] = { ...appointments.value[index], ...appointmentData };
        }
      } else {
        // Crear nuevo turno
        const newAppointment = {
          id: Math.max(...appointments.value.map(a => a.id)) + 1,
          ...appointmentData,
          status: 'pending',
          statusText: 'Pendiente'
        };
        appointments.value.push(newAppointment);
      }
      
      showModal.value = false;
      editingAppointment.value = null;
    };
    
    const toggleAppointmentStatus = (appointment) => {
      if (appointment.status === 'confirmed') {
        appointment.status = 'completed';
        appointment.statusText = 'Completado';
      } else {
        appointment.status = 'confirmed';
        appointment.statusText = 'Confirmado';
      }
    };
    
    const cancelAppointment = (appointment) => {
      if (confirm('¿Estás seguro de que quieres cancelar este turno?')) {
        appointment.status = 'cancelled';
        appointment.statusText = 'Cancelado';
      }
    };
    
    const selectDate = (date) => {
      selectedDate.value = date;
      currentView.value = 'list';
    };
    
    const prevMonth = () => {
      currentMonth.value = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth() - 1, 1);
    };
    
    const nextMonth = () => {
      currentMonth.value = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth() + 1, 1);
    };
    
    onMounted(() => {
      loadAppointments();
    });
    
    return {
      loading,
      currentView,
      statusFilter,
      selectedDate,
      currentMonth,
      showModal,
      showDetailsModal,
      editingAppointment,
      selectedAppointment,
      viewOptions,
      weekDays,
      filteredAppointments,
      calendarDays,
      modalTitle,
      showNewAppointmentModal,
      editAppointment,
      handleAppointmentSubmit,
      toggleAppointmentStatus,
      cancelAppointment,
      selectDate,
      prevMonth,
      nextMonth
    };
  }
};
</script>

<style scoped>
.barber-appointments {
  padding: 20px;
}

.appointments-header {
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

.view-controls {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 25px;
}

.view-controls .btn-group {
  width: 100%;
}

.view-controls .btn {
  flex: 1;
}

.filter-control,
.date-control {
  flex: 1;
}

/* Calendar View */
.calendar-container {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #6f42c1;
  color: white;
  font-weight: 600;
  text-align: center;
}

.calendar-day-header {
  padding: 15px;
  border-right: 1px solid rgba(255, 255, 255, 0.1);
}

.calendar-day-header:last-child {
  border-right: none;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-template-rows: repeat(6, 1fr);
  gap: 1px;
  background: #e9ecef;
}

.calendar-day {
  background: white;
  min-height: 80px;
  padding: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.calendar-day:hover {
  background: #f8f9fa;
  transform: scale(1.02);
  z-index: 1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.calendar-day.current-month {
  background: white;
}

.calendar-day:not(.current-month) {
  background: #f8f9fa;
  color: #6c757d;
}

.calendar-day.today {
  background: #e7f5ff;
  border: 2px solid #0d6efd;
}

.calendar-day.has-appointments {
  background: #f0f4ff;
}

.day-number {
  font-weight: 600;
  margin-bottom: 5px;
}

.appointments-count {
  position: absolute;
  bottom: 5px;
  right: 5px;
}

/* List View */
.appointments-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.appointment-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: white;
  border-radius: 12px;
  border-left: 4px solid #6f42c1;
  transition: all 0.3s ease;
}

.appointment-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.appointment-item.confirmed {
  border-left-color: #198754;
}

.appointment-item.pending {
  border-left-color: #ffc107;
}

.appointment-item.completed {
  border-left-color: #0dcaf0;
}

.appointment-item.cancelled {
  border-left-color: #dc3545;
}

.appointment-time {
  min-width: 80px;
  text-align: center;
  padding-right: 15px;
  border-right: 1px solid #e9ecef;
}

.appointment-time .time {
  font-size: 1.2rem;
  font-weight: 700;
  color: #2c3e50;
}

.appointment-time .duration {
  font-size: 0.8rem;
  color: #6c757d;
}

.appointment-info {
  flex: 1;
  padding: 0 20px;
}

.client-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.client-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.client-name {
  margin: 0;
  font-weight: 600;
  color: #2c3e50;
}

.service-name {
  margin: 0;
  color: #6c757d;
  font-size: 0.9rem;
}

.appointment-meta {
  display: flex;
  align-items: center;
  gap: 15px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.status-badge.confirmed {
  background: #d1e7dd;
  color: #0f5132;
}

.status-badge.pending {
  background: #fff3cd;
  color: #856404;
}

.status-badge.completed {
  background: #cfe2ff;
  color: #084298;
}

.status-badge.cancelled {
  background: #f8d7da;
  color: #842029;
}

.price {
  font-weight: 700;
  color: #198754;
}

.appointment-actions {
  display: flex;
  gap: 8px;
}

.appointment-actions .btn {
  border-radius: 8px;
  padding: 6px 10px;
}

@media (max-width: 992px) {
  .barber-appointments {
    padding: 15px;
  }
  
  .appointments-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .view-controls .row {
    gap: 15px;
  }
  
  .calendar-day {
    min-height: 60px;
    padding: 5px;
  }
  
  .appointment-item {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
  }
  
  .appointment-time {
    border-right: none;
    border-bottom: 1px solid #e9ecef;
    padding-right: 0;
    padding-bottom: 15px;
    text-align: left;
  }
  
  .appointment-info {
    padding: 0;
  }
  
  .appointment-meta {
    justify-content: space-between;
  }
  
  .appointment-actions {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .calendar-header {
    display: none;
  }
  
  .calendar-grid {
    grid-template-columns: 1fr;
  }
  
  .calendar-day {
    min-height: auto;
    padding: 15px;
    border-bottom: 1px solid #e9ecef;
  }
  
  .appointment-meta {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
}
</style>