<template>
  <form @submit.prevent="handleSubmit" class="salon-form">
    <div v-if="loading" class="loading-overlay">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <div class="row">
      <!-- Basic Information -->
      <div class="col-md-6">
        <h6 class="section-title">Información Básica</h6>
        
        <div class="mb-3">
          <label class="form-label">Nombre de la Peluquería *</label>
          <input 
            type="text" 
            class="form-control" 
            v-model="formData.name" 
            required
            placeholder="Ej: Estilo Urbano"
          >
        </div>

        <div class="mb-3">
          <label class="form-label">Categoría *</label>
          <select class="form-select" v-model="formData.category" required>
            <option value="">Seleccionar categoría</option>
            <option value="peluqueria">Peluquería</option>
            <option value="barberia">Barbería</option>
            <option value="estetica">Estética</option>
            <option value="unas">Uñas</option>
            <option value="spa">Spa & Bienestar</option>
          </select>
        </div>

        <div class="mb-3">
          <label class="form-label">Teléfono *</label>
          <input 
            type="tel" 
            class="form-control" 
            v-model="formData.phone" 
            required
            placeholder="+54 11 1234-5678"
          >
        </div>

        <div class="mb-3">
          <label class="form-label">Correo Electrónico</label>
          <input 
            type="email" 
            class="form-control" 
            v-model="formData.email" 
            placeholder="contacto@peluqueria.com"
          >
        </div>
      </div>

      <!-- Location Information -->
      <div class="col-md-6">
        <h6 class="section-title">Ubicación</h6>
        
        <div class="mb-3">
          <label class="form-label">Dirección *</label>
          <input 
            type="text" 
            class="form-control" 
            v-model="formData.address" 
            required
            placeholder="Av. Siempre Viva 742"
          >
        </div>

        <div class="row">
          <div class="col-md-6">
            <div class="mb-3">
              <label class="form-label">Ciudad *</label>
              <input 
                type="text" 
                class="form-control" 
                v-model="formData.city" 
                required
                placeholder="Ciudad"
              >
            </div>
          </div>
          <div class="col-md-6">
            <div class="mb-3">
              <label class="form-label">Código Postal</label>
              <input 
                type="text" 
                class="form-control" 
                v-model="formData.postalCode" 
                placeholder="1234"
              >
            </div>
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">Provincia *</label>
          <select class="form-select" v-model="formData.state" required>
            <option value="">Seleccionar provincia</option>
            <option value="CABA">Ciudad Autónoma de Buenos Aires</option>
            <option value="BA">Buenos Aires</option>
            <option value="CBA">Córdoba</option>
            <option value="SF">Santa Fe</option>
            <option value="MZA">Mendoza</option>
            <option value="TU">Tucumán</option>
            <option value="ER">Entre Ríos</option>
            <option value="SL">Salta</option>
            <option value="CH">Chaco</option>
            <option value="CR">Corrientes</option>
            <option value="SJ">San Juan</option>
            <option value="RN">Río Negro</option>
            <option value="NQ">Neuquén</option>
            <option value="CHB">Chubut</option>
            <option value="SCR">Santa Cruz</option>
            <option value="TF">Tierra del Fuego</option>
          </select>
        </div>
      </div>
    </div>

    <hr>

    <!-- Business Hours -->
    <div class="row">
      <div class="col-12">
        <h6 class="section-title">Horario de Atención</h6>
        
        <div class="business-hours">
          <div v-for="day in days" :key="day.value" class="day-schedule">
            <div class="day-checkbox">
              <div class="form-check">
                <input 
                  class="form-check-input" 
                  type="checkbox" 
                  :id="`day-${day.value}`"
                  v-model="formData.schedule[day.value].open"
                >
                <label class="form-check-label" :for="`day-${day.value}`">
                  {{ day.label }}
                </label>
              </div>
            </div>
            <div class="time-inputs" v-if="formData.schedule[day.value].open">
              <div class="time-input">
                <label>Apertura</label>
                <input 
                  type="time" 
                  class="form-control" 
                  v-model="formData.schedule[day.value].openTime"
                >
              </div>
              <div class="time-input">
                <label>Cierre</label>
                <input 
                  type="time" 
                  class="form-control" 
                  v-model="formData.schedule[day.value].closeTime"
                >
              </div>
            </div>
            <div class="day-closed" v-else>
              <span class="text-muted">Cerrado</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <hr>

    <!-- Services and Amenities -->
    <div class="row">
      <div class="col-md-6">
        <h6 class="section-title">Servicios Ofrecidos</h6>
        
        <div class="services-list">
          <div v-for="service in availableServices" :key="service" class="form-check mb-2">
            <input 
              class="form-check-input" 
              type="checkbox" 
              :id="`service-${service}`"
              :value="service"
              v-model="formData.services"
            >
            <label class="form-check-label" :for="`service-${service}`">
              {{ service }}
            </label>
          </div>
        </div>

        <div class="mb-3 mt-3">
          <label class="form-label">Agregar nuevo servicio</label>
          <div class="input-group">
            <input 
              type="text" 
              class="form-control" 
              v-model="newService"
              placeholder="Ej: Tratamiento capilar"
            >
            <button 
              class="btn btn-outline-primary" 
              type="button"
              @click="addNewService"
              :disabled="!newService.trim()"
            >
              <i class="fas fa-plus"></i>
            </button>
          </div>
        </div>
      </div>

      <div class="col-md-6">
        <h6 class="section-title">Comodidades y Características</h6>
        
        <div class="amenities-list">
          <div v-for="amenity in availableAmenities" :key="amenity" class="form-check mb-2">
            <input 
              class="form-check-input" 
              type="checkbox" 
              :id="`amenity-${amenity}`"
              :value="amenity"
              v-model="formData.amenities"
            >
            <label class="form-check-label" :for="`amenity-${amenity}`">
              {{ amenity }}
            </label>
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">Descripción</label>
          <textarea 
            class="form-control" 
            v-model="formData.description" 
            rows="4"
            placeholder="Describe tu peluquería, servicios especiales, ambiente..."
          ></textarea>
        </div>
      </div>
    </div>

    <!-- Social Media -->
    <hr>
    <div class="row">
      <div class="col-12">
        <h6 class="section-title">Redes Sociales</h6>
        
        <div class="row">
          <div class="col-md-4">
            <div class="mb-3">
              <label class="form-label">
                <i class="fab fa-instagram me-2 text-danger"></i>Instagram
              </label>
              <div class="input-group">
                <span class="input-group-text">@</span>
                <input 
                  type="text" 
                  class="form-control" 
                  v-model="formData.socialMedia.instagram"
                  placeholder="tu_peluqueria"
                >
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="mb-3">
              <label class="form-label">
                <i class="fab fa-facebook me-2 text-primary"></i>Facebook
              </label>
              <input 
                type="text" 
                class="form-control" 
                v-model="formData.socialMedia.facebook"
                placeholder="https://facebook.com/tu-peluqueria"
              >
            </div>
          </div>
          <div class="col-md-4">
            <div class="mb-3">
              <label class="form-label">
                <i class="fab fa-tiktok me-2 text-dark"></i>TikTok
              </label>
              <div class="input-group">
                <span class="input-group-text">@</span>
                <input 
                  type="text" 
                  class="form-control" 
                  v-model="formData.socialMedia.tiktok"
                  placeholder="tu_peluqueria"
                >
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Form Actions -->
    <div class="form-actions">
      <button type="button" class="btn btn-secondary" @click="$emit('cancel')">
        Cancelar
      </button>
      <button type="submit" class="btn btn-primary" :disabled="submitting">
        <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
        {{ submitting ? 'Guardando...' : (salon ? 'Actualizar' : 'Crear Peluquería') }}
      </button>
    </div>
  </form>
</template>

<script>
import { ref, watch, onMounted } from 'vue';

export default {
  name: 'SalonForm',
  props: {
    salon: {
      type: Object,
      default: null
    }
  },
  emits: ['submit', 'cancel'],
  setup(props, { emit }) {
    const loading = ref(false);
    const submitting = ref(false);
    const newService = ref('');
    
    const days = [
      { value: 'monday', label: 'Lunes' },
      { value: 'tuesday', label: 'Martes' },
      { value: 'wednesday', label: 'Miércoles' },
      { value: 'thursday', label: 'Jueves' },
      { value: 'friday', label: 'Viernes' },
      { value: 'saturday', label: 'Sábado' },
      { value: 'sunday', label: 'Domingo' }
    ];
    
    const availableServices = ref([
      'Corte de cabello',
      'Tintura',
      'Alisado',
      'Reflejos',
      'Peinados',
      'Maquillaje',
      'Manicura',
      'Pedicura',
      'Uñas acrílicas',
      'Depilación',
      'Barbería',
      'Afeitado tradicional',
      'Tratamiento capilar',
      'Extensiones',
      'Lifting de pestañas',
      'Perfilado de cejas'
    ]);
    
    const availableAmenities = ref([
      'Wi-Fi gratuito',
      'Estacionamiento',
      'Aire acondicionado',
      'Acceso para discapacitados',
      'Acepta tarjetas',
      'Reservas online',
      'Servicio a domicilio',
      'Productos a la venta',
      'Cafetería',
      'Zona de espera',
      'TV en sala de espera',
      'Música ambiental',
      'Revistas y diarios',
      'Paraguas disponibles',
      'Carga de celular'
    ]);
    
    const formData = ref({
      name: '',
      category: '',
      phone: '',
      email: '',
      address: '',
      city: '',
      postalCode: '',
      state: '',
      schedule: {
        monday: { open: true, openTime: '09:00', closeTime: '18:00' },
        tuesday: { open: true, openTime: '09:00', closeTime: '18:00' },
        wednesday: { open: true, openTime: '09:00', closeTime: '18:00' },
        thursday: { open: true, openTime: '09:00', closeTime: '18:00' },
        friday: { open: true, openTime: '09:00', closeTime: '18:00' },
        saturday: { open: true, openTime: '09:00', closeTime: '14:00' },
        sunday: { open: false, openTime: '', closeTime: '' }
      },
      services: [],
      amenities: [],
      description: '',
      socialMedia: {
        instagram: '',
        facebook: '',
        tiktok: ''
      }
    });
    
    // Cargar datos del salon si estamos editando
    watch(() => props.salon, (newSalon) => {
      if (newSalon) {
        loading.value = true;
        // Simular carga de datos
        setTimeout(() => {
          formData.value = {
            ...formData.value,
            name: newSalon.name,
            category: newSalon.category || '',
            phone: newSalon.phone,
            email: newSalon.email || '',
            address: newSalon.address,
            city: newSalon.city || 'Ciudad',
            postalCode: newSalon.postalCode || '',
            state: newSalon.state || 'CABA',
            services: [...newSalon.services],
            description: newSalon.description || ''
          };
          loading.value = false;
        }, 500);
      }
    }, { immediate: true });
    
    const addNewService = () => {
      if (newService.value.trim() && !availableServices.value.includes(newService.value.trim())) {
        availableServices.value.push(newService.value.trim());
        formData.value.services.push(newService.value.trim());
        newService.value = '';
      }
    };
    
    const handleSubmit = async () => {
      submitting.value = true;
      
      try {
        // Validar datos del formulario
        if (!formData.value.name || !formData.value.category || !formData.value.phone || 
            !formData.value.address || !formData.value.city || !formData.value.state) {
          throw new Error('Por favor, completa todos los campos obligatorios');
        }
        
        // Validar horarios
        for (const day of days) {
          if (formData.value.schedule[day.value].open) {
            if (!formData.value.schedule[day.value].openTime || 
                !formData.value.schedule[day.value].closeTime) {
              throw new Error(`Por favor, completa los horarios para ${day.label}`);
            }
          }
        }
        
        // Simular envío del formulario
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        emit('submit', formData.value);
      } catch (error) {
        alert(error.message);
      } finally {
        submitting.value = false;
      }
    };
    
    return {
      loading,
      submitting,
      newService,
      days,
      availableServices,
      availableAmenities,
      formData,
      addNewService,
      handleSubmit
    };
  }
};
</script>

<style scoped>
.salon-form {
  position: relative;
  min-height: 300px;
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
  z-index: 100;
  border-radius: 12px;
}

.section-title {
  color: #6f42c1;
  font-weight: 600;
  margin-bottom: 20px;
  padding-bottom: 8px;
  border-bottom: 2px solid #e9ecef;
}

.business-hours {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.day-schedule {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.day-checkbox {
  min-width: 100px;
}

.time-inputs {
  display: flex;
  gap: 10px;
  align-items: center;
}

.time-input {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.time-input label {
  font-size: 0.8rem;
  color: #6c757d;
  font-weight: 500;
}

.time-input .form-control {
  width: 100px;
}

.day-closed {
  padding: 8px 12px;
  background: #f8d7da;
  color: #721c24;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
}

.services-list,
.amenities-list {
  max-height: 200px;
  overflow-y: auto;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.form-check-label {
  font-weight: 500;
  color: #495057;
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
  .business-hours {
    grid-template-columns: 1fr;
  }
  
  .day-schedule {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .time-inputs {
    justify-content: space-between;
  }
  
  .time-input .form-control {
    width: 100%;
  }
  
  .services-list,
  .amenities-list {
    max-height: 150px;
  }
  
  .form-actions {
    flex-direction: column;
  }
}

@media (max-width: 576px) {
  .section-title {
    font-size: 1rem;
  }
  
  .time-inputs {
    flex-direction: column;
    gap: 8px;
  }
}
</style>