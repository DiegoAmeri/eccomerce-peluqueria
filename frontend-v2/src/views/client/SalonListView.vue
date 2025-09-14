<template>
  <div class="salon-list-view">
    <!-- Header -->
    <div class="salon-header">
      <div class="header-content">
        <h1>Peluquerías</h1>
        <p class="text-muted">Descubre las mejores peluquerías cerca de ti</p>
      </div>
    </div>

    <!-- Filters and Search -->
    <div class="filters-section">
      <div class="row">
        <div class="col-md-8">
          <div class="input-group mb-3">
            <span class="input-group-text bg-transparent">
              <i class="fas fa-search"></i>
            </span>
            <input type="text" class="form-control" placeholder="Buscar peluquería..." v-model="searchQuery">
            <button class="btn btn-outline-secondary" type="button" @click="clearSearch">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
        <div class="col-md-4">
          <div class="d-flex gap-2">
            <div class="dropdown">
              <button class="btn btn-outline-primary dropdown-toggle w-100" type="button" data-bs-toggle="dropdown">
                <i class="fas fa-filter me-2"></i>
                {{ selectedCategory || 'Todas las categorías' }}
              </button>
              <ul class="dropdown-menu">
                <li><a class="dropdown-item" href="#" @click="selectCategory('')">Todas las categorías</a></li>
                <li><hr class="dropdown-divider"></li>
                <li v-for="category in categories" :key="category">
                  <a class="dropdown-item" href="#" @click="selectCategory(category)">
                    {{ getCategoryName(category) }}
                  </a>
                </li>
              </ul>
            </div>
            <div class="dropdown">
              <button class="btn btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown">
                <i class="fas fa-sort"></i>
              </button>
              <ul class="dropdown-menu">
                <li><a class="dropdown-item" href="#" @click="sortBy('rating')">Mejor valoradas</a></li>
                <li><a class="dropdown-item" href="#" @click="sortBy('name')">Nombre A-Z</a></li>
                <li><a class="dropdown-item" href="#" @click="sortBy('distance')">Más cercanas</a></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Salon List -->
    <div class="salon-list-content">
      <div v-if="loading" class="text-center py-5">
        <LoadingSpinner message="Cargando peluquerías..." />
      </div>

      <div v-else-if="filteredSalons.length === 0" class="text-center py-5">
        <i class="fas fa-store-slash fa-3x text-muted mb-3"></i>
        <h4>No se encontraron peluquerías</h4>
        <p class="text-muted">Intenta con otros filtros o términos de búsqueda</p>
        <button class="btn btn-primary mt-2" @click="clearFilters">
          <i class="fas fa-refresh me-2"></i>Limpiar filtros
        </button>
      </div>

      <div v-else class="row">
        <div class="col-lg-4 col-md-6 mb-4" v-for="salon in filteredSalons" :key="salon.id">
          <div class="card h-100 salon-card shadow-sm">
            <div class="salon-image-container">
              <img :src="salon.image" class="card-img-top" :alt="salon.name">
              <button class="favorite-btn" @click="toggleFavorite(salon)">
                <i class="fas" :class="salon.isFavorite ? 'fa-heart text-danger' : 'fa-heart'"></i>
              </button>
              <div class="salon-category-badge">
                {{ getCategoryName(salon.category) }}
              </div>
            </div>
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <h5 class="card-title">{{ salon.name }}</h5>
                <div class="rating-badge">
                  <i class="fas fa-star text-warning"></i>
                  <span>{{ salon.rating }}</span>
                </div>
              </div>
              <p class="card-text text-muted">{{ salon.description }}</p>
              
              <div class="d-flex align-items-center text-muted mb-3">
                <i class="fas fa-map-marker-alt me-2"></i>
                <small>{{ salon.address }}</small>
              </div>
              
              <div class="d-flex align-items-center text-muted mb-3">
                <i class="fas fa-clock me-2"></i>
                <small>{{ salon.openTime }} - {{ salon.closeTime }}</small>
              </div>
              
              <div class="salon-services mb-3">
                <span class="badge bg-light text-dark me-1 mb-1" v-for="service in salon.services.slice(0, 3)" :key="service">
                  {{ service }}
                </span>
                <span v-if="salon.services.length > 3" class="badge bg-light text-dark">
                  +{{ salon.services.length - 3 }}
                </span>
              </div>
            </div>
            <div class="card-footer bg-white border-0 pt-0">
              <div class="d-flex gap-2">
                <router-link :to="`/salons/${salon.id}`" class="btn btn-outline-primary flex-fill">
                  <i class="fas fa-info-circle me-2"></i>Detalles
                </router-link>
                <router-link :to="`/salons/${salon.id}/booking`" class="btn btn-primary flex-fill">
                  <i class="fas fa-calendar-plus me-2"></i>Reservar
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Load More Button -->
      <div v-if="!loading && filteredSalons.length > 0 && hasMore" class="text-center mt-5">
        <button class="btn btn-outline-primary" @click="loadMore" :disabled="loadingMore">
          <span v-if="loadingMore" class="spinner-border spinner-border-sm me-2"></span>
          {{ loadingMore ? 'Cargando...' : 'Cargar más peluquerías' }}
        </button>
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
import { useStore } from 'vuex';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import NotificationToast from '@/components/common/NotificationToast.vue';

export default {
  name: 'SalonListView',
  components: {
    LoadingSpinner,
    NotificationToast
  },
  setup() {
    const store = useStore();
    
    const loading = ref(true);
    const loadingMore = ref(false);
    const showToast = ref(false);
    const toastType = ref('success');
    const toastTitle = ref('');
    const toastMessage = ref('');
    const searchQuery = ref('');
    const selectedCategory = ref('');
    const sortField = ref('rating');
    const currentPage = ref(1);
    const itemsPerPage = ref(9);
    const hasMore = ref(true);

    const categories = ref(['peluqueria', 'barberia', 'estetica', 'unas']);

    // Datos de ejemplo
    const salons = ref([
      {
        id: 1,
        name: "Estilo Urbano",
        image: "https://images.unsplash.com/photo-1585747861815-0468562f7d4f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        description: "Especialistas en cortes modernos y tendencias urbanas.",
        rating: 4.8,
        reviews: 124,
        address: "Av. Siempre Viva 742, Ciudad",
        category: "barberia",
        services: ["Corte de cabello", "Arreglo de barba", "Afeitado clásico", "Tinte", "Mascarillas"],
        openTime: "09:00",
        closeTime: "20:00",
        distance: "1.2 km",
        isFavorite: true
      },
      {
        id: 2,
        name: "Corte Perfecto",
        image: "https://images.unsplash.com/photo-1562322140-8baeececf3df?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        description: "Peluquería unisex con los mejores estilistas de la ciudad.",
        rating: 4.5,
        reviews: 89,
        address: "Calle Falsa 123, Ciudad",
        category: "peluqueria",
        services: ["Corte de cabello", "Coloración", "Peinados", "Tratamientos", "Extensiones"],
        openTime: "08:30",
        closeTime: "19:30",
        distance: "2.5 km",
        isFavorite: false
      },
      {
        id: 3,
        name: "Belleza Integral",
        image: "https://images.unsplash.com/photo-1599351431202-1e0f0137899a?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        description: "Salón de belleza completo con servicios de peluquería y estética.",
        rating: 4.9,
        reviews: 156,
        address: "Boulevard Central 456, Ciudad",
        category: "estetica",
        services: ["Corte de cabello", "Manicura", "Pedicura", "Depilación", "Maquillaje", "Masajes"],
        openTime: "09:30",
        closeTime: "21:00",
        distance: "3.1 km",
        isFavorite: true
      },
      {
        id: 4,
        name: "Barba Elegante",
        image: "https://images.unsplash.com/photo-1622286342621-4bd786c2447c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        description: "Especialistas en el cuidado y estilo de barbas.",
        rating: 4.7,
        reviews: 78,
        address: "Plaza Central 789, Ciudad",
        category: "barberia",
        services: ["Arreglo de barba", "Afeitado tradicional", "Tratamientos para barba", "Depilación"],
        openTime: "10:00",
        closeTime: "20:00",
        distance: "0.8 km",
        isFavorite: false
      },
      {
        id: 5,
        name: "Color Express",
        image: "https://images.unsplash.com/photo-1516975080664-ed2fc6a32937?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        description: "Especialistas en coloración y técnicas modernas de tinte.",
        rating: 4.6,
        reviews: 92,
        address: "Av. del Sol 321, Ciudad",
        category: "peluqueria",
        services: ["Coloración", "Balayage", "Mechas", "Reflejos", "Decoloración"],
        openTime: "08:00",
        closeTime: "19:00",
        distance: "4.2 km",
        isFavorite: false
      },
      {
        id: 6,
        name: "Estilo Clásico",
        image: "https://images.unsplash.com/photo-1503951914875-452162b0f3d1?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        description: "Tradición y elegancia en cada corte desde 1985.",
        rating: 4.9,
        reviews: 203,
        address: "Calle Tradición 654, Ciudad",
        category: "barberia",
        services: ["Corte clásico", "Afeitado con navaja", "Servicio de hot towels", "Tratamientos vintage"],
        openTime: "09:00",
        closeTime: "18:30",
        distance: "1.7 km",
        isFavorite: true
      },
      {
        id: 7,
        name: "Nails Paradise",
        image: "https://images.unsplash.com/photo-1604654894610-df63bc536371?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        description: "Especialistas en uñas y cuidado de manos.",
        rating: 4.8,
        reviews: 145,
        address: "Centro Comercial Galerías, Local 12",
        category: "unas",
        services: ["Manicura", "Pedicura", "Uñas acrílicas", "Esmaltado semipermanente", "Decoración"],
        openTime: "10:00",
        closeTime: "21:00",
        distance: "2.3 km",
        isFavorite: false
      },
      {
        id: 8,
        name: "Glow Estética",
        image: "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        description: "Tratamientos estéticos premium para realzar tu belleza.",
        rating: 4.7,
        reviews: 112,
        address: "Av. Libertador 1234, Piso 3",
        category: "estetica",
        services: ["Limpieza facial", "Masajes relajantes", "Depilación láser", "Tratamientos corporales"],
        openTime: "09:30",
        closeTime: "20:30",
        distance: "3.5 km",
        isFavorite: true
      }
    ]);

    const getCategoryName = (category) => {
      const categories = {
        'peluqueria': 'Peluquería',
        'barberia': 'Barbería',
        'estetica': 'Estética',
        'unas': 'Uñas'
      };
      return categories[category] || category;
    };

    const filteredSalons = computed(() => {
      let result = salons.value;
      
      // Filtrar por búsqueda
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        result = result.filter(salon => 
          salon.name.toLowerCase().includes(query) || 
          salon.description.toLowerCase().includes(query) ||
          getCategoryName(salon.category).toLowerCase().includes(query) ||
          salon.services.some(service => service.toLowerCase().includes(query))
        );
      }
      
      // Filtrar por categoría
      if (selectedCategory.value) {
        result = result.filter(salon => salon.category === selectedCategory.value);
      }
      
      // Ordenar
      if (sortField.value === 'rating') {
        result.sort((a, b) => b.rating - a.rating);
      } else if (sortField.value === 'name') {
        result.sort((a, b) => a.name.localeCompare(b.name));
      } else if (sortField.value === 'distance') {
        result.sort((a, b) => parseFloat(a.distance) - parseFloat(b.distance));
      }
      
      return result;
    });

    const toggleFavorite = (salon) => {
      salon.isFavorite = !salon.isFavorite;
      
      showToast.value = true;
      toastType.value = 'success';
      toastTitle.value = salon.isFavorite ? 'Agregado a favoritos' : 'Eliminado de favoritos';
      toastMessage.value = salon.isFavorite 
        ? `${salon.name} ha sido agregado a tus favoritos.` 
        : `${salon.name} ha sido eliminado de tus favoritos.`;
    };

    const selectCategory = (category) => {
      selectedCategory.value = category;
    };

    const sortBy = (field) => {
      sortField.value = field;
    };

    const clearSearch = () => {
      searchQuery.value = '';
    };

    const clearFilters = () => {
      searchQuery.value = '';
      selectedCategory.value = '';
      sortField.value = 'rating';
    };

    const loadMore = () => {
      loadingMore.value = true;
      // Simular carga de más datos
      setTimeout(() => {
        loadingMore.value = false;
        hasMore.value = false; // En una implementación real, esto dependería de si hay más páginas
      }, 1500);
    };

    onMounted(() => {
      // Simular carga de datos
      setTimeout(() => {
        loading.value = false;
      }, 1000);
    });

    return {
      loading,
      loadingMore,
      showToast,
      toastType,
      toastTitle,
      toastMessage,
      searchQuery,
      selectedCategory,
      categories,
      filteredSalons,
      hasMore,
      getCategoryName,
      toggleFavorite,
      selectCategory,
      sortBy,
      clearSearch,
      clearFilters,
      loadMore
    };
  }
};
</script>

<style scoped>
.salon-header {
  background-color: #f8f9fa;
  padding: 2rem 0;
  margin-bottom: 2rem;
}

.salon-card {
  transition: transform 0.2s, box-shadow 0.2s;
  border: none;
  border-radius: 12px;
  overflow: hidden;
}

.salon-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important;
}

.salon-image-container {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.salon-image-container img {
  object-fit: cover;
  height: 100%;
  width: 100%;
}

.favorite-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: white;
  border: none;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  transition: all 0.2s;
}

.favorite-btn:hover {
  background: #f8f9fa;
  transform: scale(1.1);
}

.salon-category-badge {
  position: absolute;
  bottom: 10px;
  left: 10px;
  background: rgba(0,0,0,0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
}

.rating-badge {
  background: #f8f9fa;
  padding: 4px 8px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.salon-services {
  display: flex;
  flex-wrap: wrap;
}

.input-group-text {
  border-right: none;
}

.form-control:focus {
  box-shadow: none;
  border-color: #ced4da;
}

.dropdown-toggle::after {
  margin-left: 0.5rem;
}

@media (max-width: 768px) {
  .salon-header {
    padding: 1.5rem 0;
  }
  
  .filters-section .row > div {
    margin-bottom: 1rem;
  }
}
</style>