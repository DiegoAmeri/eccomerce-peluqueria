<template>
  <div class="appointments-view">
    <div class="header-section">
      <h1>Mis Turnos</h1>
      <p>Gestiona todas tus reservas y citas programadas</p>
    </div>

    <div class="filters-tabs">
      <ul class="nav nav-tabs">
        <li class="nav-item" v-for="tab in tabs" :key="tab.value">
          <button 
            class="nav-link" 
            :class="{ active: activeTab === tab.value }"
            @click="activeTab = tab.value"
          >
            {{ tab.label }} ({{ getAppointmentCount(tab.value) }})
          </button>
        </li>
      </ul>
    </div>

    <div class="appointments-list">
      <div v-if="loading" class="text-center py-5">
        <LoadingSpinner message="Cargando tus turnos..." />
      </div>

      <div v-else-if="filteredAppointments.length === 0" class="text-center py-5">
        <i class="fas fa-calendar-times fa-3x text-muted mb-3"></i>
        <h5>No tienes turnos {{ getTabLabel(activeTab).toLowerCase() }}</h5>
        <p class="text-muted" v-if="activeTab === 'upcoming'">
          ¡Encuentra una peluquería y agenda tu primer turno!
        </p>
        <button 
          class="btn btn-primary" 
          v-if="activeTab === 'upcoming'"
          @click="$router.push('/salons')"
        >
          <i class="fas fa-store me-2"></i> Buscar Peluquerías
        </button>
      </div>

      <div v-else>
        <AppointmentCard 
          v-for="appointment in filteredAppointments" 
          :key="appointment.id" 
          :appointment="appointment"
          :show-actions="true"
          @cancel="handleCancelAppointment"
          @reschedule="handleRescheduleAppointment"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import AppointmentCard from '@/components/client/AppointmentCard.vue';

export default {
  name: 'MyAppointmentsView',
  components: {
    LoadingSpinner,
    AppointmentCard
  },
  setup() {
    const store = useStore();
    const loading = ref(true);
    const activeTab = ref('upcoming');

    const tabs = [
      { value: 'upcoming', label: 'Próximos' },
      { value: 'past', label: 'Pasados' },
      { value: 'cancelled', label: 'Cancelados' }
    ];

    const appointments = ref([
      {
        id: 1,
        salonName: "Estilo Urbano",
        salonImage: "https://images.unsplash.com/photo-1585747861815-0468562f7d4f?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80",
        service: "Corte de Cabello",
        employee: "Carlos Rodríguez",
        date: new Date(Date.now() + 86400000), // Mañana
        time: "10:00",
        duration: 30,
        price: 500,
        status: "confirmed",
        statusText: "Confirmado"
      },
      {
        id: 2,
        salonName: "Corte Perfecto",
        salonImage: "https://images.unsplash.com/photo-1562322140-8baeececf3df?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80",
        service: "Barba",
        employee: "Ana Martínez",
        date: new Date(Date.now() + 172800000), // Pasado mañana
        time: "14:30",
        duration: 20,
        price: 300,
        status: "pending",
        statusText: "Pendiente"
      },
      {
        id: 3,
        salonName: "Belleza Integral",
        salonImage: "https://images.unsplash.com/photo-1599458254928-4b2f391f2836?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80",
        service: "Manicura",
        employee: "Miguel Ángel",
        date: new Date(Date.now() - 86400000), // Ayer
        time: "11:00",
        duration: 45,
        price: 400,
        status: "completed",
        statusText: "Completado"
      }
    ]);

    const filteredAppointments = computed(() => {
      const now = new Date();
      return appointments.value.filter(appointment => {
        const appointmentDate = new Date(appointment.date);
        
        switch (activeTab.value) {
          case 'upcoming':
            return appointmentDate >= now && appointment.status !== 'cancelled';
          case 'past':
            return appointmentDate < now && appointment.status !== 'cancelled';
          case 'cancelled':
            return appointment.status === 'cancelled';
          default:
            return true;
        }
      });
    });

    const getAppointmentCount = (tab) => {
      const now = new Date();
      return appointments.value.filter(appointment => {
        const appointmentDate = new Date(appointment.date);
        
        switch (tab) {
          case 'upcoming':
            return appointmentDate >= now && appointment.status !== 'cancelled';
          case 'past':
            return appointmentDate < now && appointment.status !== 'cancelled';
          case 'cancelled':
            return appointment.status === 'cancelled';
          default:
            return true;
        }
      }).length;
    };

    const getTabLabel = (tabValue) => {
      const tab = tabs.find(t => t.value === tabValue);
      return tab ? tab.label : '';
    };

    const loadAppointments = async () => {
      try {
        loading.value = true;
        // Simular carga de datos
        await new Promise(resolve => setTimeout(resolve, 800));
      } catch (error) {
        console.error('Error loading appointments:', error);
      } finally {
        loading.value = false;
      }
    };

    const handleCancelAppointment = async (appointmentId) => {
      if (confirm('¿Estás seguro de que quieres cancelar este turno?')) {
        try {
          // Lógica para cancelar turno
          const appointment = appointments.value.find(a => a.id === appointmentId);
          if (appointment) {
            appointment.status = 'cancelled';
            appointment.statusText = 'Cancelado';
          }
        } catch (error) {
          console.error('Error cancelling appointment:', error);
          alert('Error al cancelar el turno');
        }
      }
    };

    const handleRescheduleAppointment = (appointmentId) => {
      const appointment = appointments.value.find(a => a.id === appointmentId);
      if (appointment) {
        this.$router.push({
          path: `/salons/${appointment.salonId}/booking`,
          query: { reschedule: appointmentId }
        });
      }
    };

    onMounted(() => {
      loadAppointments();
    });

    return {
      loading,
      activeTab,
      tabs,
      filteredAppointments,
      getAppointmentCount,
      getTabLabel,
      handleCancelAppointment,
      handleRescheduleAppointment
    };
  }
};
</script>

<style scoped>
.appointments-view {
  padding: 20px;
}

.header-section {
  margin-bottom: 30px;
}

.header-section h1 {
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 10px;
}

.header-section p {
  color: #6c757d;
  font-size: 1.1rem;
}

.filters-tabs {
  margin-bottom: 25px;
}

.nav-tabs {
  border-bottom: 2px solid #e9ecef;
}

.nav-link {
  color: #6c757d;
  font-weight: 500;
  border: none;
  padding: 12px 20px;
  transition: all 0.3s ease;
}

.nav-link:hover {
  color: #6f42c1;
  background-color: rgba(111, 66, 193, 0.05);
}

.nav-link.active {
  color: #6f42c1;
  background-color: transparent;
  border-bottom: 3px solid #6f42c1;
}

.appointments-list {
  min-height: 400px;
}

@media (max-width: 768px) {
  .appointments-view {
    padding: 15px;
  }
  
  .nav-link {
    padding: 10px 15px;
    font-size: 0.9rem;
  }
}
</style>