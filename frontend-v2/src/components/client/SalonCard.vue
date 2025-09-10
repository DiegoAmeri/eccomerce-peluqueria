<template>
  <div class="salon-card">
    <div class="salon-image">
      <img :src="salon.image" :alt="salon.name">
      <button class="favorite-btn" @click="$emit('favorite-toggle', salon.id)">
        <i class="fas fa-heart" :class="{ 'fas': salon.isFavorite, 'far': !salon.isFavorite }"></i>
      </button>
      <span class="category-badge">{{ categoryName }}</span>
    </div>
    
    <div class="salon-content">
      <div class="salon-header">
        <h5 class="salon-name">{{ salon.name }}</h5>
        <div class="rating">
          <i class="fas fa-star"></i>
          <span>{{ salon.rating }}</span>
          <small>({{ salon.reviews }})</small>
        </div>
      </div>
      
      <p class="salon-description">{{ salon.description }}</p>
      
      <div class="salon-details">
        <div class="detail-item">
          <i class="fas fa-map-marker-alt"></i>
          <span>{{ salon.distance }} km</span>
        </div>
        <div class="detail-item">
          <i class="fas fa-clock"></i>
          <span>{{ salon.openHour }} - {{ salon.closeHour }}</span>
        </div>
        <div class="detail-item">
          <i class="fas fa-dollar-sign"></i>
          <span>{{ priceRangeText }}</span>
        </div>
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
        <router-link :to="`/salons/${salon.id}`" class="btn btn-outline-primary btn-sm">
          Ver detalles
        </router-link>
        <router-link :to="`/booking/${salon.id}`" class="btn btn-primary btn-sm">
          Reservar
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue';

export default {
  name: 'SalonCard',
  props: {
    salon: {
      type: Object,
      required: true
    }
  },
  emits: ['favorite-toggle'],
  setup(props) {
    const categoryName = computed(() => {
      const categories = {
        'peluqueria': 'Peluquería',
        'barberia': 'Barbería',
        'estetica': 'Estética',
        'unas': 'Uñas'
      };
      return categories[props.salon.category] || props.salon.category;
    });
    
    const priceRangeText = computed(() => {
      return '$'.repeat(props.salon.priceRange);
    });
    
    const displayedServices = computed(() => {
      return props.salon.services.slice(0, 3);
    });
    
    return {
      categoryName,
      priceRangeText,
      displayedServices
    };
  }
};
</script>

<style scoped>
.salon-card {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
  height: 100%;
}

.salon-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

.salon-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.salon-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.salon-card:hover .salon-image img {
  transform: scale(1.05);
}

.favorite-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.favorite-btn:hover {
  background: #ff6b6b;
  color: white;
}

.favorite-btn .fa-heart {
  color: #ff6b6b;
}

.favorite-btn:hover .fa-heart {
  color: white;
}

.category-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(111, 66, 193, 0.9);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.salon-content {
  padding: 20px;
}

.salon-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.salon-name {
  margin: 0;
  font-weight: 600;
  color: #212529;
  flex: 1;
  margin-right: 10px;
}

.rating {
  display: flex;
  align-items: center;
  white-space: nowrap;
}

.rating .fa-star {
  color: #ffc107;
  margin-right: 4px;
}

.rating span {
  font-weight: 600;
  margin-right: 4px;
}

.rating small {
  color: #6c757d;
}

.salon-description {
  color: #6c757d;
  font-size: 0.9rem;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.salon-details {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 15px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.85rem;
  color: #6c757d;
}

.detail-item i {
  width: 14px;
  text-align: center;
}

.salon-services {
  margin-bottom: 15px;
}

.service-tag {
  display: inline-block;
  background: #f8f9fa;
  color: #6c757d;
  padding: 4px 10px;
  border-radius: 15px;
  font-size: 0.75rem;
  margin-right: 6px;
  margin-bottom: 6px;
}

.service-tag.more {
  background: #e9ecef;
  font-weight: 500;
}

.salon-actions {
  display: flex;
  gap: 10px;
}

.salon-actions .btn {
  flex: 1;
  border-radius: 20px;
  font-size: 0.85rem;
  padding: 6px 12px;
}

@media (max-width: 768px) {
  .salon-content {
    padding: 15px;
  }
  
  .salon-header {
    flex-direction: column;
    gap: 8px;
  }
  
  .salon-name {
    margin-right: 0;
  }
  
  .salon-details {
    gap: 8px;
  }
  
  .salon-actions {
    flex-direction: column;
    gap: 8px;
  }
}
</style>