<template>
  <div class="auth-layout">
    <div class="auth-background">
      <div class="auth-background-overlay"></div>
      <div class="auth-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>
    
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <router-link to="/" class="auth-logo">
            <i class="fas fa-cut"></i>
            <span>StyleCut Pro</span>
          </router-link>
          <h1>Únete a nosotros</h1>
          <p>Crea tu cuenta y comienza a disfrutar de nuestros servicios</p>
        </div>

        <RegisterForm />
        
        <div class="auth-footer">
          <p>¿Ya tienes una cuenta? 
            <router-link to="/login" class="auth-link">Inicia sesión aquí</router-link>
          </p>
          <div class="auth-divider">
            <span>o</span>
          </div>
          <div class="social-login">
            <button class="btn btn-social btn-google">
              <i class="fab fa-google"></i>
              Registrarse con Google
            </button>
            <button class="btn btn-social btn-facebook">
              <i class="fab fa-facebook-f"></i>
              Registrarse con Facebook
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import RegisterForm from '@/components/auth/RegisterForm.vue';

export default {
  name: 'RegisterView',
  components: {
    RegisterForm
  },
  beforeMount() {
    // Si el usuario ya está autenticado, redirigir al dashboard
    if (this.$store.getters.isAuthenticated) {
      this.$router.push('/dashboard');
    }
  }
};
</script>

<style scoped>
/* Los estilos son los mismos que LoginView.vue */
.auth-layout {
  min-height: 100vh;
  display: flex;
  position: relative;
}

.auth-background {
  flex: 1;
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  position: relative;
  overflow: hidden;
  display: none;
}

.auth-background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="50" cy="50" r="1.5" fill="rgba(255,255,255,0.05)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
}

.auth-shapes {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.shape-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  right: -150px;
}

.shape-2 {
  width: 200px;
  height: 200px;
  bottom: 100px;
  left: -100px;
}

.shape-3 {
  width: 150px;
  height: 150px;
  bottom: -75px;
  right: 100px;
}

.auth-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #f8f9fa;
}

.auth-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 500px;
  animation: slideUp 0.5s ease;
}

.auth-header {
  text-align: center;
  margin-bottom: 30px;
}

.auth-logo {
  display: inline-flex;
  align-items: center;
  font-size: 1.5rem;
  font-weight: 700;
  color: #6f42c1;
  text-decoration: none;
  margin-bottom: 20px;
}

.auth-logo i {
  margin-right: 10px;
  font-size: 2rem;
}

.auth-header h1 {
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 10px;
  font-size: 1.8rem;
}

.auth-header p {
  color: #6c757d;
  margin: 0;
}

.auth-footer {
  margin-top: 30px;
  text-align: center;
}

.auth-link {
  color: #6f42c1;
  text-decoration: none;
  font-weight: 600;
}

.auth-link:hover {
  text-decoration: underline;
}

.auth-divider {
  position: relative;
  margin: 25px 0;
  text-align: center;
}

.auth-divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #e9ecef;
}

.auth-divider span {
  background: white;
  padding: 0 15px;
  color: #6c757d;
  position: relative;
  z-index: 1;
}

.social-login {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.btn-social {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 12px 20px;
  border: 2px solid #e9ecef;
  border-radius: 10px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-social:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.btn-google {
  color: #4285f4;
  border-color: #e9ecef;
}

.btn-google:hover {
  background: #4285f4;
  color: white;
  border-color: #4285f4;
}

.btn-facebook {
  color: #3b5998;
  border-color: #e9ecef;
}

.btn-facebook:hover {
  background: #3b5998;
  color: white;
  border-color: #3b5998;
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

@media (min-width: 992px) {
  .auth-background {
    display: block;
  }
  
  .auth-container {
    padding: 40px;
  }
}

@media (max-width: 576px) {
  .auth-card {
    padding: 30px 20px;
  }
  
  .auth-header h1 {
    font-size: 1.5rem;
  }
}
</style>