<template>
  <div class="auth-container">
    <div class="reset-password-container">
      <div class="reset-password-header">
        <h2><i class="fas fa-lock"></i> Nueva Contraseña</h2>
        <p>Crea una nueva contraseña para tu cuenta</p>
      </div>
      
      <div class="reset-password-body">
        <form @submit.prevent="handleSubmit">
          <div class="mb-3">
            <label class="form-label">Nueva Contraseña *</label>
            <input 
              type="password" 
              class="form-control" 
              v-model="password" 
              required
              placeholder="Mínimo 8 caracteres"
              :class="{ 'is-invalid': passwordError }"
            >
            <div class="invalid-feedback" v-if="passwordError">
              {{ passwordError }}
            </div>
            <div class="form-text">
              La contraseña debe tener al menos 8 caracteres, una mayúscula y un número.
            </div>
          </div>
          
          <div class="mb-4">
            <label class="form-label">Confirmar Contraseña *</label>
            <input 
              type="password" 
              class="form-control" 
              v-model="confirmPassword" 
              required
              placeholder="Repite tu contraseña"
              :class="{ 'is-invalid': confirmPasswordError }"
            >
            <div class="invalid-feedback" v-if="confirmPasswordError">
              {{ confirmPasswordError }}
            </div>
          </div>
          
          <button type="submit" class="btn btn-primary w-100" :disabled="loading">
            <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
            {{ loading ? 'Actualizando...' : 'Actualizar Contraseña' }}
          </button>
          
          <div class="text-center mt-3">
            <router-link to="/auth/login" class="text-decoration-none">
              <i class="fas fa-arrow-left me-1"></i> Volver al Login
            </router-link>
          </div>
        </form>
      </div>
      
      <!-- Success Message -->
      <div v-if="success" class="success-message">
        <div class="success-icon">
          <i class="fas fa-check-circle"></i>
        </div>
        <h4>¡Contraseña actualizada!</h4>
        <p>Tu contraseña ha sido restablecida correctamente.</p>
        <button class="btn btn-primary" @click="handleBackToLogin">
          Iniciar Sesión
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: 'ResetPassword',
  setup() {
    const router = useRouter();
    const password = ref('');
    const confirmPassword = ref('');
    const loading = ref(false);
    const success = ref(false);
    
    const passwordError = computed(() => {
      if (!password.value) return '';
      
      if (password.value.length < 8) {
        return 'La contraseña debe tener al menos 8 caracteres';
      }
      
      if (!/[A-Z]/.test(password.value)) {
        return 'La contraseña debe contener al menos una mayúscula';
      }
      
      if (!/\d/.test(password.value)) {
        return 'La contraseña debe contener al menos un número';
      }
      
      return '';
    });
    
    const confirmPasswordError = computed(() => {
      if (!confirmPassword.value) return '';
      
      if (password.value !== confirmPassword.value) {
        return 'Las contraseñas no coinciden';
      }
      
      return '';
    });
    
    const isFormValid = computed(() => {
      return password.value && 
             confirmPassword.value && 
             !passwordError.value && 
             !confirmPasswordError.value;
    });
    
    const handleSubmit = async () => {
      if (!isFormValid.value) return;
      
      loading.value = true;
      
      try {
        // Simular actualización de contraseña
        await new Promise(resolve => setTimeout(resolve, 1500));
        
        // En una app real, aquí harías la llamada a la API
        // await api.post('/auth/reset-password', { 
        //   token: router.currentRoute.value.query.token,
        //   password: password.value 
        // });
        
        success.value = true;
      } catch (error) {
        console.error('Error:', error);
        alert('Error al actualizar la contraseña. Por favor, intenta nuevamente.');
      } finally {
        loading.value = false;
      }
    };
    
    const handleBackToLogin = () => {
      router.push('/login');
    };
    
    return {
      password,
      confirmPassword,
      loading,
      success,
      passwordError,
      confirmPasswordError,
      handleSubmit,
      handleBackToLogin
    };
  }
};
</script>

<style scoped>
.reset-password-container {
  background-color: white;
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(50, 50, 93, 0.1), 0 5px 15px rgba(0, 0, 0, 0.07);
  overflow: hidden;
  width: 100%;
  max-width: 450px;
  margin: 20px;
  position: relative;
}

.reset-password-header {
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  color: white;
  padding: 25px;
  text-align: center;
  position: relative;
}

.reset-password-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="50" cy="50" r="1.5" fill="rgba(255,255,255,0.05)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
}

.reset-password-header h2 {
  position: relative;
  font-weight: 700;
  margin-bottom: 5px;
}

.reset-password-header p {
  position: relative;
  opacity: 0.9;
  margin-bottom: 0;
}

.reset-password-body {
  padding: 30px;
}

.success-message {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  text-align: center;
}

.success-icon {
  font-size: 4rem;
  color: #28a745;
  margin-bottom: 20px;
}

.success-message h4 {
  color: #28a745;
  margin-bottom: 15px;
}

.success-message p {
  color: #6c757d;
  margin-bottom: 25px;
}

@media (max-width: 576px) {
  .reset-password-container {
    margin: 10px;
  }
  
  .reset-password-header {
    padding: 20px;
  }
  
  .reset-password-body {
    padding: 20px;
  }
  
  .success-message {
    padding: 20px;
  }
  
  .success-icon {
    font-size: 3rem;
  }
}
</style>