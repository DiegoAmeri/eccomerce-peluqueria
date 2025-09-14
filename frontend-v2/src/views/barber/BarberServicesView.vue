<template>
  <div class="barber-services">
    <!-- Header -->
    <div class="services-header">
      <div class="header-content">
        <h1>Mis Servicios</h1>
        <p class="text-muted">Gestiona tus servicios y precios</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" @click="showNewServiceModal">
          <i class="fas fa-plus me-2"></i> Nuevo Servicio
        </button>
      </div>
    </div>

    <!-- Services List -->
    <div class="card">
      <div class="card-header">
        <div class="row align-items-center">
          <div class="col-md-6">
            <h5 class="mb-0">Lista de Servicios</h5>
          </div>
          <div class="col-md-6">
            <div class="d-flex gap-2">
              <div class="search-control">
                <input 
                  type="text" 
                  class="form-control" 
                  placeholder="Buscar servicios..."
                  v-model="searchQuery"
                >
              </div>
              <div class="filter-control">
                <select v-model="categoryFilter" class="form-select">
                  <option value="">Todas las categorías</option>
                  <option v-for="category in categories" :key="category" :value="category">
                    {{ category }}
                  </option>
                </select>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="card-body">
        <div v-if="loading" class="text-center py-5">
          <LoadingSpinner message="Cargando servicios..." />
        </div>

        <div v-else-if="filteredServices.length === 0" class="text-center py-5">
          <i class="fas fa-scissors fa-3x text-muted mb-3"></i>
          <h5>No hay servicios registrados</h5>
          <p class="text-muted">Comienza agregando tus primeros servicios</p>
          <button class="btn btn-primary" @click="showNewServiceModal">
            <i class="fas fa-plus me-2"></i> Agregar primer servicio
          </button>
        </div>

        <div v-else class="services-list">
          <div 
            v-for="service in filteredServices" 
            :key="service.id"
            class="service-item"
          >
            <div class="service-info">
              <div class="service-icon">
                <i :class="service.icon"></i>
              </div>
              <div class="service-details">
                <h6 class="service-name">{{ service.name }}</h6>
                <p class="service-description">{{ service.description }}</p>
                <div class="service-meta">
                  <span class="category-badge">{{ service.category }}</span>
                  <span class="duration">{{ service.duration }} min</span>
                </div>
              </div>
            </div>
            
            <div class="service-price">
              <span class="price">${{ service.price }}</span>
            </div>
            
            <div class="service-actions">
              <button 
                class="btn btn-sm btn-outline-primary"
                @click="editService(service)"
                title="Editar servicio"
              >
                <i class="fas fa-edit"></i>
              </button>
              <button 
                class="btn btn-sm btn-outline-danger"
                @click="deleteService(service)"
                title="Eliminar servicio"
              >
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- New Service Modal -->
    <ModalDialog 
      :show="showModal" 
      :title="modalTitle"
      size="medium"
      @update:show="showModal = false"
    >
      <ServiceForm 
        :service="editingService"
        @submit="handleServiceSubmit"
        @cancel="showModal = false"
      />
    </ModalDialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import ModalDialog from '@/components/common/ModalDialog.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import ServiceForm from '@/components/barber/ServiceForm.vue';

export default {
  name: 'BarberServicesView',
  components: {
    ModalDialog,
    LoadingSpinner,
    ServiceForm
  },
  setup() {
    const loading = ref(true);
    const showModal = ref(false);
    const editingService = ref(null);
    const searchQuery = ref('');
    const categoryFilter = ref('');
    
    const services = ref([
      {
        id: 1,
        name: "Corte de Cabello",
        description: "Corte moderno y estilizado",
        category: "Cortes",
        price: 500,
        duration: 30,
        icon: "fas fa-cut"
      },
      {
        id: 2,
        name: "Corte + Barba",
        description: "Corte de cabello y arreglo de barba",
        category: "Cortes",
        price: 700,
        duration: 45,
        icon: "fas fa-air-freshener"
      },
      {
        id: 3,
        name: "Coloración",
        description: "Tinte profesional con productos de calidad",
        category: "Color",
        price: 1200,
        duration: 90,
        icon: "fas fa-paint-brush"
      },
      {
        id: 4,
        name: "Tratamiento Capilar",
        description: "Hidratación y reparación capilar",
        category: "Tratamientos",
        price: 800,
        duration: 60,
        icon: "fas fa-spa"
      }
    ]);
    
    const categories = computed(() => {
      const uniqueCategories = new Set(services.value.map(service => service.category));
      return Array.from(uniqueCategories);
    });
    
    const filteredServices = computed(() => {
      let filtered = services.value;
      
      // Filtrar por búsqueda
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        filtered = filtered.filter(service => 
          service.name.toLowerCase().includes(query) || 
          service.description.toLowerCase().includes(query)
        );
      }
      
      // Filtrar por categoría
      if (categoryFilter.value) {
        filtered = filtered.filter(service => service.category === categoryFilter.value);
      }
      
      return filtered;
    });
    
    const modalTitle = computed(() => 
      editingService.value ? 'Editar Servicio' : 'Nuevo Servicio'
    );
    
    const loadServices = async () => {
      try {
        loading.value = true;
        // Simular carga de datos
        await new Promise(resolve => setTimeout(resolve, 800));
      } catch (error) {
        console.error('Error loading services:', error);
      } finally {
        loading.value = false;
      }
    };
    
    const showNewServiceModal = () => {
      editingService.value = null;
      showModal.value = true;
    };
    
    const editService = (service) => {
      editingService.value = { ...service };
      showModal.value = true;
    };
    
    const handleServiceSubmit = (serviceData) => {
      if (editingService.value) {
        // Editar servicio existente
        const index = services.value.findIndex(s => s.id === editingService.value.id);
        if (index !== -1) {
          services.value[index] = { ...services.value[index], ...serviceData };
        }
      } else {
        // Crear nuevo servicio
        const newService = {
          id: Math.max(...services.value.map(s => s.id)) + 1,
          ...serviceData
        };
        services.value.push(newService);
      }
      
      showModal.value = false;
      editingService.value = null;
    };
    
    const deleteService = (service) => {
      if (confirm('¿Estás seguro de que quieres eliminar este servicio?')) {
        services.value = services.value.filter(s => s.id !== service.id);
      }
    };
    
    onMounted(() => {
      loadServices();
    });
    
    return {
      loading,
      showModal,
      editingService,
      searchQuery,
      categoryFilter,
      services,
      categories,
      filteredServices,
      modalTitle,
      showNewServiceModal,
      editService,
      handleServiceSubmit,
      deleteService
    };
  }
};
</script>

<style scoped>
.services-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
}

.header-content h1 {
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 5px;
}

.search-control,
.filter-control {
  flex: 1;
}

.services-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.service-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: white;
  border-radius: 12px;
  border-left: 4px solid #6f42c1;
  transition: all 0.3s ease;
}

.service-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.service-info {
  display: flex;
  align-items: center;
  flex: 1;
  gap: 15px;
}

.service-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  background: rgba(111, 66, 193, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6f42c1;
  font-size: 1.2rem;
}

.service-details {
  flex: 1;
}

.service-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.service-description {
  color: #6c757d;
  font-size: 0.9rem;
  margin-bottom: 8px;
}

.service-meta {
  display: flex;
  gap: 10px;
  align-items: center;
}

.category-badge {
  background: #e9ecef;
  color: #495057;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.duration {
  color: #6c757d;
  font-size: 0.85rem;
}

.service-price {
  margin: 0 20px;
  font-weight: 700;
  color: #198754;
  font-size: 1.1rem;
}

.service-actions {
  display: flex;
  gap: 8px;
}

.service-actions .btn {
  border-radius: 8px;
  padding: 6px 10px;
}

@media (max-width: 768px) {
  .services-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .service-item {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
  }
  
  .service-info {
    flex-direction: column;
    text-align: center;
  }
  
  .service-price {
    text-align: center;
    margin: 0;
  }
  
  .service-actions {
    justify-content: center;
  }
}
</style>