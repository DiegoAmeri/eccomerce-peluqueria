<template>
  <div class="p-2 border-bottom">
    <div class="d-flex justify-content-between">
      <div class="fw-bold">{{ review.clientName }}</div>
      <div class="text-warning">
        <i v-for="star in 5" :key="star" 
           :class="star <= review.rating ? 'fas fa-star' : star - 0.5 <= review.rating ? 'fas fa-star-half-alt' : 'far fa-star'"></i>
      </div>
    </div>
    <p class="mb-0 mt-1">{{ review.comment }}</p>
    <small class="text-muted">{{ formatDate(review.date) }}</small>
  </div>
</template>

<script>
import { computed } from 'vue';

export default {
  name: 'ReviewItem',
  props: {
    review: {
      type: Object,
      required: true
    }
  },
  setup(props) {
    const formatDate = (date) => {
      return new Date(date).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    };
    
    return {
      formatDate
    };
  }
};
</script>