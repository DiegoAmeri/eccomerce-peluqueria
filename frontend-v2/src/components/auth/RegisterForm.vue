<template>
  <div class="register-form-container">
    <div class="register-card">
      <!-- Header -->
      <div class="register-header">
        <div class="brand">
          <i class="fas fa-cut brand-icon"></i>
          <h2 class="brand-text">StyleCut Pro</h2>
        </div>
        <p class="welcome-text">Crear nueva cuenta</p>
      </div>

      <!-- Progress Steps -->
      <div class="progress-container">
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: `${(currentStep / 3) * 100}%` }"></div>
        </div>
        <div class="steps">
          <div 
            v-for="step in 3" 
            :key="step"
            class="step-indicator"
            :class="{
              active: currentStep === step,
              completed: currentStep > step
            }"
          >
            <span class="step-number">{{ step }}</span>
          </div>
        </div>
      </div>

      <!-- Step Content -->
      <div class="step-content">
        <!-- Step 1: User Type -->
        <div v-if="currentStep === 1" class="step-wrapper fade-in">
          <h3 class="step-title">¿Qué tipo de cuenta necesitas?</h3>
          
          <div class="user-types">
            <div 
              class="user-type-card"
              :class="{ selected: userType === 'client' }"
              @click="selectUserType('client')"
            >
              <div class="card-icon">
                <i class="fas fa-user"></i>
              </div>
              <h4>Cliente</h4>
              <p>Busca y reserva citas en salones de belleza</p>
            </div>
            
            <div 
              class="user-type-card"
              :class="{ selected: userType === 'barber' }"
              @click="selectUserType('barber')"
            >
              <div class="card-icon">
                <i class="fas fa-scissors"></i>
              </div>
              <h4>Profesional</h4>
              <p>Trabajo en un salón existente</p>
            </div>
            
            <div 
              class="user-type-card"
              :class="{ selected: userType === 'owner' }"
              @click="selectUserType('owner')"
            >
              <div class="card-icon">
                <i class="fas fa-store"></i>
              </div>
              <h4>Propietario</h4>
              <p>Administro mi propio negocio</p>
            </div>
          </div>
        </div>

        <!-- Step 2: Personal Info -->
        <div v-if="currentStep === 2" class="step-wrapper fade-in">
          <h3 class="step-title">Información personal</h3>
          
          <div class="form-grid">
            <div class="form-group">
              <label class="form-label">Nombre *</label>
              <input type="text" class="form-input" v-model="form.nombre" required>
            </div>
            
            <div class="form-group">
              <label class="form-label">Apellido *</label>
              <input type="text" class="form-input" v-model="form.apellido" required>
            </div>
            
            <div class="form-group">
              <label class="form-label">Email *</label>
              <input type="email" class="form-input" v-model="form.email" required>
            </div>
            
            <div class="form-group">
              <label class="form-label">Teléfono *</label>
              <input type="tel" class="form-input" v-model="form.telefono" required>
            </div>
            
            <div class="form-group">
              <label class="form-label">Contraseña *</label>
              <input type="password" class="form-input" v-model="form.password" required>
            </div>
            
            <div class="form-group">
              <label class="form-label">Confirmar Contraseña *</label>
              <input type="password" class="form-input" v-model="form.confirmPassword" required>
            </div>
          </div>
        </div>

        <!-- Step 3: Additional Info -->
        <div v-if="currentStep === 3" class="step-wrapper fade-in">
          <!-- Client Final Step -->
          <div v-if="userType === 'client'">
            <h3 class="step-title">¡Casi terminamos!</h3>
            <div class="review-card">
              <div class="review-item">
                <strong>Nombre:</strong> {{ form.nombre }} {{ form.apellido }}
              </div>
              <div class="review-item">
                <strong>Email:</strong> {{ form.email }}
              </div>
              <div class="review-item">
                <strong>Teléfono:</strong> {{ form.telefono }}
              </div>
              <div class="review-item">
                <strong>Tipo:</strong> Cliente
              </div>
            </div>
          </div>

          <!-- Professional Info -->
          <div v-else-if="userType === 'barber'">
            <h3 class="step-title">Información profesional</h3>
            
            <div class="form-group">
              <label class="form-label">Selecciona tu salón *</label>
              <select class="form-select" v-model="form.peluqueriaId" required>
                <option value="">Elige un salón</option>
                <option v-for="salon in peluquerias" :key="salon.id" :value="salon.id">
                  {{ salon.nombre }} - {{ salon.direccion }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label class="form-label">Especialidad</label>
              <input type="text" class="form-input" v-model="form.especialidad" 
                     placeholder="Ej: Cortes, colorimetría, barba...">
            </div>

            <div class="form-group">
              <label class="form-label">Años de experiencia</label>
              <input type="number" class="form-input" v-model="form.experiencia" min="0">
            </div>
          </div>

          <!-- Owner Info -->
          <div v-else-if="userType === 'owner'">
            <h3 class="step-title">Información del negocio</h3>
            
            <div class="form-group">
              <label class="form-label">Nombre del negocio *</label>
              <input type="text" class="form-input" v-model="form.nombrePeluqueria" required>
            </div>

            <div class="form-group">
              <label class="form-label">Categoría *</label>
              <div class="category-pills">
                <button
                  type="button"
                  v-for="cat in categories"
                  :key="cat.value"
                  class="category-pill"
                  :class="{ selected: form.categoria === cat.value }"
                  @click="form.categoria = cat.value"
                >
                  <i :class="cat.icon"></i>
                  {{ cat.label }}
                </button>
              </div>
            </div>

            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">Dirección *</label>
                <input type="text" class="form-input" v-model="form.direccion" required>
              </div>
              
              <div class="form-group">
                <label class="form-label">Instagram</label>
                <div class="input-with-prefix">
                  <span class="input-prefix">@</span>
                  <input type="text" class="form-input with-prefix" v-model="form.instagram">
                </div>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">Descripción</label>
              <textarea class="form-textarea" v-model="form.descripcion" rows="3"
                        placeholder="Describe tu negocio y servicios..."></textarea>
            </div>

            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">Hora apertura</label>
                <input type="time" class="form-input" v-model="form.horarioApertura">
              </div>
              
              <div class="form-group">
                <label class="form-label">Hora cierre</label>
                <input type="time" class="form-input" v-model="form.horarioCierre">
              </div>
            </div>
          </div>

          <!-- Terms -->
          <div class="terms-container">
            <label class="checkbox-container">
              <input type="checkbox" v-model="form.aceptoTerminos" required>
              <span class="checkmark"></span>
              Acepto los <a href="#" @click.prevent="showTerms" class="link">términos y condiciones</a>
            </label>
          </div>
        </div>
      </div>

      <!-- Navigation -->
      <div class="navigation">
        <button 
          v-if="currentStep > 1"
          class="nav-button secondary"
          @click="prevStep"
        >
          <i class="fas fa-arrow-left"></i>
          Anterior
        </button>
        
        <button 
          v-if="currentStep < 3"
          class="nav-button primary"
          :class="{ 'full-width': currentStep === 1 }"
          @click="nextStep"
          :disabled="!canProceed"
        >
          Continuar
          <i class="fas fa-arrow-right"></i>
        </button>
        
        <button 
          v-if="currentStep === 3"
          class="nav-button primary"
          @click="handleRegister"
          :disabled="loading || !form.aceptoTerminos"
        >
          <span v-if="loading" class="loading-spinner"></span>
          {{ loading ? 'Registrando...' : 'Crear Cuenta' }}
        </button>
      </div>

      <!-- Login Link -->
      <div class="login-link">
        <span>¿Ya tienes cuenta? </span>
        <router-link to="/auth/login" class="link">Inicia sesión</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
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
    
    const categories = ref([
      { value: 'peluqueria', label: 'Peluquería', icon: 'fas fa-scissors' },
      { value: 'unas', label: 'Uñas', icon: 'fas fa-hand-sparkles' },
      { value: 'estetica', label: 'Estética', icon: 'fas fa-spa' },
      { value: 'barberia', label: 'Barbería', icon: 'fas fa-cut' }
    ]);
    
    const form = ref({
      nombre: '',
      apellido: '',
      email: '',
      telefono: '',
      password: '',
      confirmPassword: '',
      peluqueriaId: '',
      especialidad: '',
      experiencia: '',
      nombrePeluqueria: '',
      categoria: 'peluqueria',
      instagram: '',
      direccion: '',
      descripcion: '',
      horarioApertura: '09:00',
      horarioCierre: '18:00',
      aceptoTerminos: false
    });

    const canProceed = computed(() => {
      if (currentStep.value === 1) {
        return userType.value !== '';
      } else if (currentStep.value === 2) {
        return form.value.nombre && form.value.apellido && 
               form.value.email && form.value.telefono &&
               form.value.password && form.value.confirmPassword &&
               form.value.password === form.value.confirmPassword;
      }
      return true;
    });

    const selectUserType = (type) => {
      userType.value = type;
    };

    const nextStep = () => {
      if (canProceed.value) {
        currentStep.value++;
        if (currentStep.value === 3 && userType.value === 'barber') {
          loadPeluquerias();
        }
      }
    };

    const prevStep = () => {
      currentStep.value--;
    };

    const loadPeluquerias = async () => {
      peluquerias.value = [
        { id: 1, nombre: "Corte Perfecto", direccion: "Av. Siempre Viva 742" },
        { id: 2, nombre: "Estilo Urbano", direccion: "Calle Falsa 123" },
        { id: 3, nombre: "Barbería Clásica", direccion: "Av. Libertador 456" }
      ];
    };

    const showTerms = () => {
      alert("Aquí se mostrarían los términos y condiciones.");
    };

    const handleRegister = async () => {
      loading.value = true;
      
      try {
        const registerData = {
          tipoUsuario: userType.value,
          ...form.value
        };

        setTimeout(() => {
          alert('¡Cuenta creada exitosamente!');
          router.push('/login');
        }, 1500);
        
      } catch (error) {
        console.error('Error en registro:', error);
        alert('Error al crear la cuenta');
      } finally {
        loading.value = false;
      }
    };

    onMounted(() => {
      loadPeluquerias();
    });

    return {
      currentStep,
      userType,
      peluquerias,
      categories,
      form,
      loading,
      canProceed,
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
.register-form-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  position: relative;
}

.register-form-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg width="60" height="60" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg"><g fill="none" fill-rule="evenodd"><g fill="%23ffffff" fill-opacity="0.1"><circle cx="30" cy="30" r="2"/></g></svg>') repeat;
  opacity: 0.3;
}

.register-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  padding: 40px;
  width: 100%;
  max-width: 600px;
  position: relative;
  z-index: 1;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.brand {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.brand-icon {
  font-size: 2.5rem;
  margin-right: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.brand-text {
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.welcome-text {
  color: #6b7280;
  font-size: 1rem;
  margin: 0;
  font-weight: 500;
}

.progress-container {
  margin-bottom: 32px;
}

.progress-bar {
  height: 4px;
  background: #e5e7eb;
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 16px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
}

.steps {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.step-indicator {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  font-weight: 600;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.step-indicator.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transform: scale(1.1);
}

.step-indicator.completed {
  background: #10b981;
  color: white;
}

.step-indicator.completed .step-number::before {
  content: '✓';
}

.step-indicator.completed .step-number {
  display: none;
}

.step-content {
  margin-bottom: 32px;
}

.step-wrapper {
  min-height: 300px;
}

.step-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #374151;
  text-align: center;
  margin-bottom: 24px;
}

.user-types {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.user-type-card {
  background: #f9fafb;
  border: 2px solid #e5e7eb;
  border-radius: 16px;
  padding: 24px 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-type-card:hover {
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.user-type-card.selected {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.card-icon i {
  font-size: 1.5rem;
  color: white;
}

.user-type-card h4 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 8px;
}

.user-type-card p {
  font-size: 0.9rem;
  color: #6b7280;
  margin: 0;
  line-height: 1.4;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-weight: 600;
  color: #374151;
  font-size: 0.9rem;
}

.form-input, .form-select, .form-textarea {
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: #f9fafb;
}

.form-input:focus, .form-select:focus, .form-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: white;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.input-with-prefix {
  position: relative;
  display: flex;
  align-items: center;
}

.input-prefix {
  position: absolute;
  left: 16px;
  color: #6b7280;
  font-weight: 500;
  z-index: 2;
}

.form-input.with-prefix {
  padding-left: 36px;
}

.category-pills {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.category-pill {
  background: #f3f4f6;
  border: 2px solid #e5e7eb;
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.category-pill:hover {
  border-color: #667eea;
}

.category-pill.selected {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: white;
}

.review-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.review-item {
  padding: 8px 0;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
}

.review-item:last-child {
  border-bottom: none;
}

.terms-container {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.checkbox-container {
  display: flex;
  align-items: flex-start;
  cursor: pointer;
  font-size: 0.9rem;
  color: #6b7280;
  user-select: none;
  line-height: 1.4;
}

.checkbox-container input {
  display: none;
}

.checkmark {
  width: 18px;
  height: 18px;
  border: 2px solid #d1d5db;
  border-radius: 4px;
  margin-right: 8px;
  margin-top: 2px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.checkbox-container input:checked + .checkmark {
  background: #667eea;
  border-color: #667eea;
}

.checkbox-container input:checked + .checkmark::after {
  content: '✓';
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.navigation {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.nav-button {
  flex: 1;
  padding: 14px 24px;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.nav-button.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.nav-button.primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.nav-button.secondary {
  background: #f3f4f6;
  color: #6b7280;
  border: 2px solid #e5e7eb;
}

.nav-button.secondary:hover {
  background: #e5e7eb;
}

.nav-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.nav-button.full-width {
  width: 100%;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.login-link {
  text-align: center;
  font-size: 0.9rem;
  color: #6b7280;
}

.link {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: opacity 0.2s ease;
}

.link:hover {
  opacity: 0.8;
  text-decoration: underline;
}

.fade-in {
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 640px) {
  .register-card {
    padding: 24px;
    margin: 10px;
  }

  .user-types {
    grid-template-columns: 1fr;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .category-pills {
    flex-direction: column;
  }

  .category-pill {
    justify-content: center;
  }

  .navigation {
    flex-direction: column;
  }

  .nav-button {
    width: 100%;
  }
}
</style>