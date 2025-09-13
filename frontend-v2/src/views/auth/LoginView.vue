<template>
  <div class="auth-layout">
    <!-- Left Side - Branding -->
    <div class="branding-side">
      <div class="branding-content">
        <div class="brand-logo">
          <i class="fas fa-cut"></i>
          <span>StyleCut Pro</span>
        </div>
        <div class="hero-content">
          <h1 class="hero-title">Bienvenido de vuelta</h1>
          <p class="hero-subtitle">
            Tu plataforma favorita para gestionar citas de belleza
          </p>
          <div class="features">
            <div class="feature">
              <i class="fas fa-calendar-check"></i>
              <span>Agenda inteligente</span>
            </div>
            <div class="feature">
              <i class="fas fa-users"></i>
              <span>Gestión de clientes</span>
            </div>
            <div class="feature">
              <i class="fas fa-chart-line"></i>
              <span>Reportes detallados</span>
            </div>
          </div>
        </div>
        <div class="testimonial">
          <blockquote>
            "StyleCut Pro transformó completamente la forma en que manejo mi salón."
          </blockquote>
          <cite>- María González, Propietaria</cite>
        </div>
      </div>
    </div>

    <!-- Right Side - Login Form -->
    <div class="form-side">
      <div class="form-container">
        <LoginForm />
        
        <!-- Additional Options -->
        <div class="auth-extras">
          <div class="divider">
            <span>o continúa con</span>
          </div>
          
          <div class="social-buttons">
            <button class="social-btn google-btn">
              <i class="fab fa-google"></i>
              Google
            </button>
            <button class="social-btn facebook-btn">
              <i class="fab fa-facebook-f"></i>
              Facebook
            </button>
          </div>
          
          <div class="help-links">
            <router-link to="/auth/forgot-password" class="help-link">¿Problemas para acceder?</router-link>
            <a href="#" class="help-link">Centro de ayuda</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import LoginForm from '@/components/auth/LoginForm.vue';

export default {
  name: 'LoginView',
  components: {
    LoginForm
  },
  beforeMount() {
    if (this.$store.getters.isAuthenticated) {
      const userRole = this.$store.getters.userType;
      switch (userRole) {
        case 'client':
          this.$router.push('/dashboard');
          break;
        case 'barber':
          this.$router.push('/barber/dashboard');
          break;
        case 'owner':
          this.$router.push('/owner/dashboard');
          break;
      }
    }
  }
};
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: flex;
  width: 100%;
}

.branding-side {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: white;
}

.branding-content {
  max-width: 400px;
  width: 100%;
}

.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 40px;
}

.brand-logo i {
  margin-right: 12px;
  font-size: 2.2rem;
}

.hero-content {
  margin-bottom: 40px;
}

.hero-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 16px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin-bottom: 32px;
  line-height: 1.5;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 1rem;
}

.feature i {
  width: 20px;
  font-size: 1.1rem;
}

.testimonial {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
}

.testimonial blockquote {
  font-style: italic;
  font-size: 1.1rem;
  margin-bottom: 12px;
}

.testimonial cite {
  font-size: 0.9rem;
  opacity: 0.8;
}

.form-side {
  flex: 1;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.form-container {
  width: 100%;
  max-width: 400px;
}

.auth-extras {
  margin-top: 32px;
}

.divider {
  position: relative;
  text-align: center;
  margin: 24px 0;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #e2e8f0;
}

.divider span {
  background: #f8fafc;
  padding: 0 16px;
  color: #64748b;
  font-size: 0.9rem;
  position: relative;
  z-index: 1;
}

.social-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 24px;
}

.social-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  background: white;
  color: #475569;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.social-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.google-btn:hover {
  border-color: #ea4335;
  color: #ea4335;
}

.facebook-btn:hover {
  border-color: #1877f2;
  color: #1877f2;
}

.help-links {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.help-link {
  color: #667eea;
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 500;
}

@media (max-width: 1024px) {
  .auth-layout {
    flex-direction: column;
  }
  
  .branding-side {
    padding: 30px;
    min-height: 40vh;
  }
  
  .form-side {
    padding: 30px;
    min-height: 60vh;
  }
}

@media (max-width: 768px) {
  .form-container {
    max-width: 100%;
  }
  
  .social-buttons {
    grid-template-columns: 1fr;
  }
  
  .help-links {
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }
  
  .hero-title {
    font-size: 2rem;
  }
}

@media (max-width: 480px) {
  .branding-side, .form-side {
    padding: 20px;
  }
  
  .brand-logo {
    font-size: 1.5rem;
  }
  
  .brand-logo i {
    font-size: 1.8rem;
  }
  
  .hero-title {
    font-size: 1.8rem;
  }
}
</style>