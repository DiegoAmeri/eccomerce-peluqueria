<template>
  <div class="my-appointments-view">
    <!-- Header -->
    <div class="appointments-header">
      <div class="header-content">
        <h1>Mis Turnos</h1>
        <p class="text-muted">Gestiona tus próximas citas y revisa el historial</p>
      </div>
    </div>

    <!-- Tabs -->
    <div class="appointments-tabs">
      <ul class="nav nav-tabs" id="appointmentsTab" role="tablist">
        <li class="nav-item" role="presentation">
          <button class="nav-link active" id="upcoming-tab" data-bs-toggle="tab" data-bs-target="#upcoming" type="button" role="tab">
            <i class="fas fa-calendar-check me-2"></i>Próximos
          </button>
        </li>
        <li class="nav-item" role="presentation">
          <button class="nav-link" id="pending-tab" data-bs-toggle="tab" data-bs-target="#pending" type="button" role="tab">
            <i class="fas fa-clock me-2"></i>Pendientes
          </button>
        </li>
        <li class="nav-item" role="presentation">
          <button class="nav-link" id="history-tab" data-bs-toggle="tab" data-bs-target="#history" type="button" role="tab">
            <i class="fas fa-history me-2"></i>Historial
          </button>
        </li>
        <li class="nav-item" role="presentation">
          <button class="nav-link" id="cancelled-tab" data-bs-toggle="tab" data-bs-target="#cancelled" type="button" role="tab">
            <i class="fas fa-times-circle me-2"></i>Cancelados
          </button>
        </li>
      </ul>
    </div>

    <!-- Tab Content -->
    <div class="tab-content mt-4" id="appointmentsTabContent">
      <!-- Upcoming Appointments -->
      <div class="tab-pane fade show active" id="upcoming" role="tabpanel" aria-labelledby="upcoming-tab">
        <div v-if="loading" class="text-center py-5">
          <LoadingSpinner message="Cargando tus citas..." />
        </div>

        <div v-else-if="upcomingAppointments.length === 0" class="text-center py-5 empty-state">
          <i class="fas fa-calendar-plus fa-3x text-muted mb-3"></i>
          <h4>No tienes citas próximas</h4>
          <p class="text-muted">Programa una cita para comenzar a gestionar tus turnos</p>
          <router-link to="/salons" class="btn btn-primary mt-2">
            <i class="fas fa-search me-2"></i>Buscar peluquerías
          </router-link>
        </div>

        <div v-else class="appointments-list">
          <div class="row">
            <div class="col-lg-6 mb-4" v-for="appointment in upcomingAppointments" :key="appointment.id">
              <div class="card appointment-card h-100">
                <div class="card-body">
                  <div class="d-flex justify-content-between align-items-start mb-3">
                    <div>
                      <h5 class="card-title mb-1">{{ appointment.salonName }}</h5>
                      <p class="card-text text-muted mb-0">{{ appointment.service }}</p>
                    </div>
                    <span class="badge bg-success">{{ appointment.status }}</span>
                  </div>
                  
                  <div class="appointment-details">
                    <div class="detail-item">
                      <i class="fas fa-calendar-alt text-primary"></i>
                      <span>{{ formatDate(appointment.date) }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="fas fa-clock text-primary"></i>
                      <span>{{ appointment.time }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="fas fa-user text-primary"></i>
                      <span>{{ appointment.stylist }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="fas fa-dollar-sign text-primary"></i>
                      <span>{{ appointment.price }}</span>
                    </div>
                  </div>
                  
                  <div class="appointment-actions mt-3">
                    <button class="btn btn-outline-primary btn-sm me-2" @click="viewAppointmentDetails(appointment)">
                      <i class="fas fa-eye me-1"></i>Ver detalles
                    </button>
                    <button class="btn btn-outline-secondary btn-sm me-2" @click="rescheduleAppointment(appointment)">
                      <i class="fas fa-calendar-alt me-1"></i>Reprogramar
                    </button>
                    <button class="btn btn-outline-danger btn-sm" @click="cancelAppointment(appointment)">
                      <i class="fas fa-times me-1"></i>Cancelar
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Pending Appointments -->
      <div class="tab-pane fade" id="pending" role="tabpanel" aria-labelledby="pending-tab">
        <div v-if="loading" class="text-center py-5">
          <LoadingSpinner message="Cargando tus citas..." />
        </div>

        <div v-else-if="pendingAppointments.length === 0" class="text-center py-5 empty-state">
          <i class="fas fa-check-circle fa-3x text-muted mb-3"></i>
          <h4>No tienes citas pendientes</h4>
          <p class="text-muted">Todas tus citas han sido confirmadas</p>
        </div>

        <div v-else class="appointments-list">
          <div class="row">
            <div class="col-lg-6 mb-4" v-for="appointment in pendingAppointments" :key="appointment.id">
              <div class="card appointment-card h-100">
                <div class="card-body">
                  <div class="d-flex justify-content-between align-items-start mb-3">
                    <div>
                      <h5 class="card-title mb-1">{{ appointment.salonName }}</h5>
                      <p class="card-text text-muted mb-0">{{ appointment.service }}</p>
                    </div>
                    <span class="badge bg-warning text-dark">{{ appointment.status }}</span>
                  </div>
                  
                  <div class="appointment-details">
                    <div class="detail-item">
                      <i class="fas fa-calendar-alt text-primary"></i>
                      <span>{{ formatDate(appointment.date) }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="fas fa-clock text-primary"></i>
                      <span>{{ appointment.time }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="fas fa-user text-primary"></i>
                      <span>{{ appointment.stylist }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="fas fa-dollar-sign text-primary"></i>
                      <span>{{ appointment.price }}</span>
                    </div>
                  </div>
                  
                  <div class="appointment-actions mt-3">
                    <button class="btn btn-outline-primary btn-sm me-2" @click="viewAppointmentDetails(appointment)">
                      <i class="fas fa-eye me-1"></i>Ver detalles
                    </button>
                    <button class="btn btn-outline-secondary btn-sm me-2" @click="rescheduleAppointment(appointment)">
                      <i class="fas fa-calendar-alt me-1"></i>Reprogramar
                    </button>
                    <button class="btn btn-outline-danger btn-sm" @click="cancelAppointment(appointment)">
                      <i class="fas fa-times me-1"></i>Cancelar
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- History Appointments -->
      <div class="tab-pane fade" id="history" role="tabpanel" aria-labelledby="history-tab">
        <div v-if="loading" class="text-center py-5">
          <LoadingSpinner message="Cargando historial..." />
        </div>

        <div v-else-if="historyAppointments.length === 0" class="text-center py-5 empty-state">
          <i class="fas fa-history fa-3x text-muted mb-3"></i>
          <h4>No tienes citas en tu historial</h4>
          <p class="text-muted">Tus citas pasadas aparecerán aquí</p>
        </div>

        <div v-else class="appointments-list">
          <div class="row">
            <div class="col-lg-6 mb-4" v-for="appointment in historyAppointments" :key="appointment.id">
              <div class="card appointment-card h-100">
                <div class="card-body">
                  <div class="d-flex justify-content-between align-items-start mb-3">
                    <div>
                      <h5 class="card-title mb-1">{{ appointment.salonName }}</h5>
                      <p class="card-text text-muted mb-0">{{ appointment.service }}</p>
                    </div>
                    <span class="badge bg-secondary">{{ appointment.status }}</span>
                  </div>
                  
                  <div class="appointment-details">
                    <div class="detail-item">
                      <i class="fas fa-calendar-alt text-primary"></i>
                      <span>{{ formatDate(appointment.date) }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="fas fa-clock text-primary"></i>
                      <span>{{ appointment.time }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="fas fa-user text-primary"></i>
                      <span>{{ appointment.stylist }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="fas fa-dollar-sign text-primary"></i>
                      <span>{{ appointment.price }}</span>
                    </div>
                  </div>
                  
                  <div class="appointment-actions mt-3">
                    <button class="btn btn-outline-primary btn-sm me-2" @click="viewAppointmentDetails(appointment)">
                      <i class="fas fa-eye me-1"></i>Ver detalles
                    </button>
                    <button class="btn btn-outline-success btn-sm" @click="bookAgain(appointment)">
                      <i class="fas fa-calendar-plus me-1"></i>Reservar de nuevo
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Cancelled Appointments -->
      <div class="tab-pane fade" id="cancelled" role="tabpanel" aria-labelledby="cancelled-tab">
        <div v-if="loading" class="text-center py-5">
          <LoadingSpinner message="Cargando citas canceladas..." />
        </div>

        <div v-else-if="cancelledAppointments.length === 0" class="text-center py-5 empty-state">
          <i class="fas fa-ban fa-3x text-muted mb-3"></i>
          <h4>No tienes citas canceladas</h4>
          <p class="text-muted">Tus citas canceladas aparecerán aquí</p>
        </div>

        <div v-else class="appointments-list">
          <div class="row">
            <div class="col-lg-6 mb-4" v-for="appointment in cancelledAppointments" :key="appointment.id">
              <div class="card appointment-card h-100">
                <div class="card-body">
                  <div class="d-flex justify-content-between align-items-start mb-3">
                    <div>
                      <h5 class="card-title mb-1">{{ appointment.salonName }}</h5>
                      <p class="card-text text-muted mb-0">{{ appointment.service }}</p>
                    </div>
                    <span class="badge bg-danger">{{ appointment.status }}</span>
                  </div>
                  
                  <div class="appointment-details">
                    <div class="detail-item">
                      <i class="fas fa-calendar-alt text-primary"></i>
                      <span>{{ formatDate(appointment.date) }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="fas fa-clock text-primary"></i>
                      <span>{{ appointment.time }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="fas fa-user text-primary"></i>
                      <span>{{ appointment.stylist }}</span>
                    </div>
                    <div class="detail-item">
                      <i class="fas fa-dollar-sign text-primary"></i>
                      <span>{{ appointment.price }}</span>
                    </div>
                  </div>
                  
                  <div class="appointment-actions mt-3">
                    <button class="btn btn-outline-primary btn-sm me-2" @click="viewAppointmentDetails(appointment)">
                      <i class="fas fa-eye me-1"></i>Ver detalles
                    </button>
                    <button class="btn btn-outline-success btn-sm" @click="bookAgain(appointment)">
                      <i class="fas fa-calendar-plus me-1"></i>Reservar de nuevo
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Appointment Details Modal -->
    <div class="modal fade" id="appointmentDetailsModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Detalles de la cita</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body" v-if="selectedAppointment">
            <div class="row">
              <div class="col-md-6">
                <h6>Información de la cita</h6>
                <ul class="list-group list-group-flush">
                  <li class="list-group-item d-flex justify-content-between align-items-center">
                    <span class="text-muted">Servicio:</span>
                    <span>{{ selectedAppointment.service }}</span>
                  </li>
                  <li class="list-group-item d-flex justify-content-between align-items-center">
                    <span class="text-muted">Fecha:</span>
                    <span>{{ formatDate(selectedAppointment.date) }}</span>
                  </li>
                  <li class="list-group-item d-flex justify-content-between align-items-center">
                    <span class="text-muted">Hora:</span>
                    <span>{{ selectedAppointment.time }}</span>
                  </li>
                  <li class="list-group-item d-flex justify-content-between align-items-center">
                    <span class="text-muted">Duración:</span>
                    <span>{{ selectedAppointment.duration }} minutos</span>
                  </li>
                  <li class="list-group-item d-flex justify-content-between align-items-center">
                    <span class="text-muted">Precio:</span>
                    <span>{{ selectedAppointment.price }}</span>
                  </li>
                </ul>
              </div>
              <div class="col-md-6">
                <h6>Información del salón</h6>
                <ul class="list-group list-group-flush">
                  <li class="list-group-item d-flex justify-content-between align-items-center">
                    <span class="text-muted">Salón:</span>
                    <span>{{ selectedAppointment.salonName }}</span>
                  </li>
                  <li class="list-group-item d-flex justify-content-between align-items-center">
                    <span class="text-muted">Estilista:</span>
                    <span>{{ selectedAppointment.stylist }}</span>
                  </li>
                  <li class="list-group-item d-flex justify-content-between align-items-center">
                    <span class="text-muted">Dirección:</span>
                    <span>{{ selectedAppointment.salonAddress }}</span>
                  </li>
                  <li class="list-group-item d-flex justify-content-between align-items-center">
                    <span class="text-muted">Teléfono:</span>
                    <span>{{ selectedAppointment.salonPhone }}</span>
                  </li>
                  <li class="list-group-item d-flex justify-content-between align-items-center">
                    <span class="text-muted">Estado:</span>
                    <span class="badge" :class="getStatusBadgeClass(selectedAppointment.status)">{{ selectedAppointment.status }}</span>
                  </li>
                </ul>
              </div>
            </div>
            <div class="mt-4" v-if="selectedAppointment.notes">
              <h6>Notas adicionales</h6>
              <p class="text-muted">{{ selectedAppointment.notes }}</p>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
            <button type="button" class="btn btn-primary" v-if="selectedAppointment && selectedAppointment.status === 'Pendiente'" @click="confirmAppointment(selectedAppointment)">
              <i class="fas fa-check me-1"></i>Confirmar cita
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Success Toast -->
    <NotificationToast 
      :show="showToast" 
      :type="toastType" 
      :title="toastTitle"
      :message="toastMessage"
      @update:show="showToast = false"
    />
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { Modal } from 'bootstrap';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import NotificationToast from '@/components/common/NotificationToast.vue';

export default {
  name: 'MyAppointmentsView',
  components: {
    LoadingSpinner,
    NotificationToast
  },
  setup() {
    const loading = ref(true);
    const showToast = ref(false);
    const toastType = ref('success');
    const toastTitle = ref('');
    const toastMessage = ref('');
    const selectedAppointment = ref(null);
    let appointmentDetailsModal = null;

    // Datos de ejemplo
    const appointments = ref([
      {
        id: 1,
        salonName: "Estilo Urbano",
        service: "Corte de cabello y arreglo de barba",
        date: "2023-12-15",
        time: "15:30",
        duration: 60,
        price: "$45.00",
        stylist: "Carlos Rodríguez",
        status: "Confirmado",
        salonAddress: "Av. Siempre Viva 742, Ciudad",
        salonPhone: "+1 234-567-8901",
        notes: "Por favor llegar 10 minutos antes de la cita"
      },
      {
        id: 2,
        salonName: "Belleza Integral",
        service: "Manicura y pedicura",
        date: "2023-12-18",
        time: "11:00",
        duration: 90,
        price: "$35.00",
        stylist: "María González",
        status: "Pendiente",
        salonAddress: "Boulevard Central 456, Ciudad",
        salonPhone: "+1 234-567-8902",
        notes: "Traer sandalias para después del servicio"
      },
      {
        id: 3,
        salonName: "Color Express",
        service: "Coloración y reflejos",
        date: "2023-11-20",
        time: "14:00",
        duration: 120,
        price: "$85.00",
        stylist: "Laura Martínez",
        status: "Completado",
        salonAddress: "Av. del Sol 321, Ciudad",
        salonPhone: "+1 234-567-8903",
        notes: "Se aplicó tratamiento nutritivo adicional"
      },
      {
        id: 4,
        salonName: "Nails Paradise",
        service: "Uñas acrílicas",
        date: "2023-11-05",
        time: "16:30",
        duration: 75,
        price: "$55.00",
        stylist: "Ana López",
        status: "Completado",
        salonAddress: "Centro Comercial Galerías, Local 12",
        salonPhone: "+1 234-567-8904",
        notes: "Diseño francés con detalles en dorado"
      },
      {
        id: 5,
        salonName: "Glow Estética",
        service: "Limpieza facial premium",
        date: "2023-10-22",
        time: "10:00",
        duration: 60,
        price: "$65.00",
        stylist: "Elena Torres",
        status: "Cancelado",
        salonAddress: "Av. Libertador 1234, Piso 3",
        salonPhone: "+1 234-567-8905",
        notes: "Cancelación por emergencia del estilista"
      }
    ]);

    const upcomingAppointments = computed(() => {
      return appointments.value.filter(apt => 
        apt.status === "Confirmado" || apt.status === "Pendiente"
      );
    });

    const pendingAppointments = computed(() => {
      return appointments.value.filter(apt => apt.status === "Pendiente");
    });

    const historyAppointments = computed(() => {
      return appointments.value.filter(apt => apt.status === "Completado");
    });

    const cancelledAppointments = computed(() => {
      return appointments.value.filter(apt => apt.status === "Cancelado");
    });

    const formatDate = (dateString) => {
      const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
      return new Date(dateString).toLocaleDateString('es-ES', options);
    };

    const getStatusBadgeClass = (status) => {
      switch(status) {
        case 'Confirmado': return 'bg-success';
        case 'Pendiente': return 'bg-warning text-dark';
        case 'Completado': return 'bg-secondary';
        case 'Cancelado': return 'bg-danger';
        default: return 'bg-secondary';
      }
    };

    const viewAppointmentDetails = (appointment) => {
      selectedAppointment.value = appointment;
      if (!appointmentDetailsModal) {
        appointmentDetailsModal = new Modal(document.getElementById('appointmentDetailsModal'));
      }
      appointmentDetailsModal.show();
    };

    const rescheduleAppointment = (appointment) => {
      showToast.value = true;
      toastType.value = 'info';
      toastTitle.value = 'Reprogramar cita';
      toastMessage.value = `Redirigiendo a la página de reprogramación para ${appointment.salonName}`;
    };

    const cancelAppointment = (appointment) => {
      if (confirm(`¿Estás seguro de que deseas cancelar tu cita en ${appointment.salonName}?`)) {
        appointment.status = "Cancelado";
        
        showToast.value = true;
        toastType.value = 'success';
        toastTitle.value = 'Cita cancelada';
        toastMessage.value = `Tu cita en ${appointment.salonName} ha sido cancelada exitosamente.`;
      }
    };

    const confirmAppointment = (appointment) => {
      appointment.status = "Confirmado";
      
      showToast.value = true;
      toastType.value = 'success';
      toastTitle.value = 'Cita confirmada';
      toastMessage.value = `Tu cita en ${appointment.salonName} ha sido confirmada.`;
      
      if (appointmentDetailsModal) {
        appointmentDetailsModal.hide();
      }
    };

    const bookAgain = (appointment) => {
      showToast.value = true;
      toastType.value = 'info';
      toastTitle.value = 'Reservar nuevamente';
      toastMessage.value = `Redirigiendo a la página de reserva de ${appointment.salonName}`;
    };

    onMounted(() => {
      // Simular carga de datos
      setTimeout(() => {
        loading.value = false;
      }, 1000);
    });

    return {
      loading,
      showToast,
      toastType,
      toastTitle,
      toastMessage,
      selectedAppointment,
      upcomingAppointments,
      pendingAppointments,
      historyAppointments,
      cancelledAppointments,
      formatDate,
      getStatusBadgeClass,
      viewAppointmentDetails,
      rescheduleAppointment,
      cancelAppointment,
      confirmAppointment,
      bookAgain
    };
  }
};
</script>

<style scoped>
.appointments-header {
  background-color: #f8f9fa;
  padding: 2rem 0;
  margin-bottom: 2rem;
}

.appointments-tabs {
  border-bottom: 1px solid #dee2e6;
}

.nav-tabs .nav-link {
  border: none;
  border-bottom: 3px solid transparent;
  color: #6c757d;
  font-weight: 500;
  padding: 0.75rem 1rem;
  transition: all 0.2s;
}

.nav-tabs .nav-link:hover {
  border-bottom-color: #dee2e6;
  color: #495057;
}

.nav-tabs .nav-link.active {
  color: #0d6efd;
  border-bottom-color: #0d6efd;
  background-color: transparent;
}

.appointment-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
  transition: transform 0.2s, box-shadow 0.2s;
}

.appointment-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 15px rgba(0,0,0,0.1);
}

.appointment-details {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.detail-item i {
  width: 16px;
}

.appointment-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.empty-state {
  padding: 3rem 1rem;
}

.modal-content {
  border: none;
  border-radius: 12px;
}

.modal-header {
  border-bottom: 1px solid #dee2e6;
}

.modal-footer {
  border-top: 1px solid #dee2e6;
}

.list-group-item {
  border-color: #f8f9fa;
  padding: 0.75rem 0;
}

@media (max-width: 768px) {
  .appointments-header {
    padding: 1.5rem 0;
  }
  
  .nav-tabs .nav-link {
    font-size: 0.9rem;
    padding: 0.5rem;
  }
  
  .appointment-actions {
    flex-direction: column;
  }
  
  .appointment-actions .btn {
    width: 100%;
    margin: 0.25rem 0;
  }
}
</style>