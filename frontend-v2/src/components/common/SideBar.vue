<template>
  <div class="sidebar" :class="{ collapsed: isCollapsed }">
    <!-- Sidebar Header -->
    <div class="sidebar-header">
      <div class="sidebar-brand">
        <i class="fas fa-cut"></i>
        <span class="brand-text" v-if="!isCollapsed">StyleCut Pro</span>
      </div>
      <button class="sidebar-toggle" @click="toggleCollapse">
        <i :class="isCollapsed ? 'fas fa-chevron-right' : 'fas fa-chevron-left'"></i>
      </button>
    </div>

    <!-- User Profile -->
    <div class="user-profile" v-if="!isCollapsed">
      <img :src="userAvatar" :alt="userName" class="user-avatar">
      <div class="user-info">
        <h6 class="user-name">{{ userName }}</h6>
        <p class="user-role">{{ userRole }}</p>
        <span class="user-status" :class="userStatus">
          <i class="fas fa-circle"></i> {{ statusText }}
        </span>
      </div>
    </div>

    <!-- Navigation Menu -->
    <nav class="sidebar-nav">
      <div v-for="group in navigationGroups" :key="group.title" class="nav-group">
        <h6 class="nav-group-title" v-if="!isCollapsed && group.title">{{ group.title }}</h6>
        <ul class="nav-list">
          <li v-for="item in group.items" :key="item.path" class="nav-item">
            <router-link :to="item.path" class="nav-link" active-class="active" 
                         :title="isCollapsed ? item.title : ''">
              <i :class="item.icon"></i>
              <span class="nav-text" v-if="!isCollapsed">{{ item.title }}</span>
              <span class="nav-badge" v-if="!isCollapsed && item.badge">{{ item.badge }}</span>
              <span class="nav-tooltip" v-if="isCollapsed">{{ item.title }}</span>
            </router-link>
          </li>
        </ul>
      </div>
    </nav>

    <!-- Sidebar Footer -->
    <div class="sidebar-footer" v-if="!isCollapsed">
      <div class="status-indicator">
        <div class="status-dot" :class="userStatus"></div>
        <span>Estado: {{ statusText }}</span>
      </div>
      <button class="btn btn-sm btn-outline-secondary" @click="toggleStatus">
        Cambiar estado
      </button>
    </div>

    <!-- Collapsed Profile -->
    <div class="collapsed-profile" v-if="isCollapsed">
      <img :src="userAvatar" :alt="userName" class="collapsed-avatar">
      <div class="collapsed-status" :class="userStatus"></div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

export default {
  name: 'SideBar',
  setup() {
    const store = useStore();
    const router = useRouter();
    
    const isCollapsed = ref(false);
    const userStatus = ref('online'); // online, away, busy, offline
    
    const isAuthenticated = computed(() => store.getters.isAuthenticated);
    const userName = computed(() => store.getters.userName);
    const userAvatar = computed(() => store.getters.userAvatar);
    const userType = computed(() => store.getters.userType);
    
    const statusText = computed(() => {
      const statusMap = {
        'online': 'En línea',
        'away': 'Ausente',
        'busy': 'Ocupado',
        'offline': 'Desconectado'
      };
      return statusMap[userStatus.value] || 'Desconectado';
    });
    
    const userRole = computed(() => {
      const roleMap = {
        'client': 'Cliente',
        'barber': 'Peluquero',
        'owner': 'Dueño de Peluquería'
      };
      return roleMap[userType.value] || 'Usuario';
    });
    
    const navigationGroups = computed(() => {
      const commonItems = [
        { path: '/dashboard', title: 'Dashboard', icon: 'fas fa-home', badge: null },
        { path: '/salons', title: 'Peluquerías', icon: 'fas fa-store', badge: null }
      ];
      
      const clientItems = [
        { path: '/my-appointments', title: 'Mis Turnos', icon: 'fas fa-calendar', badge: '3' },
        { path: '/favorites', title: 'Favoritos', icon: 'fas fa-heart', badge: null },
        { path: '/reviews', title: 'Mis Reseñas', icon: 'fas fa-star', badge: null }
      ];
      
      const barberItems = [
        { path: '/barber/appointments', title: 'Mi Agenda', icon: 'fas fa-calendar-alt', badge: '5' },
        { path: '/barber/availability', title: 'Disponibilidad', icon: 'fas fa-user-clock', badge: null },
        { path: '/barber/clients', title: 'Mis Clientes', icon: 'fas fa-users', badge: null },
        { path: '/barber/services', title: 'Mis Servicios', icon: 'fas fa-scissors', badge: null }
      ];
      
      const ownerItems = [
        { path: '/owner/salons', title: 'Mis Peluquerías', icon: 'fas fa-store', badge: '2' },
        { path: '/owner/employees', title: 'Empleados', icon: 'fas fa-users', badge: null },
        { path: '/owner/finances', title: 'Finanzas', icon: 'fas fa-chart-line', badge: null },
        { path: '/owner/reports', title: 'Reportes', icon: 'fas fa-file-alt', badge: null }
      ];
      
      let mainItems = [...commonItems];
      let secondaryItems = [
        { path: '/profile', title: 'Mi Perfil', icon: 'fas fa-user', badge: null },
        { path: '/settings', title: 'Configuración', icon: 'fas fa-cog', badge: null },
        { path: '/help', title: 'Ayuda & Soporte', icon: 'fas fa-question-circle', badge: null }
      ];
      
      if (userType.value === 'client') {
        mainItems = [...mainItems, ...clientItems];
      } else if (userType.value === 'barber') {
        mainItems = [...mainItems, ...barberItems];
      } else if (userType.value === 'owner') {
        mainItems = [...mainItems, ...ownerItems];
      }
      
      return [
        { title: 'Navegación Principal', items: mainItems },
        { title: 'Cuenta', items: secondaryItems }
      ];
    });
    
    const toggleCollapse = () => {
      isCollapsed.value = !isCollapsed.value;
      // Guardar preferencia en localStorage
      localStorage.setItem('sidebarCollapsed', isCollapsed.value);
    };
    
    const toggleStatus = () => {
      const statuses = ['online', 'away', 'busy', 'offline'];
      const currentIndex = statuses.indexOf(userStatus.value);
      const nextIndex = (currentIndex + 1) % statuses.length;
      userStatus.value = statuses[nextIndex];
      
      // En una app real, actualizaríamos el estado en el backend
      console.log('Estado cambiado a:', userStatus.value);
    };
    
    onMounted(() => {
      // Cargar preferencia de colapso desde localStorage
      const savedState = localStorage.getItem('sidebarCollapsed');
      if (savedState !== null) {
        isCollapsed.value = savedState === 'true';
      }
      
      // Ajustar colapso automáticamente en móviles
      const checkScreenSize = () => {
        if (window.innerWidth < 992) {
          isCollapsed.value = true;
        }
      };
      
      checkScreenSize();
      window.addEventListener('resize', checkScreenSize);
    });
    
    return {
      isCollapsed,
      userStatus,
      isAuthenticated,
      userName,
      userAvatar,
      userRole,
      statusText,
      navigationGroups,
      toggleCollapse,
      toggleStatus
    };
  }
};
</script>

<style scoped>
.sidebar {
  width: 280px;
  height: 100vh;
  background: linear-gradient(180deg, #2c3e50 0%, #3498db 100%);
  color: white;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
}

.sidebar.collapsed {
  width: 70px;
}

.sidebar-header {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 1.2rem;
  font-weight: 700;
}

.sidebar-brand i {
  font-size: 1.5rem;
  color: #6f42c1;
  background: white;
  padding: 8px;
  border-radius: 10px;
}

.brand-text {
  transition: opacity 0.3s ease;
}

.sidebar.collapsed .brand-text {
  opacity: 0;
  visibility: hidden;
}

.sidebar-toggle {
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: white;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sidebar-toggle:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: rotate(180deg);
}

/* User Profile */
.user-profile {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(255, 255, 255, 0.2);
  margin-bottom: 15px;
  transition: all 0.3s ease;
}

.user-avatar:hover {
  border-color: rgba(255, 255, 255, 0.5);
  transform: scale(1.05);
}

.user-info h6 {
  margin: 0 0 5px 0;
  font-weight: 600;
  font-size: 1.1rem;
}

.user-role {
  margin: 0 0 10px 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
}

.user-status {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.user-status.online {
  background: rgba(46, 204, 113, 0.2);
  color: #2ecc71;
}

.user-status.away {
  background: rgba(241, 196, 15, 0.2);
  color: #f1c40f;
}

.user-status.busy {
  background: rgba(231, 76, 60, 0.2);
  color: #e74c3c;
}

.user-status.offline {
  background: rgba(149, 165, 166, 0.2);
  color: #95a5a6;
}

.user-status i {
  font-size: 0.6rem;
}

/* Navigation */
.sidebar-nav {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.nav-group {
  margin-bottom: 25px;
}

.nav-group-title {
  padding: 0 20px 10px 20px;
  margin: 0;
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: rgba(255, 255, 255, 0.6);
  font-weight: 600;
}

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  margin: 5px 0;
}

.nav-link {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
  border-left: 4px solid transparent;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  padding-left: 25px;
}

.nav-link.active {
  background: linear-gradient(90deg, rgba(111, 66, 193, 0.2) 0%, transparent 100%);
  color: white;
  border-left-color: #6f42c1;
}

.nav-link i {
  width: 25px;
  text-align: center;
  font-size: 1.1rem;
  transition: transform 0.3s ease;
}

.nav-link:hover i {
  transform: scale(1.2);
}

.nav-text {
  margin-left: 15px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.sidebar.collapsed .nav-text {
  opacity: 0;
  visibility: hidden;
}

.nav-badge {
  margin-left: auto;
  background: #e74c3c;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.7rem;
  font-weight: 600;
  min-width: 20px;
  text-align: center;
}

.nav-tooltip {
  position: absolute;
  left: 100%;
  top: 50%;
  transform: translateY(-50%);
  background: #2c3e50;
  color: white;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 0.9rem;
  white-space: nowrap;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  z-index: 1001;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
  pointer-events: none;
}

.sidebar.collapsed .nav-link:hover .nav-tooltip {
  opacity: 1;
  visibility: visible;
  left: calc(100% + 15px);
}

/* Sidebar Footer */
.sidebar-footer {
  padding: 15px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
}

.status-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 10px;
  font-size: 0.9rem;
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.status-dot.online {
  background: #2ecc71;
  box-shadow: 0 0 10px #2ecc71;
}

.status-dot.away {
  background: #f1c40f;
  box-shadow: 0 0 10px #f1c40f;
}

.status-dot.busy {
  background: #e74c3c;
  box-shadow: 0 0 10px #e74c3c;
}

.status-dot.offline {
  background: #95a5a6;
  box-shadow: 0 0 10px #95a5a6;
}

.sidebar-footer .btn {
  width: 100%;
  border-color: rgba(255, 255, 255, 0.3);
  color: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
}

.sidebar-footer .btn:hover {
  border-color: rgba(255, 255, 255, 0.5);
  color: white;
  transform: translateY(-2px);
}

/* Collapsed Profile */
.collapsed-profile {
  position: relative;
  padding: 15px 0;
  text-align: center;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.collapsed-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.collapsed-status {
  position: absolute;
  bottom: 12px;
  right: 15px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid #2c3e50;
}

.collapsed-status.online {
  background: #2ecc71;
}

.collapsed-status.away {
  background: #f1c40f;
}

.collapsed-status.busy {
  background: #e74c3c;
}

.collapsed-status.offline {
  background: #95a5a6;
}

/* Scrollbar Styling */
.sidebar-nav::-webkit-scrollbar {
  width: 4px;
}

.sidebar-nav::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}

.sidebar-nav::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
}

.sidebar-nav::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

/* Responsive */
@media (max-width: 992px) {
  .sidebar {
    transform: translateX(-100%);
    box-shadow: none;
  }
  
  .sidebar.collapsed {
    transform: translateX(0);
    width: 70px;
  }
  
  .sidebar:not(.collapsed) {
    transform: translateX(0);
    width: 280px;
  }
}

@media (max-width: 576px) {
  .sidebar:not(.collapsed) {
    width: 100%;
    max-width: 280px;
  }
}
</style>