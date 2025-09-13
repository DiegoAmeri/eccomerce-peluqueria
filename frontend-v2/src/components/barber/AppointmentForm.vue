<template>
  <form @submit.prevent="handleSubmit" class="appointment-form">
    <div class="row">
      <div class="col-md-6">
        <div class="mb-3">
          <label class="form-label">Cliente *</label>
          <select class="form-select" v-model="formData.clientId" required>
            <option value="">Seleccionar cliente</option>
            <option v-for="client in clients" :key="client.id" :value="client.id">
              {{ client.name }} - {{ client.phone }}
            </option>
          </select>
        </div>

        <div class="mb-3">
          <label class="form-label">Servicio *</label>
          <select class="form-select" v-model="formData.serviceId" required>
            <option value="">Seleccionar servicio</option>
            <option v-for="service in services" :key="service.id" :value="service.id">
              {{ service.name }} - {{ service.duration }}min - ${{ service.price }}
            </option>
          </select>
        </div>

        <div class="mb-3">
          <label class="form-label">Fecha *</label>
          <input 
            type="date" 
            class="form-control" 
            v-model="formData.date" 
            required
            :min="minDate"
          >
        </div>

        <div class="mb-3">
          <label class="form-label">Hora *</label>
          <select class="form-select" v-model="formData.time" required>
            <option value="">Seleccionar hora</option>
            <option v-for="time in availableTimes" :key="time" :value="time">
              {{ time }}
            </option>
          </select>
        </div>
      </div>

      <div class="col-md-6">
        <div class="mb-3">
          <label class="form-label">Duración (minutos)</label>
          <input 
            type="number" 
            class="form-control" 
            v-model="formData.duration" 
            min="15"
            max="240"
            step="15"
            required
          >
        </div>

        <div class="mb-3">
          <label class="form-label">Precio ($)</label>
          <input 
            type="number" 
            class="form-control" 
            v-model="formData.price" 
            min="0"
            step="50"
            required
          >
        </div>

        <div class="mb-3">
          <label class="form-label">Estado</label>
          <select class="form-select" v-model="formData.status">
            <option value="pending">Pendiente</option>
            <option value="confirmed">Confirmado</option>
            <option value="completed">Completado</option>
            <option value="cancelled">Cancelado</option>
          </select>
        </div>

        <div class="mb-3">
          <label class="form-label">Notas</label>
          <textarea 
            class="form-control" 
            v-model="formData.notes" 
            rows="3"
            placeholder="Notas adicionales sobre el turno..."
          ></textarea>
        </div>
      </div>
    </div>

    <div class="form-actions">
      <button type="button" class="btn btn-secondary" @click="$emit('cancel')">
        Cancelar
      </button>
      <button type="submit" class="btn btn-primary" :disabled="submitting">
        <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
        {{ submitting ? 'Guardando...' : (appointment ? 'Actualizar' : 'Crear Turno') }}
      </button>
    </div>
  </form>
</template>

<script>
import { ref, computed, watch } from 'vue';

export default {
  name: 'AppointmentForm',
  props: {
    appointment: {
      type: Object,
      default: null
    }
  },
  emits: ['submit', 'cancel'],
  setup(props, { emit }) {
    const submitting = ref(false);
    
    const clients = ref([
      { id: 1, name: "María González", phone: "+54 11 1234-5678" },
      { id: 2, name: "Juan Pérez", phone: "+54 11 2345-6789" },
      { id: 3, name: "Laura Sánchez", phone: "+54 11 3456-7890" }
    ]);
    
    const services = ref([
      { id: 1, name: "Corte de Cabello", duration: 45, price: 500 },
      { id: 2, name: "Corte + Barba", duration: 60, price: 700 },
      { id: 3, name: "Tintura", duration: 120, price: 1200 },
      { id: 4, name: "Alisado", duration: 90, price: 1500 }
    ]);
    
    const formData = ref({
      clientId: '',
      serviceId: '',
      date: new Date().toISOString().split('T')[0],
      time: '',
      duration: 45,
      price: 500,
      status: 'pending',
      notes: ''
    });
    
    const minDate = computed(() => {
      return new Date().toISOString().split('T')[0];
    });
    
    const availableTimes = computed(() => {
      const times = [];
      for (let hour = 9; hour <= 20; hour++) {
        for (let minute = 0; minute < 60; minute += 30) {
          const time = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
          times.push(time);
        }
      }
      return times;
    });
    
    // Cargar datos del turno si estamos editando
    watch(() => props.appointment, (newAppointment) => {
      if (newAppointment) {
        formData.value = {
          clientId: newAppointment.clientId || '',
          serviceId: newAppointment.serviceId || '',
          date: newAppointment.date,
          time: newAppointment.time,
          duration: newAppointment.duration,
          price: newAppointment.price,
          status: newAppointment.status,
          notes: newAppointment.notes || ''
        };
      }
    }, { immediate: true });
    
    // Actualizar precio y duración cuando se selecciona un servicio
    watch(() => formData.value.serviceId, (serviceId) => {
      const service = services.value.find(s => s.id == serviceId);
      if (service) {
        formData.value.duration = service.duration;
        formData.value.price = service.price;
      }
    });
    
    const handleSubmit = async () => {
      submitting.value = true;
      
      try {
        // Validar datos
        if (!formData.value.clientId || !formData.value.serviceId || 
            !formData.value.date || !formData.value.time) {
          throw new Error('Por favor, completa todos los campos obligatorios');
        }
        
        // Simular envío
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        emit('submit', formData.value);
      } catch (error) {
        alert(error.message);
      } finally {
        submitting.value = false;
      }
    };
    
    return {
      clients,
      services,
      formData,
      minDate,
      availableTimes,
      submitting,
      handleSubmit
    };
  }
};
</script>

<style scoped>
.appointment-form {
  padding: 20px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

@media (max-width: 768px) {
  .appointment-form {
    padding: 15px;
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style>