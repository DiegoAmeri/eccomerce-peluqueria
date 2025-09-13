<template>
  <div class="client-profile-view">
    <!-- Header -->
    <div class="profile-header">
      <div class="header-content">
        <h1>Mi Perfil</h1>
        <p class="text-muted">Gestiona tu información personal y preferencias</p>
      </div>
    </div>

    <div class="row">
      <!-- Left Sidebar -->
      <div class="col-lg-4">
        <div class="card profile-card">
          <div class="card-body text-center">
            <div class="profile-avatar-container">
              <img :src="user.avatar" :alt="user.name" class="profile-avatar">
              <button class="avatar-edit-btn" @click="triggerAvatarUpload">
                <i class="fas fa-camera"></i>
              </button>
              <input type="file" ref="avatarInput" class="d-none" accept="image/*" @change="handleAvatarUpload">
            </div>
            
            <h4 class="profile-name">{{ user.name }}</h4>
            <p class="profile-email">{{ user.email }}</p>
            <p class="profile-phone">{{ user.phone }}</p>
            
            <div class="profile-stats">
              <div class="stat-item">
                <div class="stat-number">{{ userStats.totalAppointments }}</div>
                <div class="stat-label">Turnos</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ userStats.favoriteSalons }}</div>
                <div class="stat-label">Favoritos</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ userStats.memberSince }}</div>
                <div class="stat-label">Miembro desde</div>
              </div>
            </div>
          </div>
        </div>

        <div class="card mt-4">
          <div class="card-header">
            <h6 class="mb-0">Preferencias</h6>
          </div>
          <div class="card-body">
            <div class="preference-item">
              <label class="form-check form-switch">
                <input class="form-check-input" type="checkbox" v-model="preferences.emailNotifications">
                <span class="form-check-label">Notificaciones por Email</span>
              </label>
            </div>
            <div class="preference-item">
              <label class="form-check form-switch">
                <input class="form-check-input" type="checkbox" v-model="preferences.smsNotifications">
                <span class="form-check-label">Notificaciones por SMS</span>
              </label>
            </div>
            <div class="preference-item">
              <label class="form-check form-switch">
                <input class="form-check-input" type="checkbox" v-model="preferences.marketingEmails">
                <span class="form-check-label">Emails de Marketing</span>
              </label>
            </div>
          </div>
        </div>
      </div>

      <!-- Main Content -->
      <div class="col-lg-8">
        <!-- Personal Information -->
        <div class="card">
          <div class="card-header">
            <h5 class="mb-0">Información Personal</h5>
          </div>
          <div class="card-body">
            <form @submit.prevent="savePersonalInfo">
              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="form-label">Nombre *</label>
                    <input type="text" class="form-control" v-model="user.name" required>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="form-label">Apellido *</label>
                    <input type="text" class="form-control" v-model="user.lastName" required>
                  </div>
                </div>
              </div>

              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="form-label">Email *</label>
                    <input type="email" class="form-control" v-model="user.email" required>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="form-label">Teléfono *</label>
                    <input type="tel" class="form-control" v-model="user.phone" required>
                  </div>
                </div>
              </div>

              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="form-label">Fecha de Nacimiento</label>
                    <input type="date" class="form-control" v-model="user.birthDate">
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="form-label">Género</label>
                    <select class="form-select" v-model="user.gender">
                      <option value="">Seleccionar</option>
                      <option value="male">Masculino</option>
                      <option value="female">Femenino</option>
                      <option value="other">Otro</option>
                      <option value="prefer-not-to-say">Prefiero no decir</option>
                    </select>
                  </div>
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label">Dirección</label>
                <input type="text" class="form-control" v-model="user.address" placeholder="Dirección completa">
              </div>

              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="form-label">Ciudad</label>
                    <input type="text" class="form-control" v-model="user.city">
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="form-label">Código Postal</label>
                    <input type="text" class="form-control" v-model="user.postalCode">
                  </div>
                </div>
              </div>

              <div class="d-flex justify-content-end">
                <button type="submit" class="btn btn-primary" :disabled="saving">
                  <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
                  {{ saving ? 'Guardando...' : 'Guardar Cambios' }}
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- Security -->
        <div class="card mt-4">
          <div class="card-header">
            <h5 class="mb-0">Seguridad</h5>
          </div>
          <div class="card-body">
            <form @submit.prevent="changePassword">
              <div class="mb-3">
                <label class="form-label">Contraseña Actual</label>
                <input type="password" class="form-control" v-model="password.current" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Nueva Contraseña</label>
                <input type="password" class="form-control" v-model="password.new" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Confirmar Nueva Contraseña</label>
                <input type="password" class="form-control" v-model="password.confirm" required>
              </div>
              <div class="d-flex justify-content-end">
                <button type="submit" class="btn btn-primary" :disabled="changingPassword">
                  <span v-if="changingPassword" class="spinner-border spinner-border-sm me-2"></span>
                  {{ changingPassword ? 'Cambiando...' : 'Cambiar Contraseña' }}
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- Connected Accounts -->
        <div class="card mt-4">
          <div class="card-header">
            <h5 class="mb-0">Cuentas Conectadas</h5>
          </div>
          <div class="card-body">
            <div class="connected-account">
              <div class="account-info">
                <i class="fab fa-google fa-2x text-danger me-3"></i>
                <div>
                  <h6 class="mb-0">Google</h6>
                  <p class="text-muted mb-0">Conectado como {{ user.email }}</p>
                </div>
              </div>
              <button class="btn btn-outline-danger btn-sm">Desconectar</button>
            </div>
            
            <div class="connected-account mt-3">
              <div class="account-info">
                <i class="fab fa-facebook fa-2x text-primary me-3"></i>
                <div>
                  <h6 class="mb-0">Facebook</h6>
                  <p class="text-muted mb-0">No conectado</p>
                </div>
              </div>
              <button class="btn btn-outline-primary btn-sm">Conectar</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Success Toast -->
    <NotificationToast 
      :show="showSuccessToast" 
      type="success" 
      title="¡Éxito!"
      :message="toastMessage"
      @update:show="showSuccessToast = false"
    />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useStore } from 'vuex';
import NotificationToast from '@/components/common/NotificationToast.vue';

export default {
  name: 'ClientProfileView',
  components: {
    NotificationToast
  },
  setup() {
    const store = useStore();
    
    const saving = ref(false);
    const changingPassword = ref(false);
    const showSuccessToast = ref(false);
    const toastMessage = ref('');
    const avatarInput = ref(null);
    
    const user = ref({
      name: 'Juan',
      lastName: 'Pérez',
      email: 'juan@example.com',
      phone: '+54 11 1234-5678',
      birthDate: '1990-01-01',
      gender: 'male',
      address: 'Av. Siempre Viva 742',
      city: 'Ciudad',
      postalCode: '1234',
      avatar: 'https://ui-avatars.com/api/?name=Juan+Perez&background=6f42c1&color=fff'
    });
    
    const userStats = ref({
      totalAppointments: 12,
      favoriteSalons: 3,
      memberSince: '2023'
    });
    
    const preferences = ref({
      emailNotifications: true,
      smsNotifications: false,
      marketingEmails: true
    });
    
    const password = ref({
      current: '',
      new: '',
      confirm: ''
    });
    
    const savePersonalInfo = async () => {
      saving.value = true;
      
      try {
        // Simular guardado
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        toastMessage.value = 'Información personal actualizada correctamente';
        showSuccessToast.value = true;
      } catch (error) {
        console.error('Error saving personal info:', error);
      } finally {
        saving.value = false;
      }
    };
    
    const changePassword = async () => {
      changingPassword.value = true;
      
      try {
        if (password.value.new !== password.value.confirm) {
          throw new Error('Las contraseñas no coinciden');
        }
        
        // Simular cambio de contraseña
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        toastMessage.value = 'Contraseña cambiada correctamente';
        showSuccessToast.value = true;
        password.value = { current: '', new: '', confirm: '' };
      } catch (error) {
        alert(error.message);
      } finally {
        changingPassword.value = false;
      }
    };
    
    const triggerAvatarUpload = () => {
      avatarInput.value?.click();
    };
    
    const handleAvatarUpload = (event) => {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          user.value.avatar = e.target.result;
          toastMessage.value = 'Foto de perfil actualizada';
          showSuccessToast.value = true;
        };
        reader.readAsDataURL(file);
      }
    };
    
    onMounted(() => {
      // Cargar datos del usuario desde el store
      const userData = store.getters.userData;
      if (userData) {
        user.value = { ...user.value, ...userData };
      }
    });
    
    return {
      user,
      userStats,
      preferences,
      password,
      saving,
      changingPassword,
      showSuccessToast,
      toastMessage,
      avatarInput,
      savePersonalInfo,
      changePassword,
      triggerAvatarUpload,
      handleAvatarUpload
    };
  }
};
</script>

<style scoped>
.client-profile-view {
  padding: 20px;
}

.profile-header {
  margin-bottom: 30px;
}

.profile-header h1 {
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 5px;
}

.profile-card {
  text-align: center;
}

.profile-avatar-container {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #fff;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
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

.profile-email,
.profile-phone {
  color: #6c757d;
  margin-bottom: 5px;
}

.profile-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 1.5rem;
  font-weight: 700;
  color: #6f42c1;
  margin-bottom: 5px;
}

.stat-label {
  color: #6c757d;
  font-size: 0.9rem;
}

.preference-item {
  padding: 12px 0;
  border-bottom: 1px solid #f1f3f4;
}

.preference-item:last-child {
  border-bottom: none;
}

.form-check-input:checked {
  background-color: #6f42c1;
  border-color: #6f42c1;
}

.connected-account {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
}

.account-info {
  display: flex;
  align-items: center;
}

@media (max-width: 768px) {
  .client-profile-view {
    padding: 15px;
  }
  
  .profile-stats {
    flex-direction: column;
    gap: 15px;
  }
  
  .connected-account {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
  
  .account-info {
    flex-direction: column;
    text-align: center;
  }
  
  .account-info i {
    margin-right: 0;
    margin-bottom: 8px;
  }
}
</style>