<template>
  <div class="auth-container">
    <div class="register-container">
      <div class="register-header">
        <h2><i class="fas fa-cut"></i> Crear Cuenta</h2>
        <p class="mb-0">Únete a StyleCut Pro</p>
      </div>

      <!-- Progress Steps -->
      <div class="step-indicator">
        <div class="step" :class="{ active: currentStep === 1, completed: currentStep > 1 }">
          <span class="step-number">1</span>
          <span class="step-label">Tipo de Usuario</span>
        </div>
        <div class="step-line" :class="{ completed: currentStep > 1 }"></div>
        <div class="step" :class="{ active: currentStep === 2, completed: currentStep > 2 }">
          <span class="step-number">2</span>
          <span class="step-label">Información Personal</span>
        </div>
        <div class="step-line" :class="{ completed: currentStep > 2 }"></div>
        <div class="step" :class="{ active: currentStep === 3, completed: currentStep > 3 }">
          <span class="step-number">3</span>
          <span class="step-label" v-if="userType === 'client'">Finalizar</span>
          <span class="step-label" v-else>Información Profesional</span>
        </div>
      </div>

      <!-- Step 1: User Type Selection -->
      <div v-if="currentStep === 1" class="step-content fade-in">
        <h4 class="text-center mb-4">¿Qué tipo de cuenta deseas crear?</h4>
        
        <div class="row">
          <div class="col-md-4 mb-3">
            <div class="card user-type-card" :class="{ selected: userType === 'client' }" @click="selectUserType('client')">
              <div class="card-body text-center">
                <i class="fas fa-user fa-3x mb-3"></i>
                <h5>Cliente</h5>
                <p class="small">Busca y reserva turnos en peluquerías</p>
              </div>
            </div>
          </div>
          
          <div class="col-md-4 mb-3">
            <div class="card user-type-card" :class="{ selected: userType === 'barber' }" @click="selectUserType('barber')">
              <div class="card-body text-center">
                <i class="fas fa-scissors fa-3x mb-3"></i>
                <h5>Peluquero</h5>
                <p class="small">Trabaja en una peluquería existente</p>
              </div>
            </div>
          </div>
          
          <div class="col-md-4 mb-3">
            <div class="card user-type-card" :class="{ selected: userType === 'owner' }" @click="selectUserType('owner')">
              <div class="card-body text-center">
                <i class="fas fa-store fa-3x mb-3"></i>
                <h5>Dueño</h5>
                <p class="small">Administra tu propia peluquería</p>
              </div>
            </div>
          </div>
        </div>

        <button class="btn btn-primary w-100" @click="nextStep" :disabled="!userType">
          Continuar <i class="fas fa-arrow-right ms-2"></i>
        </button>
      </div>

      <!-- Step 2: Personal Information -->
      <div v-if="currentStep === 2" class="step-content fade-in">
        <h4 class="text-center mb-4">Información Personal</h4>
        
        <div class="row">
          <div class="col-md-6">
            <div class="mb-3">
              <label class="form-label">Nombre *</label>
              <input type="text" class="form-control" v-model="form.nombre" required>
            </div>
          </div>
          <div class="col-md-6">
            <div class="mb-3">
              <label class="form-label">Apellido *</label>
              <input type="text" class="form-control" v-model="form.apellido" required>
            </div>
          </div>
        </div>

        <div class="row">
          <div class="col-md-6">
            <div class="mb-3">
              <label class="form-label">Email *</label>
              <input type="email" class="form-control" v-model="form.email" required>
            </div>
          </div>
          <div class="col-md-6">
            <div class="mb-3">
              <label class="form-label">Teléfono *</label>
              <input type="tel" class="form-control" v-model="form.telefono" required>
            </div>
          </div>
        </div>

        <div class="row">
          <div class="col-md-6">
            <div class="mb-3">
              <label class="form-label">Contraseña *</label>
              <input type="password" class="form-control" v-model="form.password" required>
            </div>
          </div>
          <div class="col-md-6">
            <div class="mb-3">
              <label class="form-label">Confirmar Contraseña *</label>
              <input type="password" class="form-control" v-model="form.confirmPassword" required>
            </div>
          </div>
        </div>

        <div class="d-flex gap-2">
          <button class="btn btn-outline-primary flex-fill" @click="prevStep">
            <i class="fas fa-arrow-left me-2"></i>Anterior
          </button>
          <button class="btn btn-primary flex-fill" @click="nextStep">
            Continuar <i class="fas fa-arrow-right ms-2"></i>
          </button>
        </div>
      </div>

      <!-- Step 3: Professional Information -->
      <div v-if="currentStep === 3" class="step-content fade-in">
        <!-- Client Final Step -->
        <div v-if="userType === 'client'">
          <h4 class="text-center mb-4">¡Ya casi terminas!</h4>
          <p class="text-center mb-4">Revisa tu información y completa el registro</p>
          
          <div class="review-info">
            <p><strong>Nombre:</strong> {{ form.nombre }} {{ form.apellido }}</p>
            <p><strong>Email:</strong> {{ form.email }}</p>
            <p><strong>Teléfono:</strong> {{ form.telefono }}</p>
            <p><strong>Tipo de cuenta:</strong> Cliente</p>
          </div>
        </div>

        <!-- Barber Selection -->
        <div v-else-if="userType === 'barber'">
          <h4 class="text-center mb-4">Información Profesional</h4>
          
          <div class="mb-4">
            <label class="form-label">Selecciona tu peluquería *</label>
            <select class="form-select" v-model="form.peluqueriaId" required>
              <option value="">Selecciona una peluquería</option>
              <option v-for="peluqueria in peluquerias" :key="peluqueria.id" :value="peluqueria.id">
                {{ peluqueria.nombre }} - {{ peluqueria.direccion }}
              </option>
            </select>
            <div class="form-text">
              ¿No encuentras tu peluquería? 
              <a href="#" @click.prevent="userType = 'owner'">Crea una nueva</a>
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Especialidad</label>
            <input type="text" class="form-control" v-model="form.especialidad" placeholder="Ej: Cortes de cabello, barba, etc.">
          </div>

          <div class="mb-3">
            <label class="form-label">Años de experiencia</label>
            <input type="number" class="form-control" v-model="form.experiencia" min="0">
          </div>
        </div>

        <!-- Owner Salon Creation -->
        <div v-else-if="userType === 'owner'">
          <h4 class="text-center mb-4">Información de tu Peluquería</h4>
          
          <div class="mb-3">
            <label class="form-label">Nombre de la Peluquería *</label>
            <input type="text" class="form-control" v-model="form.nombrePeluqueria" required>
          </div>

          <div class="mb-3">
            <label class="form-label">Categoría *</label>
            <div>
              <span class="category-badge" :class="{ selected: form.categoria === 'peluqueria' }" 
                    @click="form.categoria = 'peluqueria'">
                <i class="fas fa-scissors me-1"></i> Peluquería
              </span>
              <span class="category-badge" :class="{ selected: form.categoria === 'unas' }" 
                    @click="form.categoria = 'unas'">
                <i class="fas fa-hand-sparkles me-1"></i> Uñas
              </span>
              <span class="category-badge" :class="{ selected: form.categoria === 'estetica' }" 
                    @click="form.categoria = 'estetica'">
                <i class="fas fa-spa me-1"></i> Estética
              </span>
              <span class="category-badge" :class="{ selected: form.categoria === 'barberia' }" 
                    @click="form.categoria = 'barberia'">
                <i class="fas fa-cut me-1"></i> Barbería
              </span>
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Instagram (opcional)</label>
            <div class="input-group">
              <span class="input-group-text">@</span>
              <input type="text" class="form-control" v-model="form.instagram" placeholder="tu_usuario">
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Dirección *</label>
            <input type="text" class="form-control" v-model="form.direccion" required 
                  placeholder="Ingresa la dirección de tu peluquería">
          </div>

          <div class="mb-3">
            <label class="form-label">Descripción</label>
            <textarea class="form-control" v-model="form.descripcion" rows="3" 
                      placeholder="Describe los servicios que ofreces..."></textarea>
          </div>

          <div class="row">
            <div class="col-md-6">
              <div class="mb-3">
                <label class="form-label">Horario de Apertura</label>
                <input type="time" class="form-control" v-model="form.horarioApertura">
              </div>
            </div>
            <div class="col-md-6">
              <div class="mb-3">
                <label class="form-label">Horario de Cierre</label>
                <input type="time" class="form-control" v-model="form.horarioCierre">
              </div>
            </div>
          </div>
        </div>

        <div class="mb-3 form-check">
          <input type="checkbox" class="form-check-input" id="terms" v-model="form.aceptoTerminos" required>
          <label class="form-check-label" for="terms">
            Acepto los <a href="#" @click.prevent="showTerms">términos y condiciones</a>
          </label>
        </div>

        <div class="d-flex gap-2">
          <button class="btn btn-outline-primary flex-fill" @click="prevStep">
            <i class="fas fa-arrow-left me-2"></i>Anterior
          </button>
          <button class="btn btn-primary flex-fill" @click="handleRegister" :disabled="loading">
            <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
            {{ loading ? 'Registrando...' : 'Completar Registro' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

export default {
  name: 'RegisterForm',
  setup() {
    const store = useStore();
    const router = useRouter();
    
    const currentStep = ref(1);
    const userType = ref('');
    const peluquerias = ref([]);
    const loading = ref(false);
    
    const form = ref({
      // Personal info
      nombre: '',
      apellido: '',
      email: '',
      telefono: '',
      password: '',
      confirmPassword: '',
      
      // Professional info
      peluqueriaId: '',
      especialidad: '',
      experiencia: '',
      
      // Salon info (for owners)
      nombrePeluqueria: '',
      categoria: 'peluqueria',
      instagram: '',
      direccion: '',
      descripcion: '',
      horarioApertura: '09:00',
      horarioCierre: '18:00',
      
      aceptoTerminos: false
    });

    const selectUserType = (type) => {
      userType.value = type;
    };

    const nextStep = () => {
      if (validateStep(currentStep.value)) {
        currentStep.value++;
        
        // Load salons if barber selected
        if (currentStep.value === 3 && userType.value === 'barber') {
          loadPeluquerias();
        }
      }
    };

    const prevStep = () => {
      currentStep.value--;
    };

    const validateStep = (step) => {
      if (step === 1) {
        if (!userType.value) {
          alert('Por favor, selecciona un tipo de usuario');
          return false;
        }
      } else if (step === 2) {
        if (!form.value.nombre || !form.value.apellido || !form.value.email || 
            !form.value.telefono || !form.value.password || !form.value.confirmPassword) {
          alert('Por favor, completa todos los campos obligatorios');
          return false;
        }
        
        if (form.value.password !== form.value.confirmPassword) {
          alert('Las contraseñas no coinciden');
          return false;
        }
        
        if (form.value.password.length < 6) {
          alert('La contraseña debe tener al menos 6 caracteres');
          return false;
        }
      }
      return true;
    };

    const loadPeluquerias = async () => {
      try {
        // En una app real, haríamos una llamada a la API
        // const response = await api.get('/peluquerias');
        // peluquerias.value = response.data;
        
        // Simulando la carga de peluquerías
        peluquerias.value = [
          { id: 1, nombre: "Corte Perfecto", direccion: "Av. Siempre Viva 742" },
          { id: 2, nombre: "Estilo Urbano", direccion: "Calle Falsa 123" },
          { id: 3, nombre: "Barbería Clásica", direccion: "Av. Libertador 456" }
        ];
      } catch (error) {
        console.error('Error loading peluquerias:', error);
      }
    };

    const showTerms = () => {
      alert("Aquí se mostrarían los términos y condiciones. Esta funcionalidad se implementará próximamente.");
    };

    const handleRegister = async () => {
      try {
        loading.value = true;
        
        if (!form.value.aceptoTerminos) {
          alert('Debes aceptar los términos y condiciones');
          loading.value = false;
          return;
        }
        
        const registerData = {
          tipoUsuario: userType.value,
          ...form.value
        };

        // En una app real, haríamos una llamada a la API de registro
        // await store.dispatch('register', registerData);
        
        // Simulamos un registro exitoso
        setTimeout(() => {
          alert('¡Cuenta creada exitosamente!');
          router.push('/login');
        }, 1500);
        
      } catch (error) {
        console.error('Error en registro:', error);
        alert(error.response?.data?.message || 'Error al crear la cuenta');
      } finally {
        loading.value = false;
      }
    };

    onMounted(() => {
      // Cargar peluquerías para el selector
      loadPeluquerias();
    });

    return {
      currentStep,
      userType,
      peluquerias,
      form,
      loading,
      selectUserType,
      nextStep,
      prevStep,
      handleRegister,
      showTerms
    };
  }
};
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  padding: 20px;
}

.register-container {
  background-color: white;
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(50, 50, 93, 0.1), 0 5px 15px rgba(0, 0, 0, 0.07);
  overflow: hidden;
  width: 100%;
  max-width: 1000px;
  margin: 20px;
}

.register-header {
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  color: white;
  padding: 25px;
  text-align: center;
  position: relative;
}

.register-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="50" cy="50" r="1.5" fill="rgba(255,255,255,0.05)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
}

.register-header h2 {
  position: relative;
  font-weight: 700;
  margin-bottom: 5px;
}

.register-header p {
  position: relative;
  opacity: 0.9;
}

.step-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background-color: #f8f9fa;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #dee2e6;
  color: #6c757d;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-bottom: 5px;
  transition: all 0.3s ease;
}

.step.active .step-number {
  background-color: #6f42c1;
  color: white;
  transform: scale(1.1);
}

.step.completed .step-number {
  background-color: #20c997;
  color: white;
}

.step-line {
  width: 60px;
  height: 2px;
  background-color: #dee2e6;
  margin: 0 10px;
  transition: all 0.3s ease;
}

.step-line.completed {
  background-color: #20c997;
}

.step-label {
  font-size: 0.8rem;
  color: #6c757d;
  text-align: center;
  font-weight: 500;
}

.step-content {
  padding: 30px;
}

.user-type-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  border-radius: 15px;
  height: 100%;
  text-align: center;
}

.user-type-card:hover {
  transform: translateY(-5px);
  border-color: #6f42c1;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.user-type-card.selected {
  border-color: #6f42c1;
  background-color: rgba(111, 66, 193, 0.1);
  transform: translateY(-2px);
}

.user-type-card i {
  color: #6f42c1;
  margin-bottom: 15px;
}

.user-type-card h5 {
  color: #212529;
  font-weight: 600;
}

.user-type-card p {
  color: #6c757d;
  font-size: 0.9rem;
}

.form-label {
  font-weight: 500;
  color: #212529;
  margin-bottom: 8px;
}

.form-control {
  border: 2px solid #e9ecef;
  border-radius: 10px;
  padding: 12px 15px;
  transition: all 0.3s ease;
}

.form-control:focus {
  border-color: #6f42c1;
  box-shadow: 0 0 0 0.25rem rgba(111, 66, 193, 0.1);
}

.btn-primary {
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  border: none;
  border-radius: 50px;
  padding: 12px 25px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(111, 66, 193, 0.3);
}

.btn-primary:disabled {
  opacity: 0.7;
}

.btn-outline-primary {
  border: 2px solid #6f42c1;
  color: #6f42c1;
  border-radius: 50px;
  padding: 12px 25px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-outline-primary:hover {
  background: #6f42c1;
  color: white;
  transform: translateY(-2px);
}

.review-info {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 20px;
}

.review-info p {
  margin-bottom: 10px;
  color: #495057;
}

.category-badge {
  display: inline-block;
  padding: 5px 15px;
  border-radius: 50px;
  background: rgba(111, 66, 193, 0.1);
  color: #6f42c1;
  margin-right: 10px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.category-badge.selected {
  background: #6f42c1;
  color: white;
}

.fade-in {
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
  .step-indicator {
    padding: 15px 10px;
  }
  
  .step-line {
    width: 30px;
  }
  
  .step-label {
    font-size: 0.7rem;
  }
  
  .step-content {
    padding: 20px;
  }
}
</style>