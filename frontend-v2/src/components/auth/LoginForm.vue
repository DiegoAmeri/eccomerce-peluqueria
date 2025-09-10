<template>
  <div class="auth-container">
    <div class="login-container">
      <div class="login-header">
        <h2><i class="fas fa-cut"></i> Iniciar Sesión</h2>
        <p>Ingresa a tu cuenta de StyleCut Pro</p>
      </div>
      
      <div class="login-body">
        <form @submit.prevent="handleLogin">
          <div class="mb-3">
            <label class="form-label">Email</label>
            <input type="email" class="form-control" v-model="loginForm.email" required>
          </div>
          
          <div class="mb-3">
            <label class="form-label">Contraseña</label>
            <input type="password" class="form-control" v-model="loginForm.password" required>
          </div>
          
          <div class="mb-3 form-check">
            <input type="checkbox" class="form-check-input" id="remember" v-model="loginForm.remember">
            <label class="form-check-label" for="remember">Recordarme</label>
          </div>
          
          <button type="submit" class="btn btn-primary w-100" :disabled="loading">
            <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
            {{ loading ? 'Iniciando sesión...' : 'Iniciar Sesión' }}
          </button>
          
          <div class="text-center mt-3">
            <p>¿No tienes cuenta? <router-link to="/register">Regístrate aquí</router-link></p>
          </div>
        </form>
      </div>
    </div>
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
    
    const handleLogin = async () => {
      loading.value = true;
      
      try {
        await store.dispatch('login', loginForm.value);
        router.push('/dashboard');
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
      handleLogin
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

.login-container {
  background-color: white;
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(50, 50, 93, 0.1), 0 5px 15px rgba(0, 0, 0, 0.07);
  overflow: hidden;
  width: 100%;
  max-width: 400px;
}

.login-header {
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  color: white;
  padding: 25px;
  text-align: center;
  position: relative;
}

.login-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="50" cy="50" r="1.5" fill="rgba(255,255,255,0.05)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
}

.login-header h2 {
  position: relative;
  font-weight: 700;
  margin-bottom: 5px;
}

.login-header p {
  position: relative;
  opacity: 0.9;
  margin-bottom: 0;
}

.login-body {
  padding: 25px;
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
</style>