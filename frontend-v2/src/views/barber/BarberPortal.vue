<template>
  <div class="barber-portal">
    <NavBar />
    
    <div class="portal-container">
      <SideBar />
      
      <main class="main-content">
        <div class="content-header">
          <div class="header-title">
            <h1>{{ currentPageTitle }}</h1>
            <p class="text-muted" v-if="pageDescription">{{ pageDescription }}</p>
          </div>
          
          <div class="header-actions" v-if="showHeaderActions">
            <button class="btn btn-outline-primary me-2" @click="refreshData" v-if="showRefreshButton">
              <i class="fas fa-sync-alt" :class="{ 'fa-spin': refreshing }"></i>
            </button>
            <button class="btn btn-primary" @click="quickAction" v-if="showQuickAction">
              <i class="fas fa-plus me-2"></i>
              {{ quickActionText }}
            </button>
          </div>
        </div>
        
        <div class="content-body">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </main>
    </div>
    
    <!-- Mobile Navigation -->
    <div class="mobile-nav">
      <router-link v-for="item in mobileNavItems" :key="item.path" 
                   :to="item.path" 
                   class="mobile-nav-item"
                   :class="{ active: $route.path === item.path }">
        <i :class="item.icon"></i>
        <span>{{ item.title }}</span>
      </router-link>
    </div>
    
    <!-- Loading Overlay -->
    <div v-if="globalLoading" class="global-loading">
      <div class="loading-content">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
        <p>Cargando...</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useStore } from 'vuex';
import NavBar from '@/components/common/NavBar.vue';
import SideBar from '@/components/common/SideBar.vue';

export default {
  name: 'BarberPortal',
  components: {
    NavBar,
    SideBar
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const store = useStore();
    
    const refreshing = ref(false);
    const globalLoading = ref(false);
    
    const pageTitles = {
      '/barber/dashboard': 'Dashboard',
      '/barber/appointments': 'Mi Agenda',
      '/barber/appointments/new': 'Nuevo Turno',
      '/barber/availability': 'Disponibilidad',
      '/barber/clients': 'Mis Clientes',
      '/barber/services': 'Mis Servicios',
      '/barber/performance': 'Mi Rendimiento',
      '/barber/profile': 'Mi Perfil'
    };
    
    const pageDescriptions = {
      '/barber/dashboard': 'Resumen de tu actividad y estadísticas',
      '/barber/appointments': 'Gestiona tus turnos y agenda',
      '/barber/availability': 'Configura tu disponibilidad horaria',
      '/barber/clients': 'Gestiona tu cartera de clientes',
      '/barber/services': 'Administra tus servicios y precios'
    };
    
    const mobileNavItems = [
      { path: '/barber/dashboard', icon: 'fas fa-home', title: 'Inicio' },
      { path: '/barber/appointments', icon: 'fas fa-calendar', title: 'Agenda' },
      { path: '/barber/availability', icon: 'fas fa-user-clock', title: 'Disponibilidad' },
      { path: '/barber/clients', icon: 'fas fa-users', title: 'Clientes' },
      { path: '/barber/profile', icon: 'fas fa-user', title: 'Perfil' }
    ];
    
    const currentPageTitle = computed(() => {
      return pageTitles[route.path] || 'Portal del Barbero';
    });
    
    const pageDescription = computed(() => {
      return pageDescriptions[route.path] || '';
    });
    
    const showHeaderActions = computed(() => {
      return ['/barber/appointments', '/barber/clients', '/barber/services'].includes(route.path);
    });
    
    const showRefreshButton = computed(() => {
      return ['/barber/appointments', '/barber/clients'].includes(route.path);
    });
    
    const showQuickAction = computed(() => {
      return ['/barber/appointments', '/barber/clients'].includes(route.path);
    });
    
    const quickActionText = computed(() => {
      if (route.path === '/barber/appointments') return 'Nuevo Turno';
      if (route.path === '/barber/clients') return 'Nuevo Cliente';
      return 'Nuevo';
    });
    
    const refreshData = async () => {
      refreshing.value = true;
      // Simular refresh de datos
      await new Promise(resolve => setTimeout(resolve, 1000));
      refreshing.value = false;
    };
    
    const quickAction = () => {
      if (route.path === '/barber/appointments') {
        router.push('/barber/appointments/new');
      } else if (route.path === '/barber/clients') {
        // Lógica para nuevo cliente
        console.log('Agregar nuevo cliente');
      }
    };
    
    // Simular carga global
    watch(() => route.path, (newPath) => {
      globalLoading.value = true;
      setTimeout(() => {
        globalLoading.value = false;
      }, 500);
    }, { immediate: true });
    
    return {
      refreshing,
      globalLoading,
      currentPageTitle,
      pageDescription,
      mobileNavItems,
      showHeaderActions,
      showRefreshButton,
      showQuickAction,
      quickActionText,
      refreshData,
      quickAction
    };
  }
};
</script>

<style scoped>
.barber-portal {
  min-height: 100vh;
  background: #f8f9fa;
  display: flex;
  flex-direction: column;
}

.portal-container {
  display: flex;
  flex: 1;
  padding-top: 76px; /* Altura del navbar */
}

.main-content {
  flex: 1;
  padding: 20px;
  margin-left: 0;
  transition: margin-left 0.3s ease;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
  padding: 20px 0;
}

.header-title h1 {
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
  font-size: 2rem;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.content-body {
  min-height: calc(100vh - 200px);
}

/* Mobile Navigation */
.mobile-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  display: flex;
  justify-content: space-around;
  padding: 10px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  display: none;
}

.mobile-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: #6c757d;
  font-size: 0.8rem;
  padding: 8px 12px;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.mobile-nav-item i {
  font-size: 1.2rem;
  margin-bottom: 4px;
}

.mobile-nav-item.active {
  color: #6f42c1;
  background: rgba(111, 66, 193, 0.1);
}

.mobile-nav-item:hover {
  color: #6f42c1;
  transform: translateY(-2px);
}

/* Loading Overlay */
.global-loading {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.loading-content {
  text-align: center;
}

.loading-content p {
  margin-top: 15px;
  color: #6c757d;
  font-weight: 500;
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

@media (min-width: 992px) {
  .portal-container.with-sidebar .main-content {
    margin-left: 280px;
  }
}

@media (max-width: 991.98px) {
  .mobile-nav {
    display: flex;
  }
  
  .main-content {
    padding-bottom: 80px; /* Espacio para la navegación móvil */
  }
  
  .content-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 15px;
  }
  
  .header-title h1 {
    font-size: 1.5rem;
  }
  
  .content-header {
    margin-bottom: 20px;
    padding: 15px 0;
  }
}
</style>