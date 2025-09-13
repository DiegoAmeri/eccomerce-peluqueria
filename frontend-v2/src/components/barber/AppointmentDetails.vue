<template>
  <div class="appointment-details">
    <div v-if="loading" class="text-center py-4">
      <LoadingSpinner message="Cargando detalles..." />
    </div>

    <div v-else class="details-content">
      <!-- Header -->
      <div class="details-header">
        <div class="status-badge" :class="appointment.status">
          {{ appointment.statusText }}
        </div>
        <h4 class="appointment-time">{{ appointment.time }} - {{ appointment.duration }}min</h4>
        <p class="appointment-date">{{ formatDate(appointment.date) }}</p>
      </div>

      <!-- Client Information -->
      <div class="details-section">
        <h6 class="section-title">Información del Cliente</h6>
        <div class="client-info">
          <img :src="appointment.clientAvatar" :alt="appointment.clientName" class="client-avatar">
          <div class="client-details">
            <h5>{{ appointment.clientName }}</h5>
            <p class="client-contact">
              <i class="fas fa-phone me-2"></i>{{ appointment.clientPhone }}
            </p>
            <p class="client-contact" v-if="appointment.clientEmail">
              <i class="fas fa-envelope me-2"></i>{{ appointment.clientEmail }}
            </p>
          </div>
        </div>
      </div>

      <!-- Service Information -->
      <div class="details-section">
        <h6 class="section-title">Información del Servicio</h6>
        <div class="service-info">
          <div class="service-name">{{ appointment.service }}</div>
          <div class="service-price">${{ appointment.price }}</div>
        </div>
        <div class="service-meta">
          <span class="meta-item">
            <i class="fas fa-clock me-2"></i>
            {{ appointment.duration }} minutos
          </span>
          <span class="meta-item">
            <i class="fas fa-calendar me-2"></i>
            {{ formatDate(appointment.date) }}
          </span>
        </div>
      </div>

      <!-- Notes -->
      <div class="details-section" v-if="appointment.notes">
        <h6 class="section-title">Notas</h6>
        <div class="notes-content">
          {{ appointment.notes }}
        </div>
      </div>

      <!-- Actions -->
      <div class="details-actions">
        <button 
          class="btn btn-primary"
          @click="$emit('edit')"
          v-if="appointment.status !== 'completed' && appointment.status !== 'cancelled'"
        >
          <i class="fas fa-edit me-2"></i> Editar Turno
        </button>
        
        <button 
          class="btn"
          :class="appointment.status === 'confirmed' ? 'btn-success' : 'btn-warning'"
          @click="handleStatusChange(appointment.status === 'confirmed' ? 'completed' : 'confirmed')"
          v-if="appointment.status !== 'cancelled'"
        >
          <i :class="appointment.status === 'confirmed' ? 'fas fa-check' : 'fas fa-check-double'" class="me-2"></i>
          {{ appointment.status === 'confirmed' ? 'Marcar como Completado' : 'Confirmar Turno' }}
        </button>
        
        <button 
          class="btn btn-danger"
          @click="handleStatusChange('cancelled')"
          v-if="appointment.status !== 'completed' && appointment.status !== 'cancelled'"
        >
          <i class="fas fa-times me-2"></i> Cancelar Turno
        </button>
        
        <button class="btn btn-outline-secondary" @click="$emit('close')">
          <i class="fas fa-times me-2"></i> Cerrar
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, watch } from 'vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';

export default {
  name: 'AppointmentDetails',
  components: {
    LoadingSpinner
  },
  props: {
    appointment: {
      type: Object,
      required: true
    }
  },
  emits: ['edit', 'status-change', 'close'],
  setup(props, { emit }) {
    const loading = ref(false);
    
    const formatDate = (dateString) => {
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    };
    
    const handleStatusChange = (newStatus) => {
      if (newStatus === 'cancelled') {
        if (!confirm('¿Estás seguro de que quieres cancelar este turno?')) {
          return;
        }
      }
      
      emit('status-change', newStatus);
    };
    
    watch(() => props.appointment, () => {
      // Simular carga de datos detallados
      loading.value = true;
      setTimeout(() => {
        loading.value = false;
      }, 500);
    }, { immediate: true });
    
    return {
      loading,
      formatDate,
      handleStatusChange
    };
  }
};
</script>

<style scoped>
.appointment-details {
  padding: 20px;
}

.details-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e9ecef;
}

.status-badge {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.9rem;
  margin-bottom: 15px;
}

.status-badge.pending {
  background: #fff3cd;
  color: #856404;
}

.status-badge.confirmed {
  background: #d1e7dd;
  color: #0f5132;
}

.status-badge.completed {
  background: #cfe2ff;
  color: #084298;
}

.status-badge.cancelled {
  background: #f8d7da;
  color: #842029;
}

.appointment-time {
  color: #2c3e50;
  margin-bottom: 5px;
}

.appointment-date {
  color: #6c757d;
  margin: 0;
}

.details-section {
  margin-bottom: 25px;
}

.section-title {
  color: #6f42c1;
  font-weight: 600;
  margin-bottom: 15px;
  padding-bottom: 8px;
  border-bottom: 2px solid #f1f3f4;
}

.client-info {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 12px;
}

.client-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.client-details h5 {
  margin: 0 0 8px 0;
  color: #2c3e50;
}

.client-contact {
  margin: 4px 0;
  color: #6c757d;
  font-size: 0.9rem;
}

.service-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 15px;
}

.service-name {
  font-weight: 600;
  color: #2c3e50;
  font-size: 1.1rem;
}

.service-price {
  font-weight: 700;
  color: #198754;
  font-size: 1.2rem;
}

.service-meta {
  display: flex;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  color: #6c757d;
  font-size: 0.9rem;
}

.notes-content {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 12px;
  color: #495057;
  line-height: 1.5;
}

.details-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

@media (max-width: 768px) {
  .appointment-details {
    padding: 15px;
  }
  
  .client-info {
    flex-direction: column;
    text-align: center;
  }
  
  .service-info {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .service-meta {
    flex-direction: column;
    gap: 10px;
  }
  
  .details-actions {
    gap: 10px;
  }
  
  .details-actions .btn {
    width: 100%;
  }
}
</style>