<template>
  <div class="barber-availability">
    <!-- Header -->
    <div class="availability-header">
      <div class="header-content">
        <h1>Disponibilidad</h1>
        <p class="text-muted">Configura tus horarios de trabajo y días libres</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-outline-primary me-2" @click="showDayOffModal">
          <i class="fas fa-calendar-times me-2"></i> Agregar día libre
        </button>
        <button class="btn btn-primary" @click="saveAvailability">
          <i class="fas fa-save me-2"></i> Guardar Cambios
        </button>
      </div>
    </div>

    <!-- Weekly Schedule -->
    <div class="card mb-4">
      <div class="card-header">
        <h5 class="mb-0">Horario Semanal</h5>
      </div>
      <div class="card-body">
        <div class="weekly-schedule">
          <div 
            v-for="day in daysOfWeek" 
            :key="day.key"
            class="schedule-day"
            :class="{ 'day-off': isDayOff(day.key) }"
          >
            <div class="day-header">
              <div class="day-name">
                <input 
                  type="checkbox" 
                  :id="`day-${day.key}`"
                  class="day-checkbox"
                  v-model="schedule[day.key].enabled"
                >
                <label :for="`day-${day.key}`" class="day-label">
                  {{ day.name }}
                </label>
              </div>
              <div v-if="schedule[day.key].enabled" class="day-hours">
                {{ formatTime(schedule[day.key].start) }} - {{ formatTime(schedule[day.key].end) }}
              </div>
              <div v-else class="day-off-label">
                Cerrado
              </div>
            </div>
            
            <div v-if="schedule[day.key].enabled" class="time-slots">
              <div class="time-input-group">
                <label>Inicio</label>
                <select class="form-select" v-model="schedule[day.key].start">
                  <option v-for="time in timeOptions" :key="`${day.key}-start-${time}`" :value="time">
                    {{ formatTime(time) }}
                  </option>
                </select>
              </div>
              
              <div class="time-input-group">
                <label>Fin</label>
                <select class="form-select" v-model="schedule[day.key].end">
                  <option v-for="time in timeOptions" :key="`${day.key}-end-${time}`" :value="time">
                    {{ formatTime(time) }}
                  </option>
                </select>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Time Slots Configuration -->
    <div class="card mb-4">
      <div class="card-header">
        <h5 class="mb-0">Configuración de Turnos</h5>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="col-md-6">
            <div class="mb-3">
              <label class="form-label">Duración de Turnos (minutos)</label>
              <select class="form-select" v-model="slotDuration">
                <option value="15">15 minutos</option>
                <option value="30">30 minutos</option>
                <option value="45">45 minutos</option>
                <option value="60">60 minutos</option>
                <option value="90">90 minutos</option>
                <option value="120">120 minutos</option>
              </select>
            </div>
          </div>
          <div class="col-md-6">
            <div class="mb-3">
              <label class="form-label">Máximo de turnos simultáneos</label>
              <select class="form-select" v-model="maxParallelAppointments">
                <option value="1">1 turno</option>
                <option value="2">2 turnos</option>
                <option value="3">3 turnos</option>
                <option value="4">4 turnos</option>
                <option value="5">5 turnos</option>
              </select>
            </div>
          </div>
        </div>
        
        <div class="form-check form-switch mb-3">
          <input class="form-check-input" type="checkbox" id="bufferTime" v-model="enableBufferTime">
          <label class="form-check-label" for="bufferTime">
            Agregar tiempo entre turnos
          </label>
        </div>
        
        <div v-if="enableBufferTime" class="row">
          <div class="col-md-6">
            <div class="mb-3">
              <label class="form-label">Tiempo entre turnos (minutos)</label>
              <select class="form-select" v-model="bufferTime">
                <option value="5">5 minutos</option>
                <option value="10">10 minutos</option>
                <option value="15">15 minutos</option>
                <option value="20">20 minutos</option>
                <option value="30">30 minutos</option>
              </select>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Days Off -->
    <div class="card">
      <div class="card-header">
        <h5 class="mb-0">Días Libres Programados</h5>
      </div>
      <div class="card-body">
        <div v-if="daysOff.length === 0" class="text-center py-4">
          <i class="fas fa-umbrella-beach fa-2x text-muted mb-3"></i>
          <p class="text-muted">No hay días libres programados</p>
        </div>
        
        <div v-else class="days-off-list">
          <div v-for="dayOff in daysOff" :key="dayOff.id" class="day-off-item">
            <div class="day-off-info">
              <i class="fas fa-calendar-times text-danger"></i>
              <div>
                <div class="day-off-date">{{ formatDayOffDate(dayOff.date) }}</div>
                <div class="day-off-reason">{{ dayOff.reason || 'Día libre' }}</div>
              </div>
            </div>
            <button class="btn btn-sm btn-outline-danger" @click="removeDayOff(dayOff.id)">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Day Off Modal -->
    <ModalDialog 
      :show="showDayOffModalVisible" 
      title="Agregar Día Libre"
      size="small"
      @update:show="showDayOffModalVisible = false"
    >
      <div class="day-off-form">
        <div class="mb-3">
          <label class="form-label">Fecha</label>
          <input type="date" class="form-control" v-model="newDayOff.date" :min="minDate">
        </div>
        <div class="mb-3">
          <label class="form-label">Motivo (opcional)</label>
          <input type="text" class="form-control" v-model="newDayOff.reason" placeholder="Ej: Vacaciones, Feriado, etc.">
        </div>
        <div class="form-actions">
          <button type="button" class="btn btn-secondary" @click="showDayOffModalVisible = false">
            Cancelar
          </button>
          <button type="button" class="btn btn-primary" @click="addDayOff">
            Agregar
          </button>
        </div>
      </div>
    </ModalDialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import ModalDialog from '@/components/common/ModalDialog.vue';

export default {
  name: 'BarberAvailabilityView',
  components: {
    ModalDialog
  },
  setup() {
    const showDayOffModalVisible = ref(false);
    const saving = ref(false);
    
    const daysOfWeek = [
      { key: 'monday', name: 'Lunes' },
      { key: 'tuesday', name: 'Martes' },
      { key: 'wednesday', name: 'Miércoles' },
      { key: 'thursday', name: 'Jueves' },
      { key: 'friday', name: 'Viernes' },
      { key: 'saturday', name: 'Sábado' },
      { key: 'sunday', name: 'Domingo' }
    ];
    
    const schedule = reactive({
      monday: { enabled: true, start: 540, end: 1020 }, // 9:00 - 17:00
      tuesday: { enabled: true, start: 540, end: 1020 },
      wednesday: { enabled: true, start: 540, end: 1020 },
      thursday: { enabled: true, start: 540, end: 1020 },
      friday: { enabled: true, start: 540, end: 1020 },
      saturday: { enabled: true, start: 600, end: 900 }, // 10:00 - 15:00
      sunday: { enabled: false, start: 540, end: 1020 }
    });
    
    const slotDuration = ref(30);
    const maxParallelAppointments = ref(1);
    const enableBufferTime = ref(true);
    const bufferTime = ref(10);
    
    const daysOff = ref([
      { id: 1, date: '2023-12-25', reason: 'Navidad' },
      { id: 2, date: '2024-01-01', reason: 'Año Nuevo' }
    ]);
    
    const newDayOff = reactive({
      date: '',
      reason: ''
    });
    
    // Generate time options (in minutes from midnight)
    const timeOptions = ref([]);
    for (let i = 0; i < 24 * 60; i += 15) {
      timeOptions.value.push(i);
    }
    
    const formatTime = (minutes) => {
      const hours = Math.floor(minutes / 60);
      const mins = minutes % 60;
      return `${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}`;
    };
    
    const isDayOff = (dayKey) => {
      return !schedule[dayKey].enabled;
    };
    
    const showDayOffModal = () => {
      newDayOff.date = '';
      newDayOff.reason = '';
      showDayOffModalVisible.value = true;
    };
    
    const addDayOff = () => {
      if (newDayOff.date) {
        daysOff.value.push({
          id: Date.now(),
          date: newDayOff.date,
          reason: newDayOff.reason
        });
        showDayOffModalVisible.value = false;
      }
    };
    
    const removeDayOff = (id) => {
      daysOff.value = daysOff.value.filter(day => day.id !== id);
    };
    
    const formatDayOffDate = (dateString) => {
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', { 
        weekday: 'long', 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
      });
    };
    
    const saveAvailability = async () => {
      saving.value = true;
      try {
        // Simular guardado
        await new Promise(resolve => setTimeout(resolve, 1000));
        console.log('Disponibilidad guardada:', {
          schedule,
          slotDuration: slotDuration.value,
          maxParallelAppointments: maxParallelAppointments.value,
          bufferTime: enableBufferTime.value ? bufferTime.value : 0,
          daysOff: daysOff.value
        });
        // En una app real, aquí harías la llamada a la API
      } catch (error) {
        console.error('Error saving availability:', error);
      } finally {
        saving.value = false;
      }
    };
    
    onMounted(() => {
      // Set minimum date for day off as tomorrow
      const tomorrow = new Date();
      tomorrow.setDate(tomorrow.getDate() + 1);
      newDayOff.date = tomorrow.toISOString().split('T')[0];
    });
    
    const minDate = new Date().toISOString().split('T')[0];
    
    return {
      showDayOffModalVisible,
      saving,
      daysOfWeek,
      schedule,
      slotDuration,
      maxParallelAppointments,
      enableBufferTime,
      bufferTime,
      daysOff,
      newDayOff,
      timeOptions,
      minDate,
      formatTime,
      isDayOff,
      showDayOffModal,
      addDayOff,
      removeDayOff,
      formatDayOffDate,
      saveAvailability
    };
  }
};
</script>

<style scoped>
.availability-header {
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

.weekly-schedule {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.schedule-day {
  display: flex;
  flex-direction: column;
  padding: 15px;
  background: white;
  border-radius: 12px;
  border-left: 4px solid #6f42c1;
  transition: all 0.3s ease;
}

.schedule-day.day-off {
  border-left-color: #dc3545;
  background: #f8f9fa;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.day-name {
  display: flex;
  align-items: center;
  gap: 10px;
}

.day-checkbox {
  width: 18px;
  height: 18px;
}

.day-label {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0;
  cursor: pointer;
}

.day-hours {
  font-weight: 500;
  color: #198754;
}

.day-off-label {
  color: #dc3545;
  font-weight: 500;
}

.time-slots {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.time-input-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #495057;
}

.days-off-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.day-off-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #dc3545;
}

.day-off-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.day-off-date {
  font-weight: 500;
  color: #2c3e50;
}

.day-off-reason {
  font-size: 0.9rem;
  color: #6c757d;
}

.day-off-form {
  padding: 10px 0;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .availability-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .day-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .time-slots {
    grid-template-columns: 1fr;
  }
  
  .day-off-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .day-off-item button {
    align-self: flex-end;
  }
}
</style>