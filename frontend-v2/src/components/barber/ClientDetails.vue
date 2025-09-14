<template>
  <div class="client-details">
    <div v-if="client" class="details-content">
      <!-- Client Header -->
      <div class="client-header">
        <div class="client-avatar-large">
          <img :src="client.avatar" :alt="client.name">
        </div>
        <div class="client-info-large">
          <h3>{{ client.name }}</h3>
          <div class="client-contact-large">
            <div><i class="fas fa-phone"></i> {{ client.phone }}</div>
            <div><i class="fas fa-envelope"></i> {{ client.email }}</div>
          </div>
          <div class="client-stats">
            <span class="badge bg-primary">{{ client.visits }} visitas</span>
            <span class="badge bg-secondary">Última: {{ formatDate(client.lastVisit) }}</span>
            <span class="badge" :class="statusClass">{{ getStatusText(client.status) }}</span>
          </div>
        </div>
      </div>
      
      <!-- Tabs -->
      <div class="tabs">
        <ul class="nav nav-tabs">
          <li class="nav-item">
            <button 
              class="nav-link" 
              :class="{ active: activeTab === 'history' }"
              @click="activeTab = 'history'"
            >
              Historial
            </button>
          </li>
          <li class="nav-item">
            <button 
              class="nav-link" 
              :class="{ active: activeTab === 'notes' }"
              @click="activeTab = 'notes'"
            >
              Notas
            </button>
          </li>
          <li class="nav-item">
            <button 
              class="nav-link" 
              :class="{ active: activeTab === 'preferences' }"
              @click="activeTab = 'preferences'"
            >
              Preferencias
            </button>
          </li>
        </ul>
        
        <!-- Tab Content -->
        <div class="tab-content">
          <!-- History Tab -->
          <div v-if="activeTab === 'history'" class="tab-pane fade show active">
            <div class="appointment-history">
              <div v-if="appointments.length === 0" class="text-center py-4">
                <p class="text-muted">No hay historial de turnos</p>
              </div>
              
              <div v-else class="appointment-list">
                <div 
                  v-for="appointment in appointments" 
                  :key="appointment.id"
                  class="appointment-item"
                >
                  <div class="appointment-date">
                    <div class="date">{{ formatAppointmentDate(appointment.date) }}</div>
                    <div class="time">{{ appointment.time }}</div>
                  </div>
                  <div class="appointment-details">
                    <div class="service">{{ appointment.service }}</div>
                    <div class="staff">{{ appointment.staff }}</div>
                  </div>
                  <div class="appointment-price">${{ appointment.price }}</div>
                  <div class="appointment-status">
                    <span class="badge" :class="appointment.statusClass">
                      {{ appointment.status }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Notes Tab -->
          <div v-if="activeTab === 'notes'" class="tab-pane fade show active">
            <div class="notes-section">
              <div class="current-notes">
                <p>{{ client.notes || 'No hay notas para este cliente.' }}</p>
              </div>
              <div class="add-note">
                <textarea 
                  v-model="newNote" 
                  class="form-control" 
                  rows="3" 
                  placeholder="Agregar nueva nota..."
                ></textarea>
                <button class="btn btn-primary mt-2">Guardar Nota</button>
              </div>
            </div>
          </div>
          
          <!-- Preferences Tab -->
          <div v-if="activeTab === 'preferences'" class="tab-pane fade show active">
            <div class="preferences-section">
              <div class="preference-item">
                <label class="form-label">Servicio Preferido</label>
                <p>Corte de Cabello</p>
              </div>
              <div class="preference-item">
                <label class="form-label">Estilista Preferido</label>
                <p>Juan Pérez</p>
              </div>
              <div class="preference-item">
                <label class="form-label">Horario Preferido</label>
                <p>Viernes por la tarde</p>
              </div>
              <div class="preference-item">
                <label class="form-label">Productos Usados</label>
                <div class="product-tags">
                  <span class="badge bg-light text-dark">Gel Fijador</span>
                  <span class="badge bg-light text-dark">Shampoo Hidratante</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Actions -->
      <div class="details-actions">
        <button class="btn btn-outline-secondary" @click="$emit('close')">
          Cerrar
        </button>
        <button class="btn btn-primary" @click="$emit('book', client)">
          <i class="fas fa-calendar-plus me-2"></i> Agendar Turno
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue';

export default {
  name: 'ClientDetails',
  props: {
    client: {
      type: Object,
      required: true
    }
  },
  emits: ['close', 'book'],
  setup(props) {
    const activeTab = ref('history');
    const newNote = ref('');
    
    const statusClass = computed(() => {
      const statusMap = {
        active: 'bg-success',
        new: 'bg-info',
        inactive: 'bg-secondary',
        regular: 'bg-warning'
      };
      return statusMap[props.client.status] || 'bg-secondary';
    });
    
    const appointments = ref([
      {
        id: 1,
        date: '2023-11-20',
        time: '10:00',
        service: 'Corte de Cabello',
        staff: 'Juan Pérez',
        price: 500,
        status: 'Completado',
        statusClass: 'bg-success'
      },
      {
        id: 2,
        date: '2023-10-15',
        time: '11:30',
        service: 'Corte + Barba',
        staff: 'Juan Pérez',
        price: 700,
        status: 'Completado',
        statusClass: 'bg-success'
      },
      {
        id: 3,
        date: '2023-09-08',
        time: '16:00',
        service: 'Corte de Cabello',
        staff: 'Carlos López',
        price: 500,
        status: 'Completado',
        statusClass: 'bg-success'
      }
    ]);
    
    const formatDate = (dateString) => {
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES');
    };
    
    const formatAppointmentDate = (dateString) => {
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES', { 
        weekday: 'short', 
        day: 'numeric', 
        month: 'short' 
      });
    };
    
    const getStatusText = (status) => {
      const statusMap = {
        active: 'Activo',
        new: 'Nuevo',
        inactive: 'Inactivo',
        regular: 'Regular'
      };
      return statusMap[status] || status;
    };
    
    return {
      activeTab,
      newNote,
      statusClass,
      appointments,
      formatDate,
      formatAppointmentDate,
      getStatusText
    };
  }
};
</script>

<style scoped>
.client-details {
  padding: 10px 0;
}

.client-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e9ecef;
}

.client-avatar-large {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.client-avatar-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.client-info-large h3 {
  margin-bottom: 8px;
  color: #2c3e50;
}

.client-contact-large {
  display: flex;
  gap: 15px;
  margin-bottom: 12px;
  color: #6c757d;
}

.client-contact-large i {
  margin-right: 5px;
}

.client-stats {
  display: flex;
  gap: 8px;
}

.tabs {
  margin-bottom: 20px;
}

.nav-tabs .nav-link {
  border: none;
  border-bottom: 2px solid transparent;
  color: #6c757d;
  padding: 12px 20px;
  transition: all 0.3s ease;
}

.nav-tabs .nav-link.active {
  border-bottom-color: #6f42c1;
  color: #6f42c1;
  background: none;
}

.tab-content {
  padding: 20px 0;
}

.appointment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.appointment-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.appointment-date {
  flex: 0 0 100px;
  text-align: center;
}

.appointment-date .date {
  font-weight: 600;
  color: #2c3e50;
}

.appointment-date .time {
  font-size: 0.9rem;
  color: #6c757d;
}

.appointment-details {
  flex: 1;
  padding: 0 15px;
}

.appointment-details .service {
  font-weight: 500;
  color: #2c3e50;
}

.appointment-details .staff {
  font-size: 0.9rem;
  color: #6c757d;
}

.appointment-price {
  font-weight: 600;
  color: #198754;
  margin: 0 15px;
}

.notes-section,
.preferences-section {
  padding: 10px 0;
}

.preference-item {
  margin-bottom: 20px;
}

.preference-item label {
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 5px;
}

.product-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.details-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

@media (max-width: 768px) {
  .client-header {
    flex-direction: column;
    text-align: center;
  }
  
  .client-contact-large {
    flex-direction: column;
    gap: 5px;
  }
  
  .client-stats {
    justify-content: center;
  }
  
  .appointment-item {
    flex-direction: column;
    text-align: center;
    gap: 10px;
  }
  
  .appointment-date {
    flex: none;
  }
  
  .appointment-details {
    padding: 0;
  }
}
</style>