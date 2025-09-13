<template>
  <div class="auth-container">
    <div class="forgot-password-container">
      <div class="forgot-password-header">
        <h2><i class="fas fa-key"></i> Recuperar Contraseña</h2>
        <p>Ingresa tu email para restablecer tu contraseña</p>
      </div>
      
      <div class="forgot-password-body">
        <form @submit.prevent="handleSubmit">
          <div class="mb-4">
            <label class="form-label">Email *</label>
            <input 
              type="email" 
              class="form-control" 
              v-model="email" 
              required
              placeholder="tu@email.com"
            >
          </div>
          
          <button type="submit" class="btn btn-primary w-100" :disabled="loading">
            <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
            {{ loading ? 'Enviando...' : 'Enviar Instrucciones' }}
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
        <h4>¡Email enviado!</h4>
        <p>Hemos enviado las instrucciones para restablecer tu contraseña a tu correo electrónico.</p>
        <button class="btn btn-outline-primary" @click="handleBackToLogin">
          Volver al Login
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: 'ForgotPassword',
  setup() {
    const router = useRouter();
    const email = ref('');
    const loading = ref(false);
    const success = ref(false);
    
    const handleSubmit = async () => {
      loading.value = true;
      
      try {
        // Simular envío de email
        await new Promise(resolve => setTimeout(resolve, 1500));
        
        // En una app real, aquí harías la llamada a la API
        // await api.post('/auth/forgot-password', { email: email.value });
        
        success.value = true;
      } catch (error) {
        console.error('Error:', error);
        alert('Error al enviar el email. Por favor, intenta nuevamente.');
      } finally {
        loading.value = false;
      }
    };
    
    const handleBackToLogin = () => {
      router.push('/login');
    };
    
    return {
      email,
      loading,
      success,
      handleSubmit,
      handleBackToLogin
    };
  }
};
</script>

<style scoped>
.forgot-password-container {
  background-color: white;
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(50, 50, 93, 0.1), 0 5px 15px rgba(0, 0, 0, 0.07);
  overflow: hidden;
  width: 100%;
  max-width: 450px;
  margin: 20px;
  position: relative;
}

.forgot-password-header {
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  color: white;
  padding: 25px;
  text-align: center;
  position: relative;
}

.forgot-password-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="50" cy="50" r="1.5" fill="rgba(255,255,255,0.05)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
}

.forgot-password-header h2 {
  position: relative;
  font-weight: 700;
  margin-bottom: 5px;
}

.forgot-password-header p {
  position: relative;
  opacity: 0.9;
  margin-bottom: 0;
}

.forgot-password-body {
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
  .forgot-password-container {
    margin: 10px;
  }
  
  .forgot-password-header {
    padding: 20px;
  }
  
  .forgot-password-body {
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