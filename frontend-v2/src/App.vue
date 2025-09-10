<template>
  <div id="app">
    <!-- Barra de navegación global -->
    <NavBar v-if="showNavbar" />
    
    <!-- Sidebar condicional según el tipo de usuario -->
    <SideBar v-if="showSidebar" />
    
    <!-- Contenedor principal -->
    <div :class="['main-content', { 'with-sidebar': showSidebar }]">
      <!-- Transición suave entre vistas -->
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>
    
    <!-- Notificaciones toast globales -->
    <NotificationToast />
    
    <!-- Modal global -->
    <ModalDialog />
    
    <!-- Spinner de carga global -->
    <LoadingSpinner v-if="loading" />
  </div>
</template>

<script>
import { computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'

// Componentes globales
import NavBar from '@/components/common/NavBar.vue'
import SideBar from '@/components/common/SideBar.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import NotificationToast from '@/components/common/NotificationToast.vue'
import ModalDialog from '@/components/common/ModalDialog.vue'

export default {
  name: 'App',
  components: {
    NavBar,
    SideBar,
    LoadingSpinner,
    NotificationToast,
    ModalDialog
  },
  setup() {
    const route = useRoute()
    const store = useStore()
    
    // Determinar si mostrar la navbar (no en login/register)
    const showNavbar = computed(() => {
      return !['Login', 'Register'].includes(route.name)
    })
    
    // Determinar si mostrar el sidebar (solo en dashboards)
    const showSidebar = computed(() => {
      return route.meta.requiresSidebar
    })
    
    // Estado de carga global
    const loading = computed(() => store.state.loading)
    
    // Verificar autenticación al cambiar de ruta
    watch(
      () => route.path,
      () => {
        // Verificar si el token ha expirado
        store.dispatch('auth/verifyToken')
      }
    )
    
    return {
      showNavbar,
      showSidebar,
      loading
    }
  },
  mounted() {
    // Inicializar la verificación de autenticación al cargar la app
    this.$store.dispatch('auth/verifyToken')
    
    // Escuchar eventos de red para mostrar estado de conexión
    window.addEventListener('online', this.handleOnlineStatus)
    window.addEventListener('offline', this.handleOfflineStatus)
  },
  beforeUnmount() {
    // Limpiar event listeners
    window.removeEventListener('online', this.handleOnlineStatus)
    window.removeEventListener('offline', this.handleOfflineStatus)
  },
  methods: {
    handleOnlineStatus() {
      this.$store.dispatch('notifications/add', {
        type: 'success',
        message: 'Conexión restablecida'
      })
    },
    handleOfflineStatus() {
      this.$store.dispatch('notifications/add', {
        type: 'warning',
        message: 'Estás trabajando sin conexión'
      })
    }
  }
}
</script>

<style>
/* Estilos globales */
:root {
  --primary-color: #8a4fff;
  --secondary-color: #ff7ac6;
  --accent-color: #42b883;
  --dark-color: #1a1a2e;
  --light-color: #f8f9fa;
  --gray-color: #6c757d;
  --success-color: #28a745;
  --warning-color: #ffc107;
  --danger-color: #dc3545;
  --shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  --transition: all 0.3s ease;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Poppins', sans-serif;
  background-color: #f5f7fb;
  color: #333;
  line-height: 1.6;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding-top: 70px; /* Altura de la navbar */
  transition: var(--transition);
}

.main-content.with-sidebar {
  padding-left: 250px; /* Ancho del sidebar */
}

/* Transiciones entre vistas */
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

.fade-enter-active,
.fade-leave-active {
  transition: var(--transition);
}

/* Responsividad */
@media (max-width: 992px) {
  .main-content.with-sidebar {
    padding-left: 0;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding-top: 60px;
  }
}

/* Personalización de scrollbar */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: var(--primary-color);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #6b3cc0;
}

/* Animaciones globales */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideInUp {
  from { 
    opacity: 0;
    transform: translateY(20px);
  }
  to { 
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

.fade-in {
  animation: fadeIn 0.5s ease forwards;
}

.slide-in-up {
  animation: slideInUp 0.5s ease forwards;
}

.pulse {
  animation: pulse 0.6s ease infinite;
}

/* Utilidades globales */
.text-center { text-align: center; }
.text-left { text-align: left; }
.text-right { text-align: right; }

.d-flex { display: flex; }
.flex-column { flex-direction: column; }
.align-center { align-items: center; }
.justify-center { justify-content: center; }
.justify-between { justify-content: space-between; }

.mt-1 { margin-top: 0.5rem; }
.mt-2 { margin-top: 1rem; }
.mt-3 { margin-top: 1.5rem; }
.mt-4 { margin-top: 2rem; }

.mb-1 { margin-bottom: 0.5rem; }
.mb-2 { margin-bottom: 1rem; }
.mb-3 { margin-bottom: 1.5rem; }
.mb-4 { margin-bottom: 2rem; }

.p-1 { padding: 0.5rem; }
.p-2 { padding: 1rem; }
.p-3 { padding: 1.5rem; }
.p-4 { padding: 2rem; }

.w-100 { width: 100%; }
.h-100 { height: 100%; }
</style>