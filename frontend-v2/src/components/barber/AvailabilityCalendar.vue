<template>
  <div class="availability-calendar">
    <div class="card">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">Mi Disponibilidad</h5>
        <button class="btn btn-primary btn-sm" @click="saveAvailability">
          <i class="fas fa-save me-1"></i> Guardar Cambios
        </button>
      </div>
      <div class="card-body">
        <div class="row mb-4">
          <div class="col-md-6">
            <label class="form-label">Seleccionar Semana</label>
            <input type="week" class="form-control" v-model="selectedWeek" @change="loadWeekAvailability">
          </div>
          <div class="col-md-6">
            <div class="form-check form-switch mt-4">
              <input class="form-check-input" type="checkbox" id="copyToAllWeeks" v-model="copyToAllWeeks">
              <label class="form-check-label" for="copyToAllWeeks">Aplicar a todas las semanas</label>
            </div>
          </div>
        </div>
        
        <div class="table-responsive">
          <table class="table table-bordered">
            <thead>
              <tr>
                <th>Hora</th>
                <th v-for="day in days" :key="day">{{ day }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="timeSlot in timeSlots" :key="timeSlot">
                <td>{{ timeSlot }}</td>
                <td v-for="day in days" :key="day">
                  <div class="form-check form-switch">
                    <input class="form-check-input" type="checkbox" 
                           :id="`${day}-${timeSlot}`" 
                           v-model="availability[day][timeSlot]">
                    <label class="form-check-label" :for="`${day}-${timeSlot}`"></label>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <div class="mt-3">
          <h6>Horario Estándar</h6>
          <div class="row">
            <div class="col-md-6">
              <div class="form-check">
                <input class="form-check-input" type="checkbox" id="setStandardSchedule" v-model="setStandardSchedule">
                <label class="form-check-label" for="setStandardSchedule">Establecer como horario estándar</label>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';

export default {
  name: 'AvailabilityCalendar',
  setup() {
    const days = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'];
    const timeSlots = [
      '09:00', '09:30', '10:00', '10:30', '11:00', '11:30', 
      '12:00', '12:30', '13:00', '13:30', '14:00', '14:30',
      '15:00', '15:30', '16:00', '16:30', '17:00', '17:30',
      '18:00', '18:30', '19:00', '19:30', '20:00'
    ];
    
    const selectedWeek = ref('');
    const copyToAllWeeks = ref(false);
    const setStandardSchedule = ref(false);
    const availability = ref({});
    
    // Inicializar estructura de disponibilidad
    const initializeAvailability = () => {
      const initialAvailability = {};
      days.forEach(day => {
        initialAvailability[day] = {};
        timeSlots.forEach(timeSlot => {
          initialAvailability[day][timeSlot] = true; // Por defecto disponible
        });
      });
      availability.value = initialAvailability;
    };
    
    const loadWeekAvailability = () => {
      // En una app real, haríamos una llamada a la API para cargar la disponibilidad de la semana seleccionada
      console.log('Cargando disponibilidad para la semana:', selectedWeek.value);
      // Simulamos que cargamos datos (en realidad mantenemos los datos iniciales)
    };
    
    const saveAvailability = () => {
      // En una app real, haríamos una llamada a la API para guardar la disponibilidad
      console.log('Guardando disponibilidad:', availability.value);
      console.log('Aplicar a todas las semanas:', copyToAllWeeks.value);
      console.log('Establecer como horario estándar:', setStandardSchedule.value);
      alert('Disponibilidad guardada correctamente!');
    };
    
    onMounted(() => {
      initializeAvailability();
      // Establecer la semana actual por defecto
      const today = new Date();
      const firstDayOfYear = new Date(today.getFullYear(), 0, 1);
      const pastDaysOfYear = (today - firstDayOfYear) / 86400000;
      const weekNumber = Math.ceil((pastDaysOfYear + firstDayOfYear.getDay() + 1) / 7);
      selectedWeek.value = `${today.getFullYear()}-W${weekNumber.toString().padStart(2, '0')}`;
    });
    
    return {
      days,
      timeSlots,
      selectedWeek,
      copyToAllWeeks,
      setStandardSchedule,
      availability,
      loadWeekAvailability,
      saveAvailability
    };
  }
};
</script>

<style scoped>
.availability-calendar {
  padding: 20px;
}

.table th {
  text-align: center;
  vertical-align: middle;
}

.table td {
  text-align: center;
  vertical-align: middle;
}

.form-check-input:checked {
  background-color: #20c997;
  border-color: #20c997;
}

@media (max-width: 768px) {
  .availability-calendar {
    padding: 15px;
  }
  
  .table-responsive {
    font-size: 0.8rem;
  }
}
</style>