<template>
  <div class="appointment-manager">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h3>Gestión de Turnos</h3>
      <div class="btn-group">
        <button class="btn btn-outline-primary" :class="{ active: viewMode === 'day' }" @click="viewMode = 'day'">
          <i class="fas fa-calendar-day me-1"></i> Día
        </button>
        <button class="btn btn-outline-primary" :class="{ active: viewMode === 'week' }" @click="viewMode = 'week'">
          <i class="fas fa-calendar-week me-1"></i> Semana
        </button>
        <button class="btn btn-outline-primary" :class="{ active: viewMode === 'month' }" @click="viewMode = 'month'">
          <i class="fas fa-calendar-alt me-1"></i> Mes
        </button>
      </div>
    </div>

    <div class="row mb-4">
      <div class="col-md-8">
        <div class="card">
          <div class="card-header d-flex justify-content-between align-items-center">
            <div>
              <button class="btn btn-sm btn-outline-secondary me-2" @click="previousPeriod">
                <i class="fas fa-chevron-left"></i>
              </button>
              <span class="fw-bold">{{ periodTitle }}</span>
              <button class="btn btn-sm btn-outline-secondary ms-2" @click="nextPeriod">
                <i class="fas fa-chevron-right"></i>
              </button>
            </div>
            <button class="btn btn-primary btn-sm" @click="goToToday">
              <i class="fas fa-calendar-day me-1"></i> Hoy
            </button>
          </div>
          <div class="card-body">
            <div v-if="viewMode === 'day'" class="day-view">
              <div v-for="timeSlot in dayTimeSlots" :key="timeSlot" class="time-slot-item">
                <div class="time-label">{{ timeSlot }}</div>
                <div class="appointment-slots">
                  <div v-for="appointment in getAppointmentsForTimeSlot(timeSlot)" 
                       :key="appointment.id" 
                       class="appointment-block"
                       :class="appointment.status"
                       @click="selectAppointment(appointment)">
                    <div class="appointment-client">{{ appointment.clientName }}</div>
                    <div class="appointment-service">{{ appointment.service }}</div>
                  </div>
                </div>
              </div>
            </div>
            
            <div v-else-if="viewMode === 'week'" class="week-view">
              <div class="week-grid">
                <div class="time-column">
                  <div v-for="timeSlot in weekTimeSlots" :key="timeSlot" class="time-slot">
                    {{ timeSlot }}
                  </div>
                </div>
                <div v-for="day in weekDays" :key="day" class="day-column">
                  <div class="day-header">{{ day }}</div>
                  <div v-for="timeSlot in weekTimeSlots" :key="timeSlot" class="time-slot-cell"
                       @click="openNewAppointmentModal(day, timeSlot)">
                    <div v-for="appointment in getAppointmentsForDayAndTime(day, timeSlot)" 
                         :key="appointment.id" 
                         class="appointment-block"
                         :class="appointment.status"
                         @click.stop="selectAppointment(appointment)">
                      <div class="appointment-client">{{ appointment.clientName }}</div>
                      <div class="appointment-service">{{ appointment.service }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <div v-else class="month-view">
              <!-- Vista de mes - por implementar -->
              <div class="text-center py-5">
                <i class="fas fa-calendar-alt fa-3x text-muted mb-3"></i>
                <p>Vista mensual en desarrollo</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-4">
        <div class="card">
          <div class="card-header">
            <h5 class="mb-0">Resumen del Día</h5>
          </div>
          <div class="card-body">
            <div v-if="selectedDateAppointments.length === 0" class="text-center py-3">
              <p class="text-muted">No hay turnos para esta fecha</p>
            </div>
            <div v-else>
              <div v-for="appointment in selectedDateAppointments" :key="appointment.id" 
                   class="appointment-summary">
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <div class="fw-bold">{{ appointment.time }}</div>
                    <div>{{ appointment.clientName }}</div>
                    <small class="text-muted">{{ appointment.service }}</small>
                  </div>
                  <span class="badge" :class="statusBadgeClass(appointment.status)">
                    {{ appointment.statusText }}
                  </span>
                </div>
                <hr>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal para crear/editar turno -->
    <div class="modal fade" id="appointmentModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ isEditing ? 'Editar Turno' : 'Nuevo Turno' }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form v-if="!isEditing">
              <div class="mb-3">
                <label class="form-label">Cliente</label>
                <select class="form-select" v-model="newAppointment.clientId" required>
                  <option value="">Seleccionar cliente</option>
                  <option v-for="client in clients" :key="client.id" :value="client.id">
                    {{ client.name }}
                  </option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label">Servicio</label>
                <select class="form-select" v-model="newAppointment.serviceId" required>
                  <option value="">Seleccionar servicio</option>
                  <option v-for="service in services" :key="service.id" :value="service.id">
                    {{ service.name }} ({{ service.duration }} min) - ${{ service.price }}
                  </option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label">Fecha y Hora</label>
                <input type="datetime-local" class="form-control" v-model="newAppointment.datetime" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Notas (opcional)</label>
                <textarea class="form-control" v-model="newAppointment.notes" rows="3"></textarea>
              </div>
            </form>
            <div v-else>
              <!-- Formulario de edición -->
              <p>Formulario de edición para el turno seleccionado</p>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
            <button type="button" class="btn btn-primary" @click="saveAppointment">
              {{ isEditing ? 'Guardar Cambios' : 'Crear Turno' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { Modal } from 'bootstrap';

export default {
  name: 'AppointmentManager',
  setup() {
    const viewMode = ref('week');
    const currentDate = ref(new Date());
    const selectedAppointment = ref(null);
    const isEditing = ref(false);
    
    const dayTimeSlots = [
      '09:00', '09:30', '10:00', '10:30', '11:00', '11:30', 
      '12:00', '12:30', '13:00', '13:30', '14:00', '14:30',
      '15:00', '15:30', '16:00', '16:30', '17:00', '17:30',
      '18:00', '18:30', '19:00', '19:30', '20:00'
    ];
    
    const weekTimeSlots = [
      '09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00', '19:00'
    ];
    
    const weekDays = computed(() => {
      const days = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];
      const startOfWeek = new Date(currentDate.value);
      startOfWeek.setDate(startOfWeek.getDate() - startOfWeek.getDay() + 1); // Lunes como primer día
      
      return days.map((day, index) => {
        const date = new Date(startOfWeek);
        date.setDate(startOfWeek.getDate() + index);
        return {
          name: day,
          date: date.toLocaleDateString('es-ES', { day: 'numeric', month: 'short' }),
          fullDate: date
        };
      });
    });
    
    const periodTitle = computed(() => {
      if (viewMode.value === 'day') {
        return currentDate.value.toLocaleDateString('es-ES', { 
          weekday: 'long', 
          year: 'numeric', 
          month: 'long', 
          day: 'numeric' 
        });
      } else if (viewMode.value === 'week') {
        const startOfWeek = new Date(currentDate.value);
        startOfWeek.setDate(startOfWeek.getDate() - startOfWeek.getDay() + 1);
        const endOfWeek = new Date(startOfWeek);
        endOfWeek.setDate(startOfWeek.getDate() + 6);
        
        return `Semana del ${startOfWeek.toLocaleDateString('es-ES', { day: 'numeric' })} al ${endOfWeek.toLocaleDateString('es-ES', { day: 'numeric', month: 'long' })}`;
      } else {
        return currentDate.value.toLocaleDateString('es-ES', { 
          year: 'numeric', 
          month: 'long' 
        });
      }
    });
    
    const appointments = ref([
      {
        id: 1,
        clientName: "Juan Pérez",
        service: "Corte de Cabello",
        duration: 30,
        date: new Date(),
        time: "10:00",
        status: "confirmed",
        statusText: "Confirmado"
      },
      {
        id: 2,
        clientName: "María García",
        service: "Tintura",
        duration: 120,
        date: new Date(),
        time: "11:00",
        status: "confirmed",
        statusText: "Confirmado"
      },
      {
        id: 3,
        clientName: "Carlos López",
        service: "Corte + Barba",
        duration: 45,
        date: new Date(Date.now() + 86400000),
        time: "12:30",
        status: "pending",
        statusText: "Pendiente"
      }
    ]);
    
    const clients = ref([
      { id: 1, name: "Juan Pérez" },
      { id: 2, name: "María García" },
      { id: 3, name: "Carlos López" },
      { id: 4, name: "Ana Martínez" }
    ]);
    
    const services = ref([
      { id: 1, name: "Corte de Cabello", duration: 30, price: 500 },
      { id: 2, name: "Tintura", duration: 120, price: 1200 },
      { id: 3, name: "Corte + Barba", duration: 45, price: 700 },
      { id: 4, name: "Manicura", duration: 45, price: 400 }
    ]);
    
    const newAppointment = ref({
      clientId: '',
      serviceId: '',
      datetime: '',
      notes: ''
    });
    
    const selectedDateAppointments = computed(() => {
      return appointments.value.filter(app => {
        const appDate = new Date(app.date);
        const current = new Date(currentDate.value);
        return appDate.toDateString() === current.toDateString();
      });
    });
    
    const getAppointmentsForTimeSlot = (timeSlot) => {
      return appointments.value.filter(app => {
        const appDate = new Date(app.date);
        const current = new Date(currentDate.value);
        return appDate.toDateString() === current.toDateString() && app.time === timeSlot;
      });
    };
    
    const getAppointmentsForDayAndTime = (day, timeSlot) => {
      return appointments.value.filter(app => {
        const appDate = new Date(app.date);
        const dayDate = day.fullDate;
        return appDate.toDateString() === dayDate.toDateString() && 
               app.time.startsWith(timeSlot);
      });
    };
    
    const statusBadgeClass = (status) => {
      switch (status) {
        case 'confirmed': return 'bg-success';
        case 'completed': return 'bg-primary';
        case 'cancelled': return 'bg-danger';
        default: return 'bg-warning';
      }
    };
    
    const previousPeriod = () => {
      if (viewMode.value === 'day') {
        currentDate.value.setDate(currentDate.value.getDate() - 1);
      } else if (viewMode.value === 'week') {
        currentDate.value.setDate(currentDate.value.getDate() - 7);
      } else {
        currentDate.value.setMonth(currentDate.value.getMonth() - 1);
      }
    };
    
    const nextPeriod = () => {
      if (viewMode.value === 'day') {
        currentDate.value.setDate(currentDate.value.getDate() + 1);
      } else if (viewMode.value === 'week') {
        currentDate.value.setDate(currentDate.value.getDate() + 7);
      } else {
        currentDate.value.setMonth(currentDate.value.getMonth() + 1);
      }
    };
    
    const goToToday = () => {
      currentDate.value = new Date();
    };
    
    const selectAppointment = (appointment) => {
      selectedAppointment.value = appointment;
      isEditing.value = true;
      const modal = new Modal(document.getElementById('appointmentModal'));
      modal.show();
    };
    
    const openNewAppointmentModal = (day, timeSlot) => {
      selectedAppointment.value = null;
      isEditing.value = false;
      
      // Establecer fecha y hora por defecto
      const date = day.fullDate;
      const [hours, minutes] = timeSlot.split(':');
      date.setHours(parseInt(hours), parseInt(minutes || 0));
      
      newAppointment.value.datetime = date.toISOString().slice(0, 16);
      
      const modal = new Modal(document.getElementById('appointmentModal'));
      modal.show();
    };
    
    const saveAppointment = () => {
      // En una app real, haríamos una llamada a la API para guardar el turno
      console.log('Guardando turno:', isEditing.value ? selectedAppointment.value : newAppointment.value);
      
      // Cerrar modal
      const modal = Modal.getInstance(document.getElementById('appointmentModal'));
      modal.hide();
      
      alert(isEditing.value ? 'Turno actualizado correctamente!' : 'Turno creado correctamente!');
    };
    
    onMounted(() => {
      // Inicializar modal
      const modalElement = document.getElementById('appointmentModal');
      modalElement.addEventListener('hidden.bs.modal', () => {
        selectedAppointment.value = null;
        isEditing.value = false;
        newAppointment.value = {
          clientId: '',
          serviceId: '',
          datetime: '',
          notes: ''
        };
      });
    });
    
    return {
      viewMode,
      currentDate,
      selectedAppointment,
      isEditing,
      dayTimeSlots,
      weekTimeSlots,
      weekDays,
      periodTitle,
      appointments,
      clients,
      services,
      newAppointment,
      selectedDateAppointments,
      getAppointmentsForTimeSlot,
      getAppointmentsForDayAndTime,
      statusBadgeClass,
      previousPeriod,
      nextPeriod,
      goToToday,
      selectAppointment,
      openNewAppointmentModal,
      saveAppointment
    };
  }
};
</script>

<style scoped>
.appointment-manager {
  padding: 20px;
}

.day-view .time-slot-item {
  display: flex;
  border-bottom: 1px solid #eee;
  padding: 10px 0;
}

.day-view .time-label {
  width: 80px;
  font-weight: 500;
}

.day-view .appointment-slots {
  flex: 1;
}

.week-view .week-grid {
  display: flex;
}

.week-view .time-column {
  width: 60px;
}

.week-view .time-slot {
  height: 60px;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
}

.week-view .day-column {
  flex: 1;
  border-left: 1px solid #eee;
}

.week-view .day-header {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  border-bottom: 1px solid #eee;
}

.week-view .time-slot-cell {
  height: 60px;
  border-bottom: 1px solid #eee;
  position: relative;
  cursor: pointer;
}

.week-view .time-slot-cell:hover {
  background-color: #f8f9fa;
}

.appointment-block {
  background-color: #6f42c1;
  color: white;
  padding: 5px;
  border-radius: 4px;
  margin: 2px;
  font-size: 0.8rem;
  cursor: pointer;
}

.appointment-block.confirmed {
  background-color: #198754;
}

.appointment-block.completed {
  background-color: #0d6efd;
}

.appointment-block.cancelled {
  background-color: #dc3545;
}

.appointment-block.pending {
  background-color: #ffc107;
  color: #000;
}

.appointment-client {
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.appointment-service {
  font-size: 0.7rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.appointment-summary {
  margin-bottom: 10px;
}

@media (max-width: 768px) {
  .appointment-manager {
    padding: 15px;
  }
  
  .week-view {
    font-size: 0.8rem;
  }
  
  .day-view .time-label {
    width: 60px;
  }
}
</style>