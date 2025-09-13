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
          <h1 class="hero-title">Únete a nosotros</h1>
          <p class="hero-subtitle">
            Comienza tu transformación digital en el mundo de la belleza
          </p>
          <div class="stats">
            <div class="stat">
              <span class="stat-number">5,000+</span>
              <span class="stat-label">Profesionales</span>
            </div>
            <div class="stat">
              <span class="stat-number">50,000+</span>
              <span class="stat-label">Citas gestionadas</span>
            </div>
            <div class="stat">
              <span class="stat-number">99%</span>
              <span class="stat-label">Satisfacción</span>
            </div>
          </div>
        </div>
        <div class="benefits">
          <h3>¿Por qué elegir StyleCut Pro?</h3>
          <div class="benefit-list">
            <div class="benefit">
              <i class="fas fa-rocket"></i>
              <div>
                <strong>Fácil de usar</strong>
                <p>Interfaz intuitiva diseñada para todos</p>
              </div>
            </div>
            <div class="benefit">
              <i class="fas fa-shield-alt"></i>
              <div>
                <strong>Seguro y confiable</strong>
                <p>Tus datos están protegidos</p>
              </div>
            </div>
            <div class="benefit">
              <i class="fas fa-headset"></i>
              <div>
                <strong>Soporte 24/7</strong>
                <p>Estamos aquí para ayudarte</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="decorative-elements">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
        <div class="floating-shape shape-4"></div>
      </div>
    </div>

    <!-- Right Side - Register Form -->
    <div class="form-side">
      <div class="form-container">
        <RegisterForm />
        
        <!-- Additional Info -->
        <div class="auth-extras">
          <div class="security-note">
            <i class="fas fa-lock"></i>
            <span>Tu información está segura y protegida con encriptación de nivel bancario</span>
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
    if (this.$store.getters.isAuthenticated) {
      this.$router.push('/dashboard');
    }
  }
};
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1fr 1fr;
}

.branding-side {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.branding-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: white;
  max-width: 450px;
  width: 100%;
}

.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 40px;
  animation: fadeInUp 0.8s ease;
}

.brand-logo i {
  margin-right: 12px;
  font-size: 2.2rem;
}

.hero-content {
  margin-bottom: 40px;
  animation: fadeInUp 0.8s ease 0.2s both;
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

.stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.stat {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.8;
}

.benefits {
  animation: fadeInUp 0.8s ease 0.4s both;
}

.benefits h3 {
  font-size: 1.3rem;
  font-weight: 600;
  margin-bottom: 20px;
}

.benefit-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.benefit {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  text-align: left;
}

.benefit i {
  font-size: 1.2rem;
  margin-top: 4px;
  opacity: 0.9;
}

.benefit strong {
  display: block;
  font-size: 1rem;
  margin-bottom: 4px;
}

.benefit p {
  font-size: 0.9rem;
  opacity: 0.8;
  margin: 0;
  line-height: 1.4;
}

.decorative-elements {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 8s ease-in-out infinite;
}

.shape-1 {
  width: 180px;
  height: 180px;
  top: -90px;
  right: -90px;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  bottom: -60px;
  left: -60px;
  animation-delay: 2s;
}

.shape-3 {
  width: 80px;
  height: 80px;
  top: 30%;
  right: -40px;
  animation-delay: 4s;
}

.shape-4 {
  width: 60px;
  height: 60px;
  top: 60%;
  left: -30px;
  animation-delay: 6s;
}

.form-side {
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  overflow-y: auto;
}

.form-container {
  width: 100%;
  max-width: 600px;
  margin: auto;
}

.auth-extras {
  margin-top: 24px;
}

.security-note {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 8px;
  font-size: 0.85rem;
  color: #667eea;
  text-align: center;
}

.security-note i {
  font-size: 1rem;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-25px) rotate(15deg);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 1200px) {
  .stats {
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
  }

  .stat-number {
    font-size: 1.5rem;
  }
}

@media (max-width: 1024px) {
  .auth-layout {
    grid-template-columns: 1fr;
  }
  
  .branding-side {
    display: none;
  }
  
  .form-side {
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
  
  .stats {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .stat-number {
    font-size: 1.3rem;
  }
  
  .benefits h3 {
    font-size: 1.1rem;
  }
  
  .benefit {
    gap: 12px;
  }
  
  .benefit strong {
    font-size: 0.9rem;
  }
  
  .benefit p {
    font-size: 0.8rem;
  }
}

@media (max-width: 640px) {
  .branding-content {
    padding: 20px 0;
  }
  
  .brand-logo {
    font-size: 1.5rem;
    margin-bottom: 24px;
  }
  
  .brand-logo i {
    font-size: 1.8rem;
  }
  
  .hero-content {
    margin-bottom: 24px;
  }
  
  .security-note {
    font-size: 0.8rem;
    padding: 10px 12px;
  }
}
</style>