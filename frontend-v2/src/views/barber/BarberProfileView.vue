<template>
  <div class="barber-profile">
    <!-- Header -->
    <div class="profile-header">
      <div class="header-content">
        <h1>Mi Perfil</h1>
        <p class="text-muted">Gestiona tu información personal y profesional</p>
      </div>
    </div>

    <div class="row">
      <!-- Left Column -->
      <div class="col-lg-4">
        <!-- Profile Card -->
        <div class="card mb-4">
          <div class="card-body text-center">
            <div class="profile-avatar">
              <img :src="user.avatar" :alt="user.name" class="avatar-image">
              <button class="avatar-edit-btn" @click="triggerAvatarUpload">
                <i class="fas fa-camera"></i>
              </button>
              <input 
                type="file" 
                ref="avatarInput" 
                @change="handleAvatarUpload" 
                accept="image/*" 
                style="display: none"
              >
            </div>
            <h4 class="profile-name">{{ user.name }}</h4>
            <p class="profile-title">{{ user.title }}</p>
            <div class="profile-rating">
              <i v-for="star in 5" :key="star" 
                 :class="star <= user.rating ? 'fas fa-star' : 'far fa-star'"
                 class="text-warning"></i>
              <span class="rating-value ms-2">{{ user.rating }}/5</span>
            </div>
            <div class="profile-stats">
              <div class="stat">
                <div class="stat-value">{{ user.stats.clients }}</div>
                <div class="stat-label">Clientes</div>
              </div>
              <div class="stat">
                <div class="stat-value">{{ user.stats.appointments }}</div>
                <div class="stat-label">Turnos</div>
              </div>
              <div class="stat">
                <div class="stat-value">{{ user.stats.years }}+</div>
                <div class="stat-label">Años</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Contact Info -->
        <div class="card">
          <div class="card-header">
            <h5 class="mb-0">Información de Contacto</h5>
          </div>
          <div class="card-body">
            <div class="contact-info">
              <div class="contact-item">
                <i class="fas fa-phone"></i>
                <div>
                  <div class="contact-label">Teléfono</div>
                  <div class="contact-value">{{ user.phone }}</div>
                </div>
              </div>
              <div class="contact-item">
                <i class="fas fa-envelope"></i>
                <div>
                  <div class="contact-label">Email</div>
                  <div class="contact-value">{{ user.email }}</div>
                </div>
              </div>
              <div class="contact-item">
                <i class="fas fa-map-marker-alt"></i>
                <div>
                  <div class="contact-label">Ubicación</div>
                  <div class="contact-value">{{ user.location }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Column -->
      <div class="col-lg-8">
        <!-- Profile Form -->
        <div class="card mb-4">
          <div class="card-header">
            <h5 class="mb-0">Información Personal</h5>
          </div>
          <div class="card-body">
            <form @submit.prevent="updateProfile">
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">Nombre</label>
                  <input type="text" class="form-control" v-model="user.name" required>
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">Título Profesional</label>
                  <input type="text" class="form-control" v-model="user.title">
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">Teléfono</label>
                  <input type="tel" class="form-control" v-model="user.phone">
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">Email</label>
                  <input type="email" class="form-control" v-model="user.email" required>
                </div>
                <div class="col-12 mb-3">
                  <label class="form-label">Ubicación</label>
                  <input type="text" class="form-control" v-model="user.location">
                </div>
                <div class="col-12 mb-3">
                  <label class="form-label">Biografía</label>
                  <textarea class="form-control" v-model="user.bio" rows="4"></textarea>
                </div>
                <div class="col-12 mb-3">
                  <label class="form-label">Especialidades</label>
                  <div class="specialties-input">
                    <div v-for="(specialty, index) in user.specialties" :key="index" class="specialty-tag">
                      {{ specialty }}
                      <button type="button" @click="removeSpecialty(index)" class="tag-remove">
                        <i class="fas fa-times"></i>
                      </button>
                    </div>
                    <input 
                      type="text" 
                      class="form-control" 
                      v-model="newSpecialty" 
                      @keydown.enter.prevent="addSpecialty"
                      placeholder="Agregar especialidad"
                    >
                  </div>
                </div>
              </div>
              <div class="form-actions">
                <button type="button" class="btn btn-secondary" @click="resetForm">
                  Cancelar
                </button>
                <button type="submit" class="btn btn-primary" :disabled="saving">
                  <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
                  Guardar Cambios
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- Social Media -->
        <div class="card">
          <div class="card-header">
            <h5 class="mb-0">Redes Sociales</h5>
          </div>
          <div class="card-body">
            <div class="social-links">
              <div class="social-item" v-for="(link, platform) in user.social" :key="platform">
                <div class="social-icon">
                  <i :class="`fab fa-${platform}`"></i>
                </div>
                <input 
                  type="text" 
                  class="form-control" 
                  :placeholder="`${platform.charAt(0).toUpperCase() + platform.slice(1)} URL`"
                  v-model="user.social[platform]"
                >
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue';

export default {
  name: 'BarberProfileView',
  setup() {
    const avatarInput = ref(null);
    const saving = ref(false);
    const newSpecialty = ref('');
    
    const user = reactive({
      name: 'Juan Pérez',
      title: 'Barbero Profesional',
      email: 'juan@barberia.com',
      phone: '+54 11 2345-6789',
      location: 'Buenos Aires, Argentina',
      bio: 'Barbero con más de 10 años de experiencia especializado en cortes modernos y cuidado de barba. Mi objetivo es brindar el mejor servicio con atención personalizada.',
      avatar: 'https://images.unsplash.com/photo-1560250097-0b93528c311a?ixlib=rb-1.2.1&auto=format&fit=crop&w=300&q=80',
      rating: 4.8,
      stats: {
        clients: 254,
        appointments: 1247,
        years: 10
      },
      specialties: ['Cortes Clásicos', 'Barba', 'Coloración', 'Tratamientos'],
      social: {
        facebook: '',
        instagram: '',
        twitter: '',
        whatsapp: ''
      }
    });
    
    const originalUser = JSON.parse(JSON.stringify(user));
    
    const triggerAvatarUpload = () => {
      avatarInput.value.click();
    };
    
    const handleAvatarUpload = (event) => {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          user.avatar = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    };
    
    const addSpecialty = () => {
      if (newSpecialty.value.trim() && !user.specialties.includes(newSpecialty.value.trim())) {
        user.specialties.push(newSpecialty.value.trim());
        newSpecialty.value = '';
      }
    };
    
    const removeSpecialty = (index) => {
      user.specialties.splice(index, 1);
    };
    
    const updateProfile = async () => {
      saving.value = true;
      try {
        // Simular actualización
        await new Promise(resolve => setTimeout(resolve, 1500));
        console.log('Perfil actualizado:', user);
        // En una app real, aquí harías la llamada a la API
        Object.assign(originalUser, JSON.parse(JSON.stringify(user)));
      } catch (error) {
        console.error('Error updating profile:', error);
      } finally {
        saving.value = false;
      }
    };
    
    const resetForm = () => {
      Object.assign(user, JSON.parse(JSON.stringify(originalUser)));
    };
    
    return {
      avatarInput,
      saving,
      newSpecialty,
      user,
      triggerAvatarUpload,
      handleAvatarUpload,
      addSpecialty,
      removeSpecialty,
      updateProfile,
      resetForm
    };
  }
};
</script>

<style scoped>
.profile-header {
  margin-bottom: 30px;
}

.profile-header h1 {
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 5px;
}

.profile-avatar {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto 20px;
}

.avatar-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-edit-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #6f42c1;
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.avatar-edit-btn:hover {
  background: #5a32a3;
  transform: scale(1.1);
}

.profile-name {
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 5px;
}

.profile-title {
  color: #6c757d;
  margin-bottom: 15px;
}

.profile-rating {
  margin-bottom: 20px;
}

.rating-value {
  color: #6c757d;
  font-weight: 500;
}

.profile-stats {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 20px;
}

.stat {
  text-align: center;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #6f42c1;
}

.stat-label {
  color: #6c757d;
  font-size: 0.9rem;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.contact-item i {
  width: 20px;
  color: #6f42c1;
}

.contact-label {
  font-size: 0.8rem;
  color: #6c757d;
  margin-bottom: 2px;
}

.contact-value {
  font-weight: 500;
  color: #2c3e50;
}

.specialties-input {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  padding: 10px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  min-height: 46px;
}

.specialty-tag {
  display: inline-flex;
  align-items: center;
  background: #e9ecef;
  padding: 4px 10px;
  border-radius: 16px;
  font-size: 0.9rem;
  color: #495057;
}

.tag-remove {
  background: none;
  border: none;
  color: #6c757d;
  margin-left: 5px;
  cursor: pointer;
  padding: 0;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.tag-remove:hover {
  background: #dc3545;
  color: white;
}

.specialties-input input {
  flex: 1;
  min-width: 120px;
  border: none;
  outline: none;
  padding: 4px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.social-links {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.social-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.social-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6f42c1;
  font-size: 1.2rem;
}

@media (max-width: 768px) {
  .profile-stats {
    gap: 15px;
  }
  
  .stat-value {
    font-size: 1.2rem;
  }
  
  .social-item {
    flex-direction: column;
    align-items: stretch;
    text-align: center;
  }
  
  .social-icon {
    align-self: center;
  }
}
</style>