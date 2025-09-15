<template>
  <div class="appointment-card" :class="status">
    <div class="appointment-header">
      <div class="salon-info">
        <img :src="appointment.salonImage" :alt="appointment.salonName" class="salon-image">
        <div>
          <h6 class="salon-name">{{ appointment.salonName }}</h6>
          <p class="appointment-date">{{ formatDate(appointment.date) }} a las {{ appointment.time }}</p>
        </div>
      </div>
      <span class="status-badge">{{ appointment.statusText }}</span>
    </div>
    
    <div class="appointment-body">
      <div class="service-info">
        <i class="fas fa-scissors"></i>
        <div>
          <p class="service-name">{{ appointment.service }}</p>
          <p class="service-duration">{{ appointment.duration }} minutos</p>
        </div>
      </div>
      
      <div class="professional-info" v-if="appointment.barberName">
        <i class="fas fa-user"></i>
        <div>
          <p class="professional-name">Con {{ appointment.barberName }}</p>
          <p class="professional-specialty">{{ appointment.barberSpecialty }}</p>
        </div>
      </div>
    </div>
    
    <div class="appointment-footer">
      <div class="price" v-if="appointment.price">
        <span class="amount">${{ appointment.price }}</span>
      </div>
      <div class="actions">
        <button class="btn btn-outline-primary btn-sm" @click="viewDetails">
          <i class="fas fa-eye me-1"></i> Ver
        </button>
        <button class="btn btn-primary btn-sm" v-if="appointment.status === 'pending'" @click="confirmAppointment">
          <i class="fas fa-check me-1"></i> Confirmar
        </button>
        <button class="btn btn-outline-danger btn-sm" v-if="canCancel" @click="cancelAppointment">
          <i class="fas fa-times me-1"></i> Cancelar
        </button>
        <button class="btn btn-outline-secondary btn-sm" v-if="appointment.status === 'completed'" @click="bookAgain">
          <i class="fas fa-calendar-plus me-1"></i> Reservar again
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue';

export default {
  name: 'AppointmentCard',
  props: {
    appointment: {
      type: Object,
      required: true
    }
  },
  emits: ['view-details', 'confirm', 'cancel', 'book-again'],
  setup(props, { emit }) {
    const formatDate = (dateString) => {
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    };

    const status = computed(() => props.appointment.status);

    const canCancel = computed(() => {
      return ['pending', 'confirmed'].includes(props.appointment.status);
    });

    const viewDetails = () => {
      emit('view-details', props.appointment.id);
    };

    const confirmAppointment = () => {
      emit('confirm', props.appointment.id);
    };

    const cancelAppointment = () => {
      emit('cancel', props.appointment.id);
    };

    const bookAgain = () => {
      emit('book-again', props.appointment.id);
    };

    return {
      formatDate,
      canCancel,
      viewDetails,
      confirmAppointment,
      cancelAppointment,
      bookAgain,
      status
    };
  }
};
</script>

<style scoped>
.appointment-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 15px;
  border-left: 4px solid #6f42c1;
  transition: all 0.3s ease;
}

.appointment-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.appointment-card.completed {
  border-left-color: #198754;
}

.appointment-card.cancelled {
  border-left-color: #dc3545;
}

.appointment-card.pending {
  border-left-color: #ffc107;
}

.appointment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.salon-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  flex: 1;
}

.salon-image {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
}

.salon-name {
  margin: 0;
  font-weight: 600;
  color: #212529;
}

.appointment-date {
  margin: 4px 0 0 0;
  color: #6c757d;
  font-size: 0.9rem;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.appointment-card.pending .status-badge {
  background: #fff3cd;
  color: #856404;
}

.appointment-card.confirmed .status-badge {
  background: #d1e7dd;
  color: #0f5132;
}

.appointment-card.completed .status-badge {
  background: #cfe2ff;
  color: #084298;
}

.appointment-card.cancelled .status-badge {
  background: #f8d7da;
  color: #842029;
}

.appointment-body {
  margin-bottom: 15px;
}

.service-info,
.professional-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.service-info i,
.professional-info i {
  width: 20px;
  color: #6f42c1;
  text-align: center;
}

.service-name,
.professional-name {
  margin: 0;
  font-weight: 500;
  color: #212529;
}

.service-duration,
.professional-specialty {
  margin: 2px 0 0 0;
  color: #6c757d;
  font-size: 0.85rem;
}

.appointment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.amount {
  font-weight: 600;
  color: #198754;
  font-size: 1.1rem;
}

.actions {
  display: flex;
  gap: 8px;
}

.actions .btn {
  border-radius: 20px;
  font-size: 0.8rem;
  padding: 4px 12px;
}

@media (max-width: 768px) {
  .appointment-card {
    padding: 15px;
  }
  
  .appointment-header {
    flex-direction: column;
    gap: 10px;
  }
  
  .appointment-footer {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .actions {
    justify-content: center;
  }
}
</style>