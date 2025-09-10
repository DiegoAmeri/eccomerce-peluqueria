<template>
  <form @submit.prevent="handleSubmit" class="employee-form">
    <div v-if="loading" class="loading-overlay">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <div class="row">
      <!-- Personal Information -->
      <div class="col-md-6">
        <h6 class="section-title">Información Personal</h6>
        
        <div class="mb-3">
          <label class="form-label">Nombre Completo *</label>
          <input 
            type="text" 
            class="form-control" 
            v-model="formData.name" 
            required
            placeholder="Ej: Carlos Rodríguez"
          >
        </div>

        <div class="mb-3">
          <label class="form-label">Correo Electrónico *</label>
          <input 
            type="email" 
            class="form-control" 
            v-model="formData.email" 
            required
            placeholder="carlos@peluqueria.com"
          >
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
          <label class="form-label">DNI *</label>
          <input 
            type="text" 
            class="form-control" 
            v-model="formData.dni" 
            required
            placeholder="12.345.678"
          >
        </div>

        <div class="mb-3">
          <label class="form-label">Fecha de Nacimiento</label>
          <input 
            type="date" 
            class="form-control" 
            v-model="formData.birthDate"
          >
        </div>
      </div>

      <!-- Employment Information -->
      <div class="col-md-6">
        <h6 class="section-title">Información Laboral</h6>
        
        <div class="mb-3">
          <label class="form-label">Peluquería *</label>
          <select class="form-select" v-model="formData.salonId" required>
            <option value="">Seleccionar peluquería</option>
            <option v-for="salon in salons" :key="salon.id" :value="salon.id">
              {{ salon.name }}
            </option>
          </select>
        </div>

        <div class="mb-3">
          <label class="form-label">Cargo *</label>
          <select class="form-select" v-model="formData.position" required>
            <option value="">Seleccionar cargo</option>
            <option value="stylist">Estilista</option>
            <option value="barber">Barbero</option>
            <option value="manicurist">Manicurista</option>
            <option value="esthetician">Esteticista</option>
            <option value="assistant">Asistente</option>
            <option value="manager">Gerente</option>
            <option value="receptionist">Recepcionista</option>
          </select>
        </div>

        <div class="mb-3">
          <label class="form-label">Fecha de Ingreso *</label>
          <input 
            type="date" 
            class="form-control" 
            v-model="formData.hireDate" 
            required
          >
        </div>

        <div class="mb-3">
          <label class="form-label">Salario Mensual *</label>
          <div class="input-group">
            <span class="input-group-text">$</span>
            <input 
              type="number" 
              class="form-control" 
              v-model="formData.salary" 
              required
              min="0"
              step="1000"
              placeholder="45000"
            >
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">Tipo de Contrato *</label>
          <select class="form-select" v-model="formData.contractType" required>
            <option value="">Seleccionar tipo</option>
            <option value="full-time">Tiempo completo</option>
            <option value="part-time">Medio tiempo</option>
            <option value="contractor">Contratista</option>
            <option value="temporary">Temporal</option>
          </select>
        </div>
      </div>
    </div>

    <hr>

    <!-- Specialties and Skills -->
    <div class="row">
      <div class="col-md-6">
        <h6 class="section-title">Especialidades</h6>
        
        <div class="specialties-list">
          <div v-for="specialty in availableSpecialties" :key="specialty" class="form-check mb-2">
            <input 
              class="form-check-input" 
              type="checkbox" 
              :id="`specialty-${specialty}`"
              :value="specialty"
              v-model="formData.specialties"
            >
            <label class="form-check-label" :for="`specialty-${specialty}`">
              {{ specialty }}
            </label>
          </div>
        </div>

        <div class="mb-3 mt-3">
          <label class="form-label">Agregar nueva especialidad</label>
          <div class="input-group">
            <input 
              type="text" 
              class="form-control" 
              v-model="newSpecialty"
              placeholder="Ej: Coloración avanzada"
            >
            <button 
              class="btn btn-outline-primary" 
              type="button"
              @click="addNewSpecialty"
              :disabled="!newSpecialty.trim()"
            >
              <i class="fas fa-plus"></i>
            </button>
          </div>
        </div>
      </div>

      <div class="col-md-6">
        <h6 class="section-title">Habilidades y Certificaciones</h6>
        
        <div class="mb-3">
          <label class="form-label">Años de Experiencia</label>
          <input 
            type="number" 
            class="form-control" 
            v-model="formData.experienceYears" 
            min="0"
            max="50"
            placeholder="5"
          >
        </div>

        <div class="mb-3">
          <label class="form-label">Nivel de Educación</label>
          <select class="form-select" v-model="formData.educationLevel">
            <option value="">Seleccionar nivel</option>
            <option value="high-school">Secundario completo</option>
            <option value="technical">Técnico</option>
            <option value="certificate">Certificado</option>
            <option value="associate">Terciario</option>
            <option value="bachelor">Universitario</option>
            <option value="master">Maestría</option>
          </select>
        </div>

        <div class="mb-3">
          <label class="form-label">Certificaciones</label>
          <textarea 
            class="form-control" 
            v-model="formData.certifications" 
            rows="3"
            placeholder="Lista de certificaciones obtenidas..."
          ></textarea>
        </div>

        <div class="mb-3">
          <label class="form-label">Habilidades Adicionales</label>
          <textarea 
            class="form-control" 
            v-model="formData.additionalSkills" 
            rows="3"
            placeholder="Otras habilidades relevantes..."
          ></textarea>
        </div>
      </div>
    </div>

    <hr>

    <!-- Availability and Schedule -->
    <div class="row">
      <div class="col-12">
        <h6 class="section-title">Disponibilidad</h6>
        
        <div class="availability-grid">
          <div class="availability-header">
            <div class="day-label">Día</div>
            <div class="time-label">Horario</div>
            <div class="status-label">Disponible</div>
          </div>
          
          <div v-for="day in days" :key="day.value" class="availability-row">
            <div class="day-label">
              <label class="form-check-label">{{ day.label }}</label>
            </div>
            <div class="time-inputs">
              <div class="time-input">
                <input 
                  type="time" 
                  class="form-control" 
                  v-model="formData.availability[day.value].startTime"
                  :disabled="!formData.availability[day.value].available"
                >
              </div>
              <span class="time-separator">-</span>
              <div class="time-input">
                <input 
                  type="time" 
                  class="form-control" 
                  v-model="formData.availability[day.value].endTime"
                  :disabled="!formData.availability[day.value].available"
                >
              </div>
            </div>
            <div class="status-label">
              <div class="form-check form-switch">
                <input 
                  class="form-check-input" 
                  type="checkbox" 
                  :id="`available-${day.value}`"
                  v-model="formData.availability[day.value].available"
                >
                <label class="form-check-label" :for="`available-${day.value}`"></label>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Emergency Contact -->
    <hr>
    <div class="row">
      <div class="col-12">
        <h6 class="section-title">Contacto de Emergencia</h6>
        
        <div class="row">
          <div class="col-md-4">
            <div class="mb-3">
              <label class="form-label">Nombre *</label>
              <input 
                type="text" 
                class="form-control" 
                v-model="formData.emergencyContact.name" 
                required
                placeholder="Nombre completo"
              >
            </div>
          </div>
          <div class="col-md-4">
            <div class="mb-3">
              <label class="form-label">Teléfono *</label>
              <input 
                type="tel" 
                class="form-control" 
                v-model="formData.emergencyContact.phone" 
                required
                placeholder="+54 11 1234-5678"
              >
            </div>
          </div>
          <div class="col-md-4">
            <div class="mb-3">
              <label class="form-label">Relación *</label>
              <select class="form-select" v-model="formData.emergencyContact.relationship" required>
                <option value="">Seleccionar relación</option>
                <option value="parent">Padre/Madre</option>
                <option value="spouse">Cónyuge</option>
                <option value="child">Hijo/a</option>
                <option value="sibling">Hermano/a</option>
                <option value="friend">Amigo/a</option>
                <option value="other">Otro</option>
              </select>
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
        {{ submitting ? 'Guardando...' : (employee ? 'Actualizar' : 'Crear Empleado') }}
      </button>
    </div>
  </form>
</template>

<script>
import { ref, watch, onMounted } from 'vue';

export default {
  name: 'EmployeeForm',
  props: {
    employee: {
      type: Object,
      default: null
    },
    salons: {
      type: Array,
      required: true
    }
  },
  emits: ['submit', 'cancel'],
  setup(props, { emit }) {
    const loading = ref(false);
    const submitting = ref(false);
    const newSpecialty = ref('');
    
    const days = [
      { value: 'monday', label: 'Lunes' },
      { value: 'tuesday', label: 'Martes' },
      { value: 'wednesday', label: 'Miércoles' },
      { value: 'thursday', label: 'Jueves' },
      { value: 'friday', label: 'Viernes' },
      { value: 'saturday', label: 'Sábado' },
      { value: 'sunday', label: 'Domingo' }
    ];
    
    const availableSpecialties = ref([
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
    
    const formData = ref({
      name: '',
      email: '',
      phone: '',
      dni: '',
      birthDate: '',
      salonId: '',
      position: '',
      hireDate: new Date().toISOString().split('T')[0],
      salary: 0,
      contractType: '',
      specialties: [],
      experienceYears: 0,
      educationLevel: '',
      certifications: '',
      additionalSkills: '',
      availability: {
        monday: { available: true, startTime: '09:00', endTime: '18:00' },
        tuesday: { available: true, startTime: '09:00', endTime: '18:00' },
        wednesday: { available: true, startTime: '09:00', endTime: '18:00' },
        thursday: { available: true, startTime: '09:00', endTime: '18:00' },
        friday: { available: true, startTime: '09:00', endTime: '18:00' },
        saturday: { available: true, startTime: '09:00', endTime: '14:00' },
        sunday: { available: false, startTime: '', endTime: '' }
      },
      emergencyContact: {
        name: '',
        phone: '',
        relationship: ''
      }
    });
    
    // Cargar datos del empleado si estamos editando
    watch(() => props.employee, (newEmployee) => {
      if (newEmployee) {
        loading.value = true;
        // Simular carga de datos
        setTimeout(() => {
          formData.value = {
            ...formData.value,
            name: newEmployee.name,
            email: newEmployee.email,
            phone: newEmployee.phone,
            dni: newEmployee.dni || '',
            birthDate: newEmployee.birthDate || '',
            salonId: newEmployee.salonId,
            position: newEmployee.position || '',
            hireDate: newEmployee.hireDate || new Date().toISOString().split('T')[0],
            salary: newEmployee.salary || 0,
            contractType: newEmployee.contractType || '',
            specialties: [...newEmployee.specialties],
            experienceYears: newEmployee.experienceYears || 0,
            educationLevel: newEmployee.educationLevel || '',
            certifications: newEmployee.certifications || '',
            additionalSkills: newEmployee.additionalSkills || ''
          };
          loading.value = false;
        }, 500);
      }
    }, { immediate: true });
    
    const addNewSpecialty = () => {
      if (newSpecialty.value.trim() && !availableSpecialties.value.includes(newSpecialty.value.trim())) {
        availableSpecialties.value.push(newSpecialty.value.trim());
        formData.value.specialties.push(newSpecialty.value.trim());
        newSpecialty.value = '';
      }
    };
    
    const handleSubmit = async () => {
      submitting.value = true;
      
      try {
        // Validar datos del formulario
        if (!formData.value.name || !formData.value.email || !formData.value.phone || 
            !formData.value.dni || !formData.value.salonId || !formData.value.position || 
            !formData.value.hireDate || !formData.value.salary || !formData.value.contractType) {
          throw new Error('Por favor, completa todos los campos obligatorios');
        }
        
        // Validar contacto de emergencia
        if (!formData.value.emergencyContact.name || 
            !formData.value.emergencyContact.phone || 
            !formData.value.emergencyContact.relationship) {
          throw new Error('Por favor, completa la información de contacto de emergencia');
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
      newSpecialty,
      days,
      availableSpecialties,
      formData,
      addNewSpecialty,
      handleSubmit
    };
  }
};
</script>

<style scoped>
.employee-form {
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

.specialties-list {
  max-height: 200px;
  overflow-y: auto;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.availability-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.availability-header {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr;
  gap: 15px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  font-weight: 600;
  color: #495057;
}

.availability-row {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr;
  gap: 15px;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.time-inputs {
  display: flex;
  align-items: center;
  gap: 10px;
}

.time-input {
  flex: 1;
}

.time-separator {
  color: #6c757d;
  font-weight: 600;
}

.status-label {
  display: flex;
  justify-content: center;
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
  .availability-header,
  .availability-row {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  
  .day-label,
  .time-label,
  .status-label {
    text-align: left;
  }
  
  .time-inputs {
    justify-content: space-between;
  }
  
  .specialties-list {
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
  
  .time-separator {
    display: none;
  }
}
</style>