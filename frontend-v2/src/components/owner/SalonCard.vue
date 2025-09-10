<template>
  <div class="salon-card" :class="salon.status">
    <div class="salon-image">
      <img :src="salon.image" :alt="salon.name" class="salon-img">
      <div class="salon-overlay">
        <span class="status-badge">{{ statusLabel }}</span>
        <div class="action-buttons">
          <button class="btn btn-sm btn-light me-2" @click.stop="$emit('edit', salon)">
            <i class="fas fa-edit"></i>
          </button>
          <button class="btn btn-sm btn-light" @click.stop="$emit('delete', salon.id)">
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>
    </div>

    <div class="salon-content">
      <h5 class="salon-name">{{ salon.name }}</h5>
      <p class="salon-address">
        <i class="fas fa-map-marker-alt me-2"></i>{{ salon.address }}
      </p>
      <p class="salon-phone">
        <i class="fas fa-phone me-2"></i>{{ salon.phone }}
      </p>

      <div class="salon-stats">
        <div class="stat">
          <i class="fas fa-star text-warning"></i>
          <span>{{ salon.rating }}</span>
          <small>({{ salon.totalReviews }})</small>
        </div>
        <div class="stat">
          <i class="fas fa-users"></i>
          <span>{{ salon.employees }}</span>
        </div>
        <div class="stat">
          <i class="fas fa-calendar-alt"></i>
          <span>{{ salon.appointments }}</span>
        </div>
      </div>

      <div class="salon-revenue">
        <div class="revenue-label">Ingresos mensuales</div>
        <div class="revenue-value">${{ salon.revenue.toLocaleString() }}</div>
      </div>

      <div class="salon-schedule">
        <small class="text-muted">
          <i class="fas fa-clock me-1"></i>
          {{ salon.schedule.open }} - {{ salon.schedule.close }} • {{ salon.schedule.days }}
        </small>
      </div>

      <div class="salon-services">
        <span v-for="service in displayedServices" :key="service" class="service-tag">
          {{ service }}
        </span>
        <span v-if="salon.services.length > 3" class="service-tag more">
          +{{ salon.services.length - 3 }} más
        </span>
      </div>

      <div class="salon-actions">
        <button class="btn btn-outline-primary btn-sm" @click="viewDetails">
          <i class="fas fa-eye me-1"></i> Ver detalles
        </button>
        <button class="btn btn-primary btn-sm" @click="manageSalon">
          <i class="fas fa-cog me-1"></i> Gestionar
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: 'SalonCard',
  props: {
    salon: {
      type: Object,
      required: true
    }
  },
  emits: ['edit', 'delete'],
  setup(props) {
    const router = useRouter();
    
    const statusLabel = computed(() => {
      const statusMap = {
        'active': 'Activa',
        'inactive': 'Inactiva',
        'maintenance': 'Mantenimiento'
      };
      return statusMap[props.salon.status] || props.salon.status;
    });
    
    const displayedServices = computed(() => {
      return props.salon.services.slice(0, 3);
    });
    
    const viewDetails = () => {
      router.push(`/owner/salons/${props.salon.id}`);
    };
    
    const manageSalon = () => {
      router.push(`/owner/salons/${props.salon.id}/manage`);
    };
    
    return {
      statusLabel,
      displayedServices,
      viewDetails,
      manageSalon
    };
  }
};
</script>

<style scoped>
.salon-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  height: 100%;
}

.salon-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.salon-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.salon-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.salon-card:hover .salon-img {
  transform: scale(1.05);
}

.salon-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.3), rgba(0, 0, 0, 0.7));
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 15px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.salon-card:hover .salon-overlay {
  opacity: 1;
}

.status-badge {
  background: rgba(255, 255, 255, 0.9);
  color: #2c3e50;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.salon-card.active .status-badge {
  background: rgba(46, 204, 113, 0.9);
  color: white;
}

.salon-card.inactive .status-badge {
  background: rgba(231, 76, 60, 0.9);
  color: white;
}

.salon-card.maintenance .status-badge {
  background: rgba(241, 196, 15, 0.9);
  color: white;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-buttons .btn {
  border-radius: 8px;
  padding: 6px 10px;
}

.salon-content {
  padding: 20px;
}

.salon-name {
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
}

.salon-address,
.salon-phone {
  color: #6c757d;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.salon-address i,
.salon-phone i {
  width: 16px;
}

.salon-stats {
  display: flex;
  justify-content: space-around;
  margin: 15px 0;
  padding: 15px 0;
  border-top: 1px solid #f1f3f4;
  border-bottom: 1px solid #f1f3f4;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat i {
  font-size: 1.2rem;
}

.stat span {
  font-weight: 600;
  color: #2c3e50;
}

.stat small {
  color: #6c757d;
  font-size: 0.8rem;
}

.salon-revenue {
  text-align: center;
  margin: 15px 0;
  padding: 12px;
  background: linear-gradient(135deg, #6f42c1 0%, #d63384 100%);
  color: white;
  border-radius: 12px;
}

.revenue-label {
  font-size: 0.9rem;
  opacity: 0.9;
  margin-bottom: 4px;
}

.revenue-value {
  font-size: 1.5rem;
  font-weight: 700;
}

.salon-schedule {
  text-align: center;
  margin: 12px 0;
}

.salon-services {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin: 15px 0;
  justify-content: center;
}

.service-tag {
  background: rgba(111, 66, 193, 0.1);
  color: #6f42c1;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.service-tag.more {
  background: #e9ecef;
  color: #6c757d;
  font-style: italic;
}

.salon-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.salon-actions .btn {
  flex: 1;
  border-radius: 8px;
  font-size: 0.85rem;
  padding: 8px 12px;
}

@media (max-width: 768px) {
  .salon-content {
    padding: 15px;
  }
  
  .salon-stats {
    padding: 10px 0;
  }
  
  .stat i {
    font-size: 1rem;
  }
  
  .revenue-value {
    font-size: 1.3rem;
  }
  
  .salon-actions {
    flex-direction: column;
    gap: 8px;
  }
}

@media (max-width: 576px) {
  .salon-overlay {
    opacity: 1;
    background: linear-gradient(to bottom, rgba(0, 0, 0, 0.1), rgba(0, 0, 0, 0.5));
  }
  
  .action-buttons .btn {
    padding: 4px 8px;
  }
}
</style>