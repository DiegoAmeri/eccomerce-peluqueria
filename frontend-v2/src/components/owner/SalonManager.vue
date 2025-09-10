<template>
  <div class="salon-manager">
    <!-- Header -->
    <div class="manager-header">
      <div class="header-content">
        <h1>Gestión de Peluquerías</h1>
        <p class="text-muted">Administra todas tus peluquerías desde un solo lugar</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" @click="openCreateModal">
          <i class="fas fa-plus me-2"></i> Nueva Peluquería
        </button>
      </div>
    </div>

    <!-- Filters and Search -->
    <div class="filters-section">
      <div class="row">
        <div class="col-md-6">
          <div class="search-box">
            <i class="fas fa-search"></i>
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="Buscar peluquerías..." 
              class="form-control"
            >
          </div>
        </div>
        <div class="col-md-6">
          <div class="filter-controls">
            <select v-model="statusFilter" class="form-select me-2">
              <option value="">Todos los estados</option>
              <option value="active">Activas</option>
              <option value="inactive">Inactivas</option>
              <option value="maintenance">En mantenimiento</option>
            </select>
            <select v-model="sortBy" class="form-select">
              <option value="name">Ordenar por nombre</option>
              <option value="revenue">Ordenar por ingresos</option>
              <option value="rating">Ordenar por rating</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Statistics Overview -->
    <div class="stats-overview">
      <div class="row">
        <div class="col-md-3">
          <div class="stat-item">
            <div class="stat-value">{{ totalSalons }}</div>
            <div class="stat-label">Total Peluquerías</div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="stat-item">
            <div class="stat-value">{{ activeSalons }}</div>
            <div class="stat-label">Activas</div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="stat-item">
            <div class="stat-value">${{ totalRevenue.toLocaleString() }}</div>
            <div class="stat-label">Ingresos Totales</div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="stat-item">
            <div class="stat-value">{{ totalEmployees }}</div>
            <div class="stat-label">Empleados Totales</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Salons Grid -->
    <div class="salons-grid">
      <div v-if="loading" class="text-center py-5">
        <LoadingSpinner message="Cargando peluquerías..." />
      </div>

      <div v-else-if="filteredSalons.length === 0" class="text-center py-5">
        <i class="fas fa-store-slash fa-3x text-muted mb-3"></i>
        <h5>No se encontraron peluquerías</h5>
        <p class="text-muted">Intenta ajustar tus filtros de búsqueda o crea una nueva peluquería</p>
        <button class="btn btn-primary" @click="openCreateModal">
          <i class="fas fa-plus me-2"></i> Crear primera peluquería
        </button>
      </div>

      <div v-else class="row">
        <div class="col-xl-4 col-lg-6 mb-4" v-for="salon in filteredSalons" :key="salon.id">
          <SalonCard :salon="salon" @edit="openEditModal" @delete="confirmDelete" />
        </div>
      </div>
    </div>

    <!-- Pagination -->
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

    <!-- Create/Edit Modal -->
    <ModalDialog 
      :show="showModal" 
      :title="modalTitle" 
      size="large"
      @update:show="showModal = false"
    >
      <SalonForm 
        :salon="editingSalon" 
        @submit="handleSubmit" 
        @cancel="showModal = false" 
      />
    </ModalDialog>

    <!-- Delete Confirmation Modal -->
    <ModalDialog 
      :show="showDeleteModal" 
      title="Confirmar Eliminación"
      @update:show="showDeleteModal = false"
    >
      <div class="text-center">
        <i class="fas fa-exclamation-triangle fa-3x text-warning mb-3"></i>
        <h5>¿Estás seguro de que quieres eliminar esta peluquería?</h5>
        <p class="text-muted">
          Esta acción no se puede deshacer. Todos los datos de la peluquería se perderán permanentemente.
        </p>
        <div class="d-flex justify-content-center gap-3">
          <button class="btn btn-secondary" @click="showDeleteModal = false">
            Cancelar
          </button>
          <button class="btn btn-danger" @click="confirmDeleteAction">
            Sí, eliminar
          </button>
        </div>
      </div>
    </ModalDialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import ModalDialog from '@/components/common/ModalDialog.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import SalonCard from '@/components/owner/SalonCard.vue';
import SalonForm from '@/components/owner/SalonForm.vue';

export default {
  name: 'SalonManager',
  components: {
    ModalDialog,
    LoadingSpinner,
    SalonCard,
    SalonForm
  },
  setup() {
    const store = useStore();
    
    const loading = ref(true);
    const showModal = ref(false);
    const showDeleteModal = ref(false);
    const editingSalon = ref(null);
    const deletingSalonId = ref(null);
    const searchQuery = ref('');
    const statusFilter = ref('');
    const sortBy = ref('name');
    const currentPage = ref(1);
    const itemsPerPage = ref(6);
    
    const salons = ref([
      {
        id: 1,
        name: "Estilo Urbano",
        image: "https://images.unsplash.com/photo-1585747861815-0468562f7d4f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        address: "Av. Siempre Viva 742, Ciudad",
        phone: "+54 11 1234-5678",
        status: "active",
        rating: 4.8,
        totalReviews: 124,
        revenue: 58400,
        employees: 8,
        appointments: 156,
        services: ["Corte de cabello", "Barba", "Tintura"],
        schedule: {
          open: "09:00",
          close: "20:00",
          days: "Lunes a Sábado"
        }
      },
      {
        id: 2,
        name: "Corte Perfecto",
        image: "https://images.unsplash.com/photo-1562322140-8baeececf3df?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        address: "Calle Falsa 123, Ciudad",
        phone: "+54 11 2345-6789",
        status: "active",
        rating: 4.5,
        totalReviews: 89,
        revenue: 42200,
        employees: 6,
        appointments: 132,
        services: ["Corte de cabello", "Alisado", "Manicura"],
        schedule: {
          open: "08:00",
          close: "19:00",
          days: "Lunes a Viernes"
        }
      },
      {
        id: 3,
        name: "Belleza Integral",
        image: "https://images.unsplash.com/photo-1599458254928-4b2f391f2836?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        address: "Av. Libertador 456, Ciudad",
        phone: "+54 11 3456-7890",
        status: "maintenance",
        rating: 4.3,
        totalReviews: 67,
        revenue: 29800,
        employees: 5,
        appointments: 98,
        services: ["Manicura", "Pedicura", "Depilación", "Maquillaje"],
        schedule: {
          open: "10:00",
          close: "20:00",
          days: "Martes a Domingo"
        }
      }
    ]);
    
    const filteredSalons = computed(() => {
      let filtered = salons.value;
      
      // Filtrar por búsqueda
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        filtered = filtered.filter(salon => 
          salon.name.toLowerCase().includes(query) ||
          salon.address.toLowerCase().includes(query) ||
          salon.phone.toLowerCase().includes(query)
        );
      }
      
      // Filtrar por estado
      if (statusFilter.value) {
        filtered = filtered.filter(salon => salon.status === statusFilter.value);
      }
      
      // Ordenar
      switch (sortBy.value) {
        case 'name':
          filtered.sort((a, b) => a.name.localeCompare(b.name));
          break;
        case 'revenue':
          filtered.sort((a, b) => b.revenue - a.revenue);
          break;
        case 'rating':
          filtered.sort((a, b) => b.rating - a.rating);
          break;
      }
      
      return filtered;
    });
    
    const totalSalons = computed(() => salons.value.length);
    const activeSalons = computed(() => salons.value.filter(s => s.status === 'active').length);
    const totalRevenue = computed(() => salons.value.reduce((sum, salon) => sum + salon.revenue, 0));
    const totalEmployees = computed(() => salons.value.reduce((sum, salon) => sum + salon.employees, 0));
    
    const totalPages = computed(() => Math.ceil(filteredSalons.value.length / itemsPerPage.value));
    
    const paginatedSalons = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage.value;
      const end = start + itemsPerPage.value;
      return filteredSalons.value.slice(start, end);
    });
    
    const modalTitle = computed(() => 
      editingSalon.value ? 'Editar Peluquería' : 'Nueva Peluquería'
    );
    
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
    
    const openCreateModal = () => {
      editingSalon.value = null;
      showModal.value = true;
    };
    
    const openEditModal = (salon) => {
      editingSalon.value = { ...salon };
      showModal.value = true;
    };
    
    const confirmDelete = (salonId) => {
      deletingSalonId.value = salonId;
      showDeleteModal.value = true;
    };
    
    const confirmDeleteAction = () => {
      salons.value = salons.value.filter(salon => salon.id !== deletingSalonId.value);
      showDeleteModal.value = false;
      deletingSalonId.value = null;
    };
    
    const handleSubmit = (salonData) => {
      if (editingSalon.value) {
        // Editar peluquería existente
        const index = salons.value.findIndex(s => s.id === editingSalon.value.id);
        if (index !== -1) {
          salons.value[index] = { ...salons.value[index], ...salonData };
        }
      } else {
        // Crear nueva peluquería
        const newSalon = {
          id: Math.max(...salons.value.map(s => s.id)) + 1,
          ...salonData,
          rating: 0,
          totalReviews: 0,
          revenue: 0,
          employees: 0,
          appointments: 0,
          status: 'active'
        };
        salons.value.push(newSalon);
      }
      
      showModal.value = false;
      editingSalon.value = null;
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
      showModal,
      showDeleteModal,
      editingSalon,
      searchQuery,
      statusFilter,
      sortBy,
      currentPage,
      filteredSalons: paginatedSalons,
      totalSalons,
      activeSalons,
      totalRevenue,
      totalEmployees,
      totalPages,
      modalTitle,
      openCreateModal,
      openEditModal,
      confirmDelete,
      confirmDeleteAction,
      handleSubmit,
      goToPage,
      nextPage,
      prevPage
    };
  }
};
</script>

<style scoped>
.salon-manager {
  padding: 20px;
}

.manager-header {
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

.stats-overview {
  margin-bottom: 30px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: #6f42c1;
  margin-bottom: 5px;
}

.stat-label {
  color: #6c757d;
  font-weight: 500;
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
  .salon-manager {
    padding: 15px;
  }
  
  .manager-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .filters-section {
    padding: 15px;
  }
  
  .filter-controls {
    flex-direction: column;
    gap: 10px;
  }
  
  .stats-overview .row {
    margin: 0 -5px;
  }
  
  .stats-overview .col-md-3 {
    padding: 0 5px;
    margin-bottom: 10px;
  }
  
  .stat-item {
    padding: 15px;
  }
  
  .stat-value {
    font-size: 1.5rem;
  }
}
</style>