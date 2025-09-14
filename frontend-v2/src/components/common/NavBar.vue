<template>
  <nav class="navbar navbar-expand-lg navbar-dark" v-if="isAuthenticated">
    <div class="container-fluid">
      <button class="navbar-toggler me-2" type="button" @click="toggleMobileMenu">
        <span class="navbar-toggler-icon"></span>
      </button>
      
      <router-link class="navbar-brand" to="/">
        <i class="fas fa-cut me-2"></i>StyleCuti Pro
      </router-link>
      
      <!-- Desktop Navigation -->
      <div class="collapse navbar-collapse" id="navbarContent">
        <ul class="navbar-nav me-auto" v-if="isAuthenticated">
          <li class="nav-item" v-for="item in navItems" :key="item.path">
            <router-link class="nav-link" :to="item.path" active-class="active">
              <i :class="item.icon + ' me-1'"></i> {{ item.title }}
            </router-link>
          </li>
        </ul>
        
        <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
          <li class="nav-item dropdown" v-if="isAuthenticated">
            <a class="nav-link dropdown-toggle" href="#" role="button" @click.prevent="toggleUserDropdown">
              <img :src="userAvatar" class="profile-img me-2">
              <span>{{ userName }}</span>
              <span class="badge bg-success ms-2" v-if="userStatus === 'online'">
                <i class="fas fa-circle fa-xs"></i> En línea
              </span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end" :class="{ show: showUserDropdown }">
              <li><a class="dropdown-item" href="#" @click="goToProfile">
                <i class="fas fa-user me-2"></i>Mi Perfil
              </a></li>
              <li><a class="dropdown-item" href="#" @click="goToSettings">
                <i class="fas fa-cog me-2"></i>Configuración
              </a></li>
              <li><a class="dropdown-item" href="#" @click="toggleNotifications">
                <i class="fas fa-bell me-2"></i>Notificaciones
                <span class="badge bg-danger float-end" v-if="unreadNotifications > 0">
                  {{ unreadNotifications }}
                </span>
              </a></li>
              <li><hr class="dropdown-divider"></li>
              <li><hr class="dropdown-divider"></li>
              <li>
                <router-link to="/auth/login" class="dropdown-item" @click="logout">
                  <i class="fas fa-sign-out-alt me-2"></i>Cerrar Sesión
                </router-link>
              </li>
            </ul>
          </li>
          
          <template v-else>
            <li class="nav-item">
              <router-link to="/auth/login" class="btn btn-outline-primary me-2">
                Iniciar Sesión
              </router-link>
              <router-link to="/auth/register" class="btn btn-primary">
                Registrarse
              </router-link>
            </li>
          </template>
        </ul>
      </div>
      
      <!-- Mobile Notifications -->
      <div class="d-lg-none">
        <button class="btn btn-link nav-link position-relative" @click="toggleNotifications">
          <i class="fas fa-bell"></i>
          <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" 
                v-if="unreadNotifications > 0">
            {{ unreadNotifications }}
            <span class="visually-hidden">Notificaciones no leídas</span>
          </span>
        </button>
      </div>
    </div>
    
    <!-- Notifications Panel -->
    <div class="notifications-panel" :class="{ show: showNotifications }">
      <div class="notifications-header">
        <h6>Notificaciones</h6>
        <button class="btn-close" @click="hideNotifications"></button>
      </div>
      <div class="notifications-body">
        <div v-if="notificationsLoading" class="text-center py-3">
          <LoadingSpinner size="small" />
        </div>
        <div v-else-if="notifications.length === 0" class="text-center py-3">
          <i class="fas fa-bell-slash fa-2x text-muted"></i>
          <p class="text-muted mt-2">No hay notificaciones</p>
        </div>
        <div v-else>
          <div v-for="notification in notifications" :key="notification.id" 
               class="notification-item" :class="{ unread: !notification.read }">
            <div class="notification-icon">
              <i :class="notification.icon"></i>
            </div>
            <div class="notification-content">
              <p class="notification-text">{{ notification.message }}</p>
              <small class="notification-time">{{ formatTime(notification.timestamp) }}</small>
            </div>
            <button class="notification-action" @click="markAsRead(notification.id)">
              <i class="fas fa-check"></i>
            </button>
          </div>
        </div>
      </div>
      <div class="notifications-footer">
        <button class="btn btn-link" @click="markAllAsRead" v-if="notifications.length > 0">
          Marcar todas como leídas
        </button>
      </div>
    </div>
    
    <!-- Mobile Menu Overlay -->
    <div class="mobile-menu-overlay" :class="{ show: showMobileMenu }" @click="hideMobileMenu"></div>
    
    <!-- Mobile Sidebar -->
    <div class="mobile-sidebar" :class="{ show: showMobileMenu }">
      <div class="mobile-sidebar-header">
        <img :src="userAvatar" class="mobile-user-avatar" v-if="isAuthenticated">
        <div class="mobile-user-info" v-if="isAuthenticated">
          <h6>{{ userName }}</h6>
          <p class="text-muted">{{ userEmail }}</p>
        </div>
        <button class="btn-close" @click="hideMobileMenu"></button>
      </div>
      <div class="mobile-sidebar-body">
        <div class="nav flex-column">
          <router-link v-for="item in navItems" :key="item.path" 
                       :to="item.path" class="nav-link" @click="hideMobileMenu">
            <i :class="item.icon"></i> {{ item.title }}
          </router-link>
          <hr v-if="isAuthenticated">
          <a href="#" class="nav-link" @click="goToProfile" v-if="isAuthenticated">
            <i class="fas fa-user"></i> Mi Perfil
          </a>
          <a href="#" class="nav-link" @click="goToSettings" v-if="isAuthenticated">
            <i class="fas fa-cog"></i> Configuración
          </a>
          <a href="#" class="nav-link" @click="logout" v-if="isAuthenticated">
            <i class="fas fa-sign-out-alt"></i> Cerrar Sesión
          </a>
          <template v-else>
            <router-link to="/login" class="nav-link" @click="hideMobileMenu">
              <i class="fas fa-sign-in-alt"></i> Iniciar Sesión
            </router-link>
            <router-link to="/register" class="nav-link" @click="hideMobileMenu">
              <i class="fas fa-user-plus"></i> Registrarse
            </router-link>
          </template>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';

export default {
  name: 'NavBar',
  components: {
    LoadingSpinner
  },
  setup() {
    const store = useStore();
    const router = useRouter();
    
    const showUserDropdown = ref(false);
    const showNotifications = ref(false);
    const showMobileMenu = ref(false);
    const notificationsLoading = ref(false);
    
    const isAuthenticated = computed(() => store.getters.isAuthenticated);
    const userName = computed(() => store.getters.userName);
    const userEmail = computed(() => store.getters.userEmail);
    const userAvatar = computed(() => store.getters.userAvatar);
    const userStatus = computed(() => store.getters.userStatus);
    const userType = computed(() => store.getters.userType);
    
    const notifications = ref([
      {
        id: 1,
        message: "Tu cita de mañana a las 10:00 ha sido confirmada",
        icon: "fas fa-calendar-check",
        timestamp: new Date(Date.now() - 3600000), // 1 hora atrás
        read: false
      },
      {
        id: 2,
        message: "Nuevo mensaje de Peluquería Estilo Urbano",
        icon: "fas fa-comment",
        timestamp: new Date(Date.now() - 10800000), // 3 horas atrás
        read: true
      },
      {
        id: 3,
        message: "¡Oferta especial! 20% de descuento en cortes esta semana",
        icon: "fas fa-tag",
        timestamp: new Date(Date.now() - 86400000), // 1 día atrás
        read: true
      }
    ]);
    
    const unreadNotifications = computed(() => {
      return notifications.value.filter(n => !n.read).length;
    });
    
    const navItems = computed(() => {
      const baseItems = [
        { path: '/dashboard', title: 'Dashboard', icon: 'fas fa-home' },
        { path: '/salons', title: 'Peluquerías', icon: 'fas fa-store' }
      ];
      
      if (userType.value === 'client') {
        return [
          ...baseItems,
          { path: '/my-appointments', title: 'Mis Turnos', icon: 'fas fa-calendar' },
          { path: '/favorites', title: 'Favoritos', icon: 'fas fa-heart' }
        ];
      } else if (userType.value === 'barber') {
        return [
          ...baseItems,
          { path: '/barber/appointments', title: 'Agenda', icon: 'fas fa-calendar-alt' },
          { path: '/barber/availability', title: 'Disponibilidad', icon: 'fas fa-user-clock' }
        ];
      } else if (userType.value === 'owner') {
        return [
          ...baseItems,
          { path: '/owner/salons', title: 'Mis Peluquerías', icon: 'fas fa-store' },
          { path: '/owner/employees', title: 'Empleados', icon: 'fas fa-users' }
        ];
      }
      
      return baseItems;
    });
    
    const toggleUserDropdown = () => {
      showUserDropdown.value = !showUserDropdown.value;
      showNotifications.value = false;
    };
    
    const toggleNotifications = async () => {
      if (!showNotifications.value) {
        notificationsLoading.value = true;
        // Simular carga de notificaciones
        setTimeout(() => {
          notificationsLoading.value = false;
        }, 500);
      }
      showNotifications.value = !showNotifications.value;
      showUserDropdown.value = false;
    };
    
    const hideNotifications = () => {
      showNotifications.value = false;
    };
    
    const toggleMobileMenu = () => {
      showMobileMenu.value = !showMobileMenu.value;
    };
    
    const hideMobileMenu = () => {
      showMobileMenu.value = false;
    };
    
    const markAsRead = (notificationId) => {
      const notification = notifications.value.find(n => n.id === notificationId);
      if (notification) {
        notification.read = true;
      }
    };
    
    const markAllAsRead = () => {
      notifications.value.forEach(notification => {
        notification.read = true;
      });
    };
    
    const goToProfile = () => {
      router.push('/profile');
      showUserDropdown.value = false;
    };
    
    const goToSettings = () => {
      router.push('/settings');
      showUserDropdown.value = false;
    };
    
    const logout = () => {
      if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
        store.dispatch('logout');
        router.push('/auth/login');
        showUserDropdown.value = false;
      }
    };
    
    const formatTime = (timestamp) => {
      const now = new Date();
      const diff = now - timestamp;
      const minutes = Math.floor(diff / 60000);
      const hours = Math.floor(diff / 3600000);
      const days = Math.floor(diff / 86400000);
      
      if (minutes < 60) {
        return `Hace ${minutes} min`;
      } else if (hours < 24) {
        return `Hace ${hours} h`;
      } else {
        return `Hace ${days} días`;
      }
    };
    
    const handleClickOutside = (event) => {
      // Cerrar dropdowns al hacer clic fuera
      if (!event.target.closest('.dropdown') && showUserDropdown.value) {
        showUserDropdown.value = false;
      }
      if (!event.target.closest('.notifications-panel') && showNotifications.value) {
        hideNotifications();
      }
    };
    
    onMounted(() => {
      document.addEventListener('click', handleClickOutside);
    });
    
    onUnmounted(() => {
      document.removeEventListener('click', handleClickOutside);
    });
    
    return {
      isAuthenticated,
      userName,
      userEmail,
      userAvatar,
      userStatus,
      navItems,
      showUserDropdown,
      showNotifications,
      showMobileMenu,
      notificationsLoading,
      notifications,
      unreadNotifications,
      toggleUserDropdown,
      toggleNotifications,
      hideNotifications,
      toggleMobileMenu,
      hideMobileMenu,
      markAsRead,
      markAllAsRead,
      goToProfile,
      goToSettings,
      logout,
      formatTime
    };
  }
};
</script>

<style scoped>
.navbar {
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
  padding: 0.8rem 1rem;
  position: sticky;
  top: 0;
  z-index: 1030;
}

.navbar-brand {
  font-weight: 700;
  font-size: 1.5rem;
  transition: transform 0.3s ease;
}

.navbar-brand:hover {
  transform: scale(1.05);
}

.profile-img {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.nav-link:hover .profile-img {
  border-color: rgba(255, 255, 255, 0.8);
}

.nav-link {
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
}

.nav-link.active {
  font-weight: 600;
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background: white;
  border-radius: 2px;
}

.dropdown-menu {
  border: none;
  border-radius: 12px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
  margin-top: 10px;
}

.dropdown-item {
  padding: 0.6rem 1.2rem;
  transition: all 0.3s ease;
  border-radius: 6px;
  margin: 2px 6px;
  width: auto;
}

.dropdown-item:hover {
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  color: white;
  transform: translateX(5px);
}

/* Notifications Panel */
.notifications-panel {
  position: fixed;
  top: 70px;
  right: 20px;
  width: 350px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  z-index: 1040;
  opacity: 0;
  transform: translateY(-20px) scale(0.95);
  visibility: hidden;
  transition: all 0.3s ease;
}

.notifications-panel.show {
  opacity: 1;
  transform: translateY(0) scale(1);
  visibility: visible;
}

.notifications-header {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notifications-header h6 {
  margin: 0;
  font-weight: 600;
}

.notifications-body {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px 0;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 12px 20px;
  border-bottom: 1px solid #f8f9fa;
  transition: background-color 0.3s ease;
}

.notification-item:hover {
  background-color: #f8f9fa;
}

.notification-item.unread {
  background-color: #f0f4ff;
}

.notification-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
}

.notification-text {
  margin: 0;
  font-size: 0.9rem;
  color: #333;
}

.notification-time {
  color: #6c757d;
  font-size: 0.8rem;
}

.notification-action {
  background: none;
  border: none;
  color: #6c757d;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.notification-action:hover {
  background-color: #6f42c1;
  color: white;
}

.notifications-footer {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  text-align: center;
}

/* Mobile Menu */
.mobile-menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1040;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.mobile-menu-overlay.show {
  opacity: 1;
  visibility: visible;
}

.mobile-sidebar {
  position: fixed;
  top: 0;
  left: -300px;
  width: 300px;
  height: 100%;
  background: white;
  z-index: 1050;
  transition: all 0.3s ease;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
}

.mobile-sidebar.show {
  left: 0;
}

.mobile-sidebar-header {
  padding: 20px;
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  color: white;
  display: flex;
  align-items: center;
  position: relative;
}

.mobile-user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(255, 255, 255, 0.3);
  margin-right: 15px;
}

.mobile-user-info h6 {
  margin: 0;
  font-weight: 600;
}

.mobile-user-info p {
  margin: 0;
  font-size: 0.9rem;
  opacity: 0.9;
}

.mobile-sidebar-header .btn-close {
  position: absolute;
  top: 20px;
  right: 20px;
  filter: invert(1);
}

.mobile-sidebar-body {
  padding: 20px 0;
  height: calc(100% - 110px);
  overflow-y: auto;
}

.mobile-sidebar-body .nav-link {
  color: #333;
  padding: 15px 25px;
  font-weight: 500;
  transition: all 0.3s ease;
  border-left: 4px solid transparent;
}

.mobile-sidebar-body .nav-link:hover,
.mobile-sidebar-body .nav-link.router-link-active {
  background-color: #f8f9fa;
  border-left-color: #6f42c1;
  color: #6f42c1;
  padding-left: 30px;
}

.mobile-sidebar-body .nav-link i {
  width: 25px;
  text-align: center;
  margin-right: 10px;
}

/* Responsive */
@media (max-width: 992px) {
  .navbar-collapse {
    background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    padding: 15px;
    border-radius: 0 0 15px 15px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
  }
  
  .notifications-panel {
    right: 10px;
    left: 10px;
    width: auto;
    max-width: 400px;
    margin: 0 auto;
  }
}

@media (max-width: 576px) {
  .navbar-brand {
    font-size: 1.3rem;
  }
  
  .mobile-sidebar {
    width: 280px;
    left: -280px;
  }
  
  .notifications-panel {
    top: 60px;
  }
}
</style>