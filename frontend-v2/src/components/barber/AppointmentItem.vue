<template>
  <div class="d-flex align-items-center p-3 border-bottom">
    <div class="flex-shrink-0">
      <img :src="appointment.clientImage" class="client-avatar">
    </div>
    <div class="flex-grow-1 ms-3">
      <h6 class="mb-0">{{ appointment.clientName }}</h6>
      <p class="mb-0 text-muted">{{ appointment.service }} - {{ appointment.duration }} min</p>
    </div>
    <div class="flex-shrink-0 text-end">
      <div class="fw-bold">{{ appointment.time }}</div>
      <div class="btn-group">
        <button type="button" class="btn btn-sm dropdown-toggle" data-bs-toggle="dropdown" 
                :class="statusClass">
          {{ appointment.statusText }}
        </button>
        <ul class="dropdown-menu">
          <li><a class="dropdown-item" href="#" @click="changeStatus('confirmed')">Confirmado</a></li>
          <li><a class="dropdown-item" href="#" @click="changeStatus('completed')">Completado</a></li>
          <li><a class="dropdown-item" href="#" @click="changeStatus('cancelled')">Cancelado</a></li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue';

export default {
  name: 'AppointmentItem',
  props: {
    appointment: {
      type: Object,
      required: true
    }
  },
  emits: ['status-change'],
  setup(props, { emit }) {
    const statusClass = computed(() => {
      switch (props.appointment.status) {
        case 'confirmed': return 'btn-success';
        case 'completed': return 'btn-primary';
        case 'cancelled': return 'btn-danger';
        default: return 'btn-warning';
      }
    });
    
    const changeStatus = (newStatus) => {
      emit('status-change', props.appointment.id, newStatus);
    };
    
    return {
      statusClass,
      changeStatus
    };
  }
};
</script>

<style scoped>
.client-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  object-fit: cover;
}

.btn-group .dropdown-toggle {
  border-radius: 50px;
  padding: 0.25rem 0.75rem;
  font-size: 0.75rem;
}
</style>