<template>
  <div class="salon-list">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h3>Peluquerías</h3>
      <div class="d-flex gap-2">
        <div class="search-box">
          <i class="fas fa-search"></i>
          <input type="text" v-model="searchQuery" placeholder="Buscar peluquerías..." class="form-control">
        </div>
        <div class="filter-dropdown">
          <button class="btn btn-outline-primary dropdown-toggle" type="button" data-bs-toggle="dropdown">
            <i class="fas fa-filter me-1"></i> Filtros
          </button>
          <div class="dropdown-menu dropdown-menu-end p-3">
            <div class="mb-3">
              <label class="form-label">Categoría</label>
              <select class="form-select" v-model="filters.category">
                <option value="">Todas las categorías</option>
                <option value="peluqueria">Peluquería</option>
                <option value="barberia">Barbería</option>
                <option value="unas">Uñas</option>
                <option value="estetica">Estética</option>
              </select>
            </div>
            <div class="mb-3">
              <label class="form-label">Ordenar por</label>
              <select class="form-select" v-model="filters.sortBy">
                <option value="rating">Mejor valoración</option>
                <option value="distance">Más cercano</option>
                <option value="name">Nombre A-Z</option>
              </select>
            </div>
            <div class="mb-3">
              <label class="form-label">Precio</label>
              <select class="form-select" v-model="filters.priceRange">
                <option value="">Todos los precios</option>
                <option value="low">$ (Económico)</option>
                <option value="medium">$$ (Moderado)</option>
                <option value="high">$$$ (Premium)</option>
              </select>
            </div>
            <button class="btn btn-primary w-100" @click="applyFilters">Aplicar Filtros</button>
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-md-4 col-lg-3">
        <div class="card mb-4">
          <div class="card-header">
            <h6 class="mb-0">Filtros Avanzados</h6>
          </div>
          <div class="card-body">
            <div class="mb-3">
              <label class="form-label">Servicios</label>
              <div class="form-check" v-for="service in availableServices" :key="service">
                <input class="form-check-input" type="checkbox" :id="`service-${service}`" 
                       :value="service" v-model="filters.services">
                <label class="form-check-label" :for="`service-${service}`">{{ service }}</label>
              </div>
            </div>
            
            <div class="mb-3">
              <label class="form-label">Horario</label>
              <div class="form-check">
                <input class="form-check-input" type="checkbox" id="open-now" v-model="filters.openNow">
                <label class="form-check-label" for="open-now">Abierto ahora</label>
              </div>
            </div>
            
            <div class="mb-3">
              <label class="form-label">Calificación mínima</label>
              <div class="rating-filter">
                <div v-for="rating in 5" :key="rating" class="form-check">
                  <input class="form-check-input" type="radio" :id="`rating-${rating}`" 
                         :value="rating" v-model="filters.minRating">
                  <label class="form-check-label" :for="`rating-${rating}`">
                    <span class="text-warning">
                      <i v-for="star in 5" :key="star" 
                         :class="star <= rating ? 'fas fa-star' : 'far fa-star'"></i>
                    </span>
                    {{ rating }}+
                  </label>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="card">
          <div class="card-header">
            <h6 class="mb-0">Mapa</h6>
          </div>
          <div class="card-body">
            <div class="map-placeholder">
              <i class="fas fa-map-marked-alt fa-3x text-muted mb-3"></i>
              <p>Mapa interactivo</p>
              <small class="text-muted">Mostrando {{ filteredSalons.length }} peluquerías</small>
            </div>
          </div>
        </div>
      </div>
      
      <div class="col-md-8 col-lg-9">
        <div v-if="loading" class="text-center py-5">
          <LoadingSpinner message="Cargando peluquerías..." />
        </div>
        
        <div v-else-if="filteredSalons.length === 0" class="text-center py-5">
          <i class="fas fa-store-slash fa-3x text-muted mb-3"></i>
          <h5>No se encontraron peluquerías</h5>
          <p class="text-muted">Intenta ajustar tus filtros de búsqueda</p>
          <button class="btn btn-primary" @click="clearFilters">
            <i class="fas fa-times me-1"></i> Limpiar filtros
          </button>
        </div>
        
        <div v-else class="row">
          <div class="col-lg-6 col-xl-4 mb-4" v-for="salon in filteredSalons" :key="salon.id">
            <SalonCard :salon="salon" @favorite-toggle="toggleFavorite" />
          </div>
        </div>
        
        <div v-if="filteredSalons.length > 0" class="d-flex justify-content-center mt-4">
          <button class="btn btn-outline-primary" @click="loadMore" :disabled="loadingMore">
            <span v-if="loadingMore" class="spinner-border spinner-border-sm me-2"></span>
            {{ loadingMore ? 'Cargando...' : 'Ver más' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import SalonCard from '@/components/client/SalonCard.vue';

export default {
  name: 'SalonList',
  components: {
    LoadingSpinner,
    SalonCard
  },
  setup() {
    const store = useStore();
    
    const loading = ref(true);
    const loadingMore = ref(false);
    const searchQuery = ref('');
    const filters = ref({
      category: '',
      sortBy: 'rating',
      priceRange: '',
      services: [],
      openNow: false,
      minRating: 0
    });
    
    const availableServices = ref([
      'Corte de cabello',
      'Tintura',
      'Alisado',
      'Manicura',
      'Pedicura',
      'Barbería',
      'Maquillaje',
      'Depilación'
    ]);
    
    const salons = ref([]);
    const currentPage = ref(1);
    const itemsPerPage = ref(12);
    
    const filteredSalons = computed(() => {
      let filtered = salons.value;
      
      // Filtrar por búsqueda
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        filtered = filtered.filter(salon => 
          salon.name.toLowerCase().includes(query) ||
          salon.description.toLowerCase().includes(query) ||
          salon.services.some(service => service.toLowerCase().includes(query))
        );
      }
      
      // Filtrar por categoría
      if (filters.value.category) {
        filtered = filtered.filter(salon => salon.category === filters.value.category);
      }
      
      // Filtrar por servicios
      if (filters.value.services.length > 0) {
        filtered = filtered.filter(salon =>
          filters.value.services.every(service =>
            salon.services.includes(service)
          )
        );
      }
      
      // Filtrar por calificación mínima
      if (filters.value.minRating > 0) {
        filtered = filtered.filter(salon => salon.rating >= filters.value.minRating);
      }
      
      // Filtrar por precio
      if (filters.value.priceRange) {
        filtered = filtered.filter(salon => {
          if (filters.value.priceRange === 'low') return salon.priceRange === 1;
          if (filters.value.priceRange === 'medium') return salon.priceRange === 2;
          if (filters.value.priceRange === 'high') return salon.priceRange === 3;
          return true;
        });
      }
      
      // Filtrar por horario (abierto ahora)
      if (filters.value.openNow) {
        const now = new Date();
        const currentTime = now.getHours() * 100 + now.getMinutes();
        filtered = filtered.filter(salon => {
          const openTime = parseInt(salon.openHour.replace(':', ''));
          const closeTime = parseInt(salon.closeHour.replace(':', ''));
          return currentTime >= openTime && currentTime <= closeTime;
        });
      }
      
      // Ordenar
      switch (filters.value.sortBy) {
        case 'rating':
          filtered.sort((a, b) => b.rating - a.rating);
          break;
        case 'distance':
          filtered.sort((a, b) => a.distance - b.distance);
          break;
        case 'name':
          filtered.sort((a, b) => a.name.localeCompare(b.name));
          break;
      }
      
      // Paginación
      return filtered.slice(0, currentPage.value * itemsPerPage.value);
    });
    
    const loadSalons = async () => {
      try {
        loading.value = true;
        // En una app real, haríamos una llamada a la API
        // const response = await api.get('/salons', { params: { ...filters.value, search: searchQuery.value } });
        // salons.value = response.data;
        
        // Datos de ejemplo
        salons.value = [
          {
            id: 1,
            name: "Corte Perfecto",
            image: "https://images.unsplash.com/photo-1562322140-8baeececf3df?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
            category: "peluqueria",
            rating: 4.5,
            reviews: 124,
            distance: 1.2,
            address: "Av. Siempre Viva 742",
            openHour: "09:00",
            closeHour: "19:00",
            priceRange: 2,
            services: ["Corte de cabello", "Tintura", "Alisado"],
            description: "Peluquería especializada en cortes modernos y tinturas.",
            isFavorite: false
          },
          {
            id: 2,
            name: "Estilo Urbano",
            image: "https://images.unsplash.com/photo-1585747861815-0468562f7d4f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
            category: "barberia",
            rating: 4.8,
            reviews: 89,
            distance: 0.8,
            address: "Calle Falsa 123",
            openHour: "08:00",
            closeHour: "20:00",
            priceRange: 2,
            services: ["Corte de cabello", "Barbería", "Afeitado"],
            description: "Barbería clásica con servicios tradicionales para hombres.",
            isFavorite: true
          },
          {
            id: 3,
            name: "Belleza Integral",
            image: "https://images.unsplash.com/photo-1599458254928-4b2f391f2836?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
            category: "estetica",
            rating: 4.3,
            reviews: 67,
            distance: 2.5,
            address: "Av. Libertador 456",
            openHour: "10:00",
            closeHour: "20:00",
            priceRange: 3,
            services: ["Manicura", "Pedicura", "Depilación", "Maquillaje"],
            description: "Centro de estética integral con servicios de belleza completos.",
            isFavorite: false
          }
        ];
        
        // Agregar más datos de ejemplo
        for (let i = 4; i <= 15; i++) {
          salons.value.push({
            id: i,
            name: `Peluquería Ejemplo ${i}`,
            image: "https://images.unsplash.com/photo-1560066984-138dadb4c035?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
            category: ["peluqueria", "barberia", "estetica"][Math.floor(Math.random() * 3)],
            rating: Math.random() * 2 + 3, // Entre 3 y 5
            reviews: Math.floor(Math.random() * 100) + 20,
            distance: Math.random() * 5 + 0.5, // Entre 0.5 y 5.5 km
            address: `Calle Ejemplo ${i}, Ciudad`,
            openHour: "09:00",
            closeHour: "19:00",
            priceRange: Math.floor(Math.random() * 3) + 1, // 1, 2 o 3
            services: availableServices.value.slice(0, Math.floor(Math.random() * 4) + 2),
            description: "Peluquería de calidad con servicios profesionales.",
            isFavorite: false
          });
        }
      } catch (error) {
        console.error('Error loading salons:', error);
      } finally {
        loading.value = false;
      }
    };
    
    const loadMore = async () => {
      loadingMore.value = true;
      // Simular carga de más datos
      await new Promise(resolve => setTimeout(resolve, 1000));
      currentPage.value += 1;
      loadingMore.value = false;
    };
    
    const applyFilters = () => {
      currentPage.value = 1;
      loadSalons();
    };
    
    const clearFilters = () => {
      searchQuery.value = '';
      filters.value = {
        category: '',
        sortBy: 'rating',
        priceRange: '',
        services: [],
        openNow: false,
        minRating: 0
      };
      currentPage.value = 1;
      loadSalons();
    };
    
    const toggleFavorite = (salonId) => {
      const salon = salons.value.find(s => s.id === salonId);
      if (salon) {
        salon.isFavorite = !salon.isFavorite;
        // En una app real, haríamos una llamada a la API para guardar en favoritos
        console.log(`Peluquería ${salonId} ${salon.isFavorite ? 'añadida a' : 'eliminada de'} favoritos`);
      }
    };
    
    onMounted(() => {
      loadSalons();
    });
    
    return {
      loading,
      loadingMore,
      searchQuery,
      filters,
      availableServices,
      filteredSalons,
      loadMore,
      applyFilters,
      clearFilters,
      toggleFavorite
    };
  }
};
</script>

<style scoped>
.salon-list {
  padding: 20px;
}

.search-box {
  position: relative;
  width: 300px;
}

.search-box .fa-search {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #6c757d;
}

.search-box input {
  padding-left: 40px;
}

.filter-dropdown .dropdown-menu {
  width: 250px;
}

.rating-filter .form-check {
  margin-bottom: 5px;
}

.rating-filter .form-check-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.map-placeholder {
  text-align: center;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 10px;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .salon-list {
    padding: 15px;
  }
  
  .search-box {
    width: 100%;
    margin-bottom: 10px;
  }
  
  .d-flex.gap-2 {
    flex-direction: column;
  }
}
</style>