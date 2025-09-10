<template>
  <div class="favorite-salons">
    <div class="card">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h6 class="mb-0">Peluquerías Favoritas</h6>
        <i class="fas fa-heart text-danger"></i>
      </div>
      <div class="card-body">
        <div v-if="loading" class="text-center py-3">
          <LoadingSpinner size="small" />
        </div>
        
        <div v-else-if="favorites.length === 0" class="text-center py-3">
          <i class="fas fa-heart fa-2x text-muted mb-3"></i>
          <p class="text-muted mb-0">No tienes peluquerías favoritas</p>
          <router-link to="/salons" class="btn btn-link btn-sm">
            Explorar peluquerías
          </router-link>
        </div>
        
        <div v-else>
          <div v-for="salon in favorites" :key="salon.id" class="favorite-item">
            <div class="favorite-content">
              <img :src="salon.image" :alt="salon.name" class="favorite-image">
              <div class="favorite-info">
                <h6 class="favorite-name">{{ salon.name }}</h6>
                <div class="favorite-rating">
                  <i class="fas fa-star text-warning"></i>
                  <span>{{ salon.rating }}</span>
                  <small>({{ salon.reviews }})</small>
                </div>
                <p class="favorite-address">{{ salon.address }}</p>
              </div>
            </div>
            <button class="favorite-remove" @click="removeFavorite(salon.id)">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
      </div>
      
      <div class="card-footer" v-if="favorites.length > 0">
        <router-link to="/favorites" class="btn btn-link w-100 text-decoration-none">
          Ver todas las favoritas
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useStore } from 'vuex';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';

export default {
  name: 'FavoriteSalons',
  components: {
    LoadingSpinner
  },
  setup() {
    const store = useStore();
    const loading = ref(true);
    const favorites = ref([]);
    
    const loadFavorites = async () => {
      try {
        // En una app real, haríamos una llamada a la API
        // const response = await api.get('/client/favorites');
        // favorites.value = response.data;
        
        // Datos de ejemplo
        favorites.value = [
          {
            id: 2,
            name: "Estilo Urbano",
            image: "https://images.unsplash.com/photo-1585747861815-0468562f7d4f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
            rating: 4.8,
            reviews: 89,
            address: "Calle Falsa 123"
          },
          {
            id: 5,
            name: "Corte Moderno",
            image: "https://images.unsplash.com/photo-1560066984-138dadb4c035?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
            rating: 4.6,
            reviews: 156,
            address: "Av. Central 789"
          }
        ];
      } catch (error) {
        console.error('Error loading favorites:', error);
      } finally {
        loading.value = false;
      }
    };
    
    const removeFavorite = async (salonId) => {
      try {
        // En una app real, haríamos una llamada a la API
        // await api.delete(`/client/favorites/${salonId}`);
        favorites.value = favorites.value.filter(salon => salon.id !== salonId);
        console.log(`Peluquería ${salonId} eliminada de favoritos`);
      } catch (error) {
        console.error('Error removing favorite:', error);
      }
    };
    
    onMounted(() => {
      loadFavorites();
    });
    
    return {
      loading,
      favorites,
      removeFavorite
    };
  }
};
</script>

<style scoped>
.favorite-salons {
  margin-bottom: 20px;
}

.favorite-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f1f3f4;
}

.favorite-item:last-child {
  border-bottom: none;
}

.favorite-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.favorite-image {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
}

.favorite-info {
  flex: 1;
}

.favorite-name {
  margin: 0;
  font-weight: 600;
  font-size: 0.9rem;
  color: #212529;
}

.favorite-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  margin: 2px 0;
}

.favorite-rating span {
  font-weight: 500;
  font-size: 0.85rem;
}

.favorite-rating small {
  color: #6c757d;
  font-size: 0.8rem;
}

.favorite-address {
  margin: 0;
  color: #6c757d;
  font-size: 0.8rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 150px;
}

.favorite-remove {
  background: none;
  border: none;
  color: #6c757d;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.favorite-remove:hover {
  color: #dc3545;
  background: #f8d7da;
}

.card-footer .btn-link {
  color: #6f42c1;
  font-weight: 500;
}

.card-footer .btn-link:hover {
  color: #5a32a3;
}

@media (max-width: 768px) {
  .favorite-item {
    padding: 10px 0;
  }
  
  .favorite-image {
    width: 40px;
    height: 40px;
  }
  
  .favorite-address {
    max-width: 120px;
  }
}
</style>