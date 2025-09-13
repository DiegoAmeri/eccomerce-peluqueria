<template>
  <div class="login-form-container">
    <div class="form-header">
      <h2 class="form-title">Ingresa a tu cuenta</h2>
      <p class="form-subtitle">Ingresa tus credenciales para continuar</p>
    </div>
    
    <form @submit.prevent="handleLogin" class="login-form">
      <div class="input-group">
        <label for="email">Correo electrónico</label>
        <div class="input-field">
          <i class="fas fa-envelope"></i>
          <input 
            type="email" 
            id="email"
            v-model="loginForm.email" 
            placeholder="tu@email.com"
            required
          >
        </div>
      </div>
      
      <div class="input-group">
        <label for="password">Contraseña</label>
        <div class="input-field">
          <i class="fas fa-lock"></i>
          <input 
            :type="showPassword ? 'text' : 'password'" 
            id="password"
            v-model="loginForm.password" 
            placeholder="Tu contraseña"
            required
          >
          <button 
            type="button" 
            class="toggle-password"
            @click="showPassword = !showPassword"
          >
            <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
          </button>
        </div>
      </div>
      
      <div class="form-options">
        <label class="checkbox-container">
          <input type="checkbox" v-model="loginForm.remember">
          <span class="checkmark"></span>
          Recordarme
        </label>
        <a href="#" class="forgot-password">¿Olvidaste tu contraseña?</a>
      </div>
      
      <button type="submit" class="login-button" :disabled="loading">
        <span v-if="loading" class="loading-spinner"></span>
        <span>{{ loading ? 'Iniciando sesión...' : 'Iniciar Sesión' }}</span>
      </button>
      
      <div class="register-link">
        <span>¿No tienes cuenta? </span>
        <router-link to="/auth/register" class="link">Regístrate aquí</router-link>
      </div>
    </form>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

export default {
  name: 'LoginForm',
  setup() {
    const store = useStore();
    const router = useRouter();
    
    const loginForm = ref({
      email: '',
      password: '',
      remember: false
    });
    
    const loading = ref(false);
    const showPassword = ref(false);
    
    const handleLogin = async () => {
      loading.value = true;
      
      try {
        await store.dispatch('login', loginForm.value);
        const userType = store.getters.userType;
        
        // Redirigir según el tipo de usuario
        switch (userType) {
          case 'client':
            router.push('/dashboard');
            break;
          case 'barber':
            router.push('/barber/dashboard');
            break;
          case 'owner':
            router.push('/owner/dashboard');
            break;
          default:
            router.push('/dashboard');
        }
      } catch (error) {
        console.error('Login error:', error);
        alert('Error al iniciar sesión. Por favor, verifica tus credenciales.');
      } finally {
        loading.value = false;
      }
    };
    
    return {
      loginForm,
      loading,
      showPassword,
      handleLogin
    };
  }
};
</script>

<style scoped>
.login-form-container {
  width: 100%;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 8px;
}

.form-subtitle {
  color: #6B7280;
  font-size: 16px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-group label {
  font-weight: 500;
  color: #374151;
  font-size: 14px;
}

.input-field {
  position: relative;
  display: flex;
  align-items: center;
}

.input-field i {
  position: absolute;
  left: 16px;
  color: #9CA3AF;
  z-index: 2;
}

.input-field input {
  width: 100%;
  padding: 14px 16px 14px 44px;
  border: 1px solid #E5E7EB;
  border-radius: 10px;
  font-size: 16px;
  transition: all 0.2s ease;
  background: #F9FAFB;
}

.input-field input:focus {
  outline: none;
  border-color: #6366F1;
  box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
  background: white;
}

.toggle-password {
  position: absolute;
  right: 16px;
  background: none;
  border: none;
  color: #9CA3AF;
  cursor: pointer;
  padding: 4px;
  transition: color 0.2s ease;
}

.toggle-password:hover {
  color: #6366F1;
}

.form-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 10px 0;
}

.checkbox-container {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
  color: #4B5563;
  user-select: none;
}

.checkbox-container input {
  display: none;
}

.checkmark {
  width: 18px;
  height: 18px;
  border: 1.5px solid #D1D5DB;
  border-radius: 4px;
  margin-right: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.checkbox-container input:checked + .checkmark {
  background: #6366F1;
  border-color: #6366F1;
}

.checkbox-container input:checked + .checkmark::after {
  content: '✓';
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.forgot-password {
  color: #6366F1;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: opacity 0.2s ease;
}

.forgot-password:hover {
  opacity: 0.8;
}

.login-button {
  background: #6366F1;
  border: none;
  border-radius: 10px;
  padding: 16px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 10px;
}

.login-button:hover:not(:disabled) {
  background: #4F46E5;
  transform: translateY(-2px);
  box-shadow: 0 7px 14px rgba(99, 102, 241, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

.login-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
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

.register-link {
  text-align: center;
  font-size: 14px;
  color: #6B7280;
  margin-top: 24px;
}

.register-link .link {
  color: #6366F1;
  text-decoration: none;
  font-weight: 500;
  transition: opacity 0.2s ease;
}

.register-link .link:hover {
  opacity: 0.8;
}

@media (max-width: 480px) {
  .form-options {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .input-field input {
    padding: 12px 14px 12px 40px;
  }
}
</style>