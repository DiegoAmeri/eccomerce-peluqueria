import { createRouter, createWebHistory } from 'vue-router';
import { useStore } from 'vuex';

// Layout Components
import MainLayout from '@/layouts/MainLayout.vue';
import AuthLayout from '@/layouts/AuthLayout.vue';

// Auth Views
import LoginView from '@/views/auth/LoginView.vue';
import RegisterView from '@/views/auth/RegisterView.vue';
import ForgotPasswordView from '@/views/auth/ForgotPasswordView.vue';
import ResetPasswordView from '@/views/auth/ResetPasswordView.vue';

// Client Views
import ClientDashboard from '@/views/client/ClientDashboard.vue';
import SalonListView from '@/views/client/SalonListView.vue';
import SalonDetailView from '@/views/client/SalonDetailView.vue';
import MyAppointmentsView from '@/views/client/MyAppointmentsView.vue';
import FavoritesView from '@/views/client/FavoritesView.vue';
import ClientProfileView from '@/views/client/ClientProfileView.vue';
import BookingView from '@/views/client/BookingView.vue';
import HomeView from '@/views/client/HomeView.vue';

// Barber Views
import BarberDashboard from '@/views/barber/BarberDashboard.vue';
import BarberAppointmentsView from '@/views/barber/BarberAppointmentsView.vue';
import BarberAvailabilityView from '@/views/barber/BarberAvailabilityView.vue';
import BarberClientsView from '@/views/barber/BarberClientsView.vue';
import BarberServicesView from '@/views/barber/BarberServicesView.vue';
import BarberProfileView from '@/views/barber/BarberProfileView.vue';

// Owner Views
import OwnerDashboard from '@/views/owner/OwnerDashboard.vue';
import OwnerSalonsView from '@/views/owner/OwnerSalonsView.vue';
import OwnerEmployeesView from '@/views/owner/OwnerEmployeesView.vue';
import OwnerFinancesView from '@/views/owner/OwnerFinancesView.vue';
import OwnerReportsView from '@/views/owner/OwnerReportsView.vue';
import OwnerSettingsView from '@/views/owner/OwnerSettingsView.vue';

// Common Views
import NotFoundView from '@/views/NotFoundView.vue';
import ServerErrorView from '@/views/ServerErrorView.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomeView,
    meta: { requiresAuth: false, hideSidebar: true }
  },

  // Auth Routes
  {
    path: '/auth',
    component: AuthLayout,
    meta: { requiresAuth: false, hideSidebar: true },
    children: [
      {
        path: 'login',
        name: 'Login',
        component: LoginView,
        meta: { title: 'Iniciar Sesión' }
      },
      {
        path: 'register',
        name: 'Register',
        component: RegisterView,
        meta: { title: 'Crear Cuenta' }
      },
      {
        path: 'forgot-password',
        name: 'ForgotPassword',
        component: ForgotPasswordView,
        meta: { title: 'Recuperar Contraseña' }
      },
      {
        path: 'reset-password/:token',
        name: 'ResetPassword',
        component: ResetPasswordView,
        meta: { title: 'Restablecer Contraseña' }
      }
    ]
  },

  // Dashboard redirect
  {
    path: '/dashboard',
    redirect: '/client/dashboard'
  },

  // Client Routes
  {
    path: '/client',
    component: MainLayout,
    meta: { requiresAuth: true, role: 'client' },
    children: [
      {
        path: '',
        redirect: 'dashboard'
      },
      {
        path: 'dashboard',
        name: 'ClientDashboard',
        component: ClientDashboard,
        meta: { title: 'Dashboard', icon: 'fas fa-home' }
      },
      {
        path: 'salons',
        name: 'Salons',
        component: SalonListView,
        meta: { title: 'Peluquerías', icon: 'fas fa-store' }
      },
      {
        path: 'salons/:id',
        name: 'SalonDetail',
        component: SalonDetailView,
        meta: { title: 'Detalle de Peluquería' }
      },
      {
        path: 'salons/:id/booking',
        name: 'Booking',
        component: BookingView,
        meta: { title: 'Reservar Turno' }
      },
      {
        path: 'my-appointments',
        name: 'MyAppointments',
        component: MyAppointmentsView,
        meta: { title: 'Mis Turnos', icon: 'fas fa-calendar' }
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: FavoritesView,
        meta: { title: 'Favoritos', icon: 'fas fa-heart' }
      },
      {
        path: 'profile',
        name: 'ClientProfile',
        component: ClientProfileView,
        meta: { title: 'Mi Perfil', icon: 'fas fa-user' }
      }
    ]
  },

  // Barber Routes
  {
    path: '/barber',
    component: MainLayout,
    meta: { requiresAuth: true, role: 'barber' },
    children: [
      {
        path: '',
        redirect: 'dashboard'
      },
      {
        path: 'dashboard',
        name: 'BarberDashboard',
        component: BarberDashboard,
        meta: { title: 'Dashboard', icon: 'fas fa-home' }
      },
      {
        path: 'appointments',
        name: 'BarberAppointments',
        component: BarberAppointmentsView,
        meta: { title: 'Mi Agenda', icon: 'fas fa-calendar-alt' }
      },
      {
        path: 'availability',
        name: 'BarberAvailability',
        component: BarberAvailabilityView,
        meta: { title: 'Disponibilidad', icon: 'fas fa-user-clock' }
      },
      {
        path: 'clients',
        name: 'BarberClients',
        component: BarberClientsView,
        meta: { title: 'Mis Clientes', icon: 'fas fa-users' }
      },
      {
        path: 'services',
        name: 'BarberServices',
        component: BarberServicesView,
        meta: { title: 'Mis Servicios', icon: 'fas fa-scissors' }
      },
      {
        path: 'profile',
        name: 'BarberProfile',
        component: BarberProfileView,
        meta: { title: 'Mi Perfil', icon: 'fas fa-user' }
      }
    ]
  },

  // Owner Routes
  {
    path: '/owner',
    component: MainLayout,
    meta: { requiresAuth: true, role: 'owner' },
    children: [
      {
        path: '',
        redirect: 'dashboard'
      },
      {
        path: 'dashboard',
        name: 'OwnerDashboard',
        component: OwnerDashboard,
        meta: { title: 'Dashboard', icon: 'fas fa-home' }
      },
      {
        path: 'salons',
        name: 'OwnerSalons',
        component: OwnerSalonsView,
        meta: { title: 'Mis Peluquerías', icon: 'fas fa-store' }
      },
      {
        path: 'salons/:id',
        name: 'OwnerSalonDetail',
        component: () => import('@/views/owner/OwnerSalonDetailView.vue'),
        meta: { title: 'Gestión de Peluquería' }
      },
      {
        path: 'salons/:id/edit',
        name: 'OwnerSalonEdit',
        component: () => import('@/views/owner/OwnerSalonEditView.vue'),
        meta: { title: 'Editar Peluquería' }
      },
      {
        path: 'employees',
        name: 'OwnerEmployees',
        component: OwnerEmployeesView,
        meta: { title: 'Empleados', icon: 'fas fa-users' }
      },
      {
        path: 'employees/:id',
        name: 'OwnerEmployeeDetail',
        component: () => import('@/views/owner/OwnerEmployeeDetailView.vue'),
        meta: { title: 'Detalle de Empleado' }
      },
      {
        path: 'finances',
        name: 'OwnerFinances',
        component: OwnerFinancesView,
        meta: { title: 'Finanzas', icon: 'fas fa-chart-line' }
      },
      {
        path: 'reports',
        name: 'OwnerReports',
        component: OwnerReportsView,
        meta: { title: 'Reportes', icon: 'fas fa-file-alt' }
      },
      {
        path: 'settings',
        name: 'OwnerSettings',
        component: OwnerSettingsView,
        meta: { title: 'Configuración', icon: 'fas fa-cog' }
      }
    ]
  },

  // Public Routes (no authentication required)
  {
    path: '/salons/:id',
    name: 'PublicSalonDetail',
    component: SalonDetailView,
    meta: { requiresAuth: false, hideSidebar: true, title: 'Detalle de Peluquería' }
  },
  {
    path: '/salons/:id/booking',
    name: 'PublicBooking',
    component: BookingView,
    meta: { requiresAuth: false, hideSidebar: true, title: 'Reservar Turno' }
  },

  // Common Routes
  {
    path: '/not-found',
    name: 'NotFound',
    component: NotFoundView,
    meta: { title: 'Página No Encontrada' }
  },
  {
    path: '/server-error',
    name: 'ServerError',
    component: ServerErrorView,
    meta: { title: 'Error del Servidor' }
  },

  // Catch all route - must be last
  {
    path: '/:pathMatch(.*)*',
    redirect: '/not-found'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0 };
    }
  }
});

// Navigation Guards
router.beforeEach(async (to, from, next) => {
  console.log('🔍 ROUTER DEBUG - Starting navigation guard');
  console.log('📍 From:', from.path, '-> To:', to.path);
  console.log('🎯 Route name:', to.name);
  console.log('🔧 Route meta:', to.meta);

  try {
    const store = useStore();
    
    // Debug store state
    console.log('📦 Store state debug:');
    console.log('- Store exists:', !!store);
    console.log('- Store getters:', store ? Object.keys(store.getters) : 'No store');
    
    let isAuthenticated = false;
    let userRole = null;

    try {
      isAuthenticated = store?.getters?.isAuthenticated || false;
      userRole = store?.getters?.userType || null;
    } catch (storeError) {
      console.error('❌ Error accessing store:', storeError);
      console.log('🔄 Proceeding without authentication check');
    }

    console.log('🔐 Auth state:', { isAuthenticated, userRole });

    // Set page title
    if (to.meta.title) {
      document.title = `${to.meta.title} - StyleCut Pro`;
    }

    // Check if route requires authentication
    if (to.meta.requiresAuth) {
      console.log('🚫 Route requires auth');
      
      if (!isAuthenticated) {
        console.log('❌ User not authenticated, redirecting to login');
        next({
          name: 'Login',
          query: { redirect: to.fullPath }
        });
        return;
      }

      // Check role-based access
      if (to.meta.role && to.meta.role !== userRole) {
        console.log(`❌ Role mismatch. Required: ${to.meta.role}, User has: ${userRole}`);
        switch (userRole) {
          case 'client':
            next({ name: 'ClientDashboard' });
            break;
          case 'barber':
            next({ name: 'BarberDashboard' });
            break;
          case 'owner':
            next({ name: 'OwnerDashboard' });
            break;
          default:
            next({ name: 'Login' });
        }
        return;
      }
    }

    // Redirect authenticated users away from auth pages
    if (to.path.startsWith('/auth') && isAuthenticated) {
      console.log('🔄 Authenticated user trying to access auth pages, redirecting');
      switch (userRole) {
        case 'client':
          next({ name: 'ClientDashboard' });
          break;
        case 'barber':
          next({ name: 'BarberDashboard' });
          break;
        case 'owner':
          next({ name: 'OwnerDashboard' });
          break;
        default:
          next({ name: 'Login' });
      }
      return;
    }

    console.log('✅ Navigation allowed, proceeding to:', to.path);
    next();

  } catch (error) {
    console.error('💥 Critical error in navigation guard:', error);
    console.log('🚨 Redirecting to not-found due to error');
    next('/not-found');
  }
});

// Handle navigation errors
router.onError((error) => {
  console.error('🚨 Router error:', error);
  console.error('📋 Error stack:', error.stack);
  
  if (error.message.includes('Cannot resolve')) {
    console.log('🔄 Redirecting to not-found due to resolution error');
    router.push('/not-found');
  }
});

// Global after hook for analytics and logging
router.afterEach((to, from) => {
  console.log('🎉 Successfully navigated to:', to.fullPath);
  console.log('📊 Final route name:', to.name);
  
  // Track page view in analytics service
  if (typeof window !== 'undefined' && window.gtag) {
    window.gtag('config', process.env.VUE_APP_GA_MEASUREMENT_ID, {
      page_path: to.fullPath,
    });
  }
});

export default router;