<template>
  <div class="salon-list-view">
    <div class="filters-section">
      <div class="row">
        <div class="col-md-8">
          <div class="search-box">
            <i class="fas fa-search"></i>
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="Buscar peluquerías por nombre, servicio o ubicación..." 
              class="form-control"
            >
          </div>
        </div>
        <div class="col-md-4">
          <div class="filter-controls">
            <select v-model="sortBy" class="form-select me-2">
              <option value="distance">Más cercanas</option>
              <option value="rating">Mejor valoradas</option>
              <option value="name">Orden alfabético</option>
            </select>
            <button class="btn btn-outline-primary" @click="showFilters = !showFilters">
              <i class="fas fa-filter"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Filtros avanzados -->
      <div v-if="showFilters" class="advanced-filters mt-3">
        <div class="row">
          <div class="col-md-3">
            <label class="form-label">Servicios</label>
            <select v-model="serviceFilter" class="form-select">
              <option value="">Todos los servicios</option>
              <option v-for="service in availableServices" :key="service" :value="service">
                {{ service }}
              </option>
            </select>
          </div>
          <div class="col-md-3">
            <label class="form-label">Precio máximo</label>
            <input type="range" v-model="maxPrice" class="form-range" min="0" max="5000" step="100">
            <span>${{ maxPrice }}</span>
          </div>
          <div class="col-md-3">
            <label class="form-label">Rating mínimo</label>
            <select v-model="minRating" class="form-select">
              <option value="0">Cualquier rating</option>
              <option value="3">3+ estrellas</option>
              <option value="4">4+ estrellas</option>
              <option value="4.5">4.5+ estrellas</option>
            </select>
          </div>
          <div class="col-md-3">
            <label class="form-label">Distancia máxima</label>
            <select v-model="maxDistance" class="form-select">
              <option value="5">5 km</option>
              <option value="10">10 km</option>
              <option value="15">15 km</option>
              <option value="20">20 km</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <div class="salons-grid">
      <div v-if="loading" class="text-center py-5">
        <LoadingSpinner message="Buscando peluquerías..." />
      </div>

      <div v-else-if="filteredSalons.length === 0" class="text-center py-5">
        <i class="fas fa-store-slash fa-3x text-muted mb-3"></i>
        <h5>No se encontraron peluquerías</h5>
        <p class="text-muted">Intenta ajustar tus filtros de búsqueda</p>
        <button class="btn btn-primary" @click="clearFilters">
          <i class="fas fa-times me-2"></i> Limpiar filtros
        </button>
      </div>

      <div v-else class="row">
        <div class="col-xl-4 col-lg-6 mb-4" v-for="salon in filteredSalons" :key="salon.id">
          <SalonCard :salon="salon" />
        </div>
      </div>
    </div>

    <!-- Paginación -->
    <div class="pagination-section" v-if="filteredSalons.length > 0">
      <nav aria-label="Page navigation">
        <ul class="pagination justify-content-center">
          <li class="page-item" :class="{ disabled: currentPage === 1 }">
            <button class="page-link" @click="prevPage">
              <i class="fas fa-chevron-left"></i>
            </button>
          </li>
          <li class="page-item" v-for="page in totalPages" :key="page" 
              :class="{ active: page === currentPage }">
            <button class="page-link" @click="goToPage(page)">{{ page }}</button>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
            <button class="page-link" @click="nextPage">
              <i class="fas fa-chevron-right"></i>
            </button>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import SalonCard from '@/components/client/SalonCard.vue';

export default {
  name: 'SalonListView',
  components: {
    LoadingSpinner,
    SalonCard
  },
  setup() {
    const store = useStore();
    const loading = ref(true);
    const showFilters = ref(false);
    const searchQuery = ref('');
    const sortBy = ref('distance');
    const serviceFilter = ref('');
    const maxPrice = ref(5000);
    const minRating = ref(0);
    const maxDistance = ref(10);
    const currentPage = ref(1);
    const itemsPerPage = ref(9);

    const availableServices = ref([
      'Corte de cabello',
      'Tintura',
      'Alisado',
      'Barba',
      'Manicura',
      'Pedicura',
      'Maquillaje',
      'Depilación'
    ]);

    const salons = ref([
      {
        id: 1,
        name: "Estilo Urbano",
        image: "https://images.unsplash.com/photo-1585747861815-0468562f7d4f?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80",
        address: "Av. Siempre Viva 742, Ciudad",
        rating: 4.8,
        totalReviews: 124,
        distance: "1.2 km",
        services: ["Corte de cabello", "Barba", "Tintura"],
        priceRange: "$$",
        isOpen: true,
        openingHours: "09:00 - 20:00"
      },
      {
        id: 2,
        name: "Corte Perfecto",
        image: "https://images.unsplash.com/photo-1562322140-8baeececf3df?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80",
        address: "Calle Falsa 123, Ciudad",
        rating: 4.5,
        totalReviews: 89,
        distance: "0.8 km",
        services: ["Corte de cabello", "Alisado", "Manicura"],
        priceRange: "$$$",
        isOpen: true,
        openingHours: "08:00 - 19:00"
      },
      // Más peluquerías...
    ]);

    const filteredSalons = computed(() => {
      let filtered = salons.value;

      // Filtrar por búsqueda
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        filtered = filtered.filter(salon => 
          salon.name.toLowerCase().includes(query) ||
          salon.address.toLowerCase().includes(query) ||
          salon.services.some(service => service.toLowerCase().includes(query))
        );
      }

      // Filtrar por servicio
      if (serviceFilter.value) {
        filtered = filtered.filter(salon => 
          salon.services.includes(serviceFilter.value)
        );
      }

      // Filtrar por rating
      if (minRating.value > 0) {
        filtered = filtered.filter(salon => salon.rating >= minRating.value);
      }

      // Ordenar
      switch (sortBy.value) {
        case 'distance':
          filtered.sort((a, b) => parseFloat(a.distance) - parseFloat(b.distance));
          break;
        case 'rating':
          filtered.sort((a, b) => b.rating - a.rating);
          break;
        case 'name':
          filtered.sort((a, b) => a.name.localeCompare(b.name));
          break;
      }

      return filtered;
    });

    const totalPages = computed(() => Math.ceil(filteredSalons.value.length / itemsPerPage.value));

    const paginatedSalons = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage.value;
      const end = start + itemsPerPage.value;
      return filteredSalons.value.slice(start, end);
    });

    const loadSalons = async () => {
      try {
        loading.value = true;
        // Simular carga de datos
        await new Promise(resolve => setTimeout(resolve, 1000));
      } catch (error) {
        console.error('Error loading salons:', error);
      } finally {
        loading.value = false;
      }
    };

    const clearFilters = () => {
      searchQuery.value = '';
      serviceFilter.value = '';
      maxPrice.value = 5000;
      minRating.value = 0;
      currentPage.value = 1;
    };

    const goToPage = (page) => {
      currentPage.value = page;
    };

    const nextPage = () => {
      if (currentPage.value < totalPages.value) {
        currentPage.value++;
      }
    };

    const prevPage = () => {
      if (currentPage.value > 1) {
        currentPage.value--;
      }
    };

    onMounted(() => {
      loadSalons();
    });

    return {
      loading,
      showFilters,
      searchQuery,
      sortBy,
      serviceFilter,
      maxPrice,
      minRating,
      maxDistance,
      currentPage,
      availableServices,
      filteredSalons: paginatedSalons,
      totalPages,
      clearFilters,
      goToPage,
      nextPage,
      prevPage
    };
  }
};
</script>

<style scoped>
.salon-list-view {
  padding: 20px;
}

.filters-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 25px;
}

.search-box {
  position: relative;
}

.search-box .fa-search {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #6c757d;
}

.search-box input {
  padding-left: 40px;
}

.filter-controls {
  display: flex;
  gap: 12px;
}

.advanced-filters {
  background: white;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.salons-grid {
  min-height: 400px;
}

.pagination-section {
  margin-top: 30px;
}

.page-link {
  border-radius: 8px;
  margin: 0 4px;
  border: none;
  color: #6f42c1;
  font-weight: 500;
}

.page-item.active .page-link {
  background: #6f42c1;
  border-color: #6f42c1;
}

.page-item.disabled .page-link {
  color: #6c757d;
}

@media (max-width: 768px) {
  .salon-list-view {
    padding: 15px;
  }
  
  .filters-section {
    padding: 15px;
  }
  
  .filter-controls {
    flex-direction: column;
    gap: 10px;
  }
  
  .advanced-filters .row {
    margin: 0 -5px;
  }
  
  .advanced-filters .col-md-3 {
    padding: 0 5px;
    margin-bottom: 10px;
  }
}
</style>