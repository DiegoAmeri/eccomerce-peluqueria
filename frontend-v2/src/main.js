import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import './services/api'; // Import para inicializar interceptores

// Importar estilos globales
import './assets/css/main.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import '@fortawesome/fontawesome-free/css/all.min.css';

// Importar componentes globales
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import ModalDialog from '@/components/common/ModalDialog.vue';
import NotificationToast from '@/components/common/NotificationToast.vue';

const app = createApp(App);

// Usar plugins
app.use(store);
app.use(router);

// Registrar componentes globales
app.component('LoadingSpinner', LoadingSpinner);
app.component('ModalDialog', ModalDialog);
app.component('NotificationToast', NotificationToast);

// Directivas globales
app.directive('click-outside', {
  beforeMount(el, binding) {
    el.clickOutsideEvent = function(event) {
      if (!(el === event.target || el.contains(event.target))) {
        binding.value(event);
      }
    };
    document.addEventListener('click', el.clickOutsideEvent);
  },
  unmounted(el) {
    document.removeEventListener('click', el.clickOutsideEvent);
  }
});

// Inicializar la aplicación
router.isReady().then(() => {
  // Inicializar el store
  store.dispatch('initializeApp');
  
  app.mount('#app');
});

// Manejar errores globales
app.config.errorHandler = (error, instance, info) => {
  console.error('Error global:', error);
  console.info('Info:', info);
  store.dispatch('setError', 'Ha ocurrido un error inesperado');
};

// Manejar advertencias
app.config.warnHandler = (msg, instance, trace) => {
  console.warn('Advertencia Vue:', msg);
  console.trace(trace);
};