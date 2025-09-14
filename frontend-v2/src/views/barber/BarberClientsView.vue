<template>
  <div class="barber-clients">
    <!-- Header -->
    <div class="clients-header">
      <div class="header-content">
        <h1>Mis Clientes</h1>
        <p class="text-muted">Gestiona tu lista de clientes y su historial</p>
      </div>
      <div class="header-actions">
        <div class="search-control">
          <input 
            type="text" 
            class="form-control" 
            placeholder="Buscar clientes..."
            v-model="searchQuery"
          >
          <i class="fas fa-search search-icon"></i>
        </div>
      </div>
    </div>

    <!-- Clients Stats -->
    <div class="row mb-4">
      <div class="col-md-3 col-6">
        <div class="stat-card">
          <div class="stat-icon clients">
            <i class="fas fa-users"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalClients }}</div>
            <div class="stat-label">Clientes Totales</div>
          </div>
        </div>
      </div>
      <div class="col-md-3 col-6">
        <div class="stat-card">
          <div class="stat-icon new">
            <i class="fas fa-user-plus"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.newThisMonth }}</div>
            <div class="stat-label">Nuevos este mes</div>
          </div>
        </div>
      </div>
      <div class="col-md-3 col-6">
        <div class="stat-card">
          <div class="stat-icon regular">
            <i class="fas fa-star"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.regularClients }}</div>
            <div class="stat-label">Clientes Regulares</div>
          </div>
        </div>
      </div>
      <div class="col-md-3 col-6">
        <div class="stat-card">
          <div class="stat-icon revenue">
            <i class="fas fa-dollar-sign"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">${{ stats.averageRevenue }}</div>
            <div class="stat-label">Ingreso Promedio</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Clients List -->
    <div class="card">
      <div class="card-header">
        <div class="row align-items-center">
          <div class="col-md-6">
            <h5 class="mb-0">Lista de Clientes</h5>
          </div>
          <div class="col-md-6">
            <div class="d-flex gap-2 justify-content-end">
              <div class="filter-control">
                <select v-model="statusFilter" class="form-select">
                  <option value="">Todos los clientes</option>
                  <option value="active">Activos</option>
                  <option value="new">Nuevos</option>
                  <option value="inactive">Inactivos</option>
                </select>
              </div>
              <div class="sort-control">
                <select v-model="sortBy" class="form-select">
                  <option value="name">Ordenar por nombre</option>
                  <option value="recent">Más recientes</option>
                  <option value="visits">Más visitas</option>
                </select>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="card-body">
        <div v-if="loading" class="text-center py-5">
          <LoadingSpinner message="Cargando clientes..." />
        </div>

        <div v-else-if="filteredClients.length === 0" class="text-center py-5">
          <i class="fas fa-users fa-3x text-muted mb-3"></i>
          <h5>No hay clientes registrados</h5>
          <p class="text-muted">Tus clientes aparecerán aquí una vez que agenden turnos</p>
        </div>

        <div v-else class="clients-list">
          <div 
            v-for="client in filteredClients" 
            :key="client.id"
            class="client-item"
          >
            <div class="client-avatar">
              <img :src="client.avatar" :alt="client.name">
            </div>
            <div class="client-info">
              <h6 class="client-name">{{ client.name }}</h6>
              <div class="client-contact">
                <span class="phone"><i class="fas fa-phone"></i> {{ client.phone }}</span>
                <span class="email"><i class="fas fa-envelope"></i> {{ client.email }}</span>
              </div>
              <div class="client-meta">
                <span class="visits"><i class="fas fa-calendar-check"></i> {{ client.visits }} visitas</span>
                <span class="last-visit">Última: {{ formatDate(client.lastVisit) }}</span>
                <span class="status" :class="client.status">{{ getStatusText(client.status) }}</span>
              </div>
            </div>
            
            <div class="client-actions">
              <button 
                class="btn btn-sm btn-outline-primary"
                @click="viewClientDetails(client)"
                title="Ver detalles"
              >
                <i class="fas fa-eye"></i>
              </button>
              <button 
                class="btn btn-sm btn-outline-success"
                @click="bookAppointment(client)"
                title="Agendar turno"
              >
                <i class="fas fa-calendar-plus"></i>
              </button>
              <button 
                class="btn btn-sm btn-outline-secondary"
                @click="contactClient(client)"
                title="Contactar"
              >
                <i class="fas fa-comment"></i>
              </button>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="filteredClients.length > 0" class="pagination-container">
          <nav aria-label="Client pagination">
            <ul class="pagination justify-content-center">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <button class="page-link" @click="changePage(currentPage - 1)">
                  <i class="fas fa-chevron-left"></i>
                </button>
              </li>
              
              <li v-for="page in totalPages" :key="page" class="page-item" :class="{ active: currentPage === page }">
                <button class="page-link" @click="changePage(page)">{{ page }}</button>
              </li>
              
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <button class="page-link" @click="changePage(currentPage + 1)">
                  <i class="fas fa-chevron-right"></i>
                </button>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>

    <!-- Client Details Modal -->
    <ModalDialog 
      :show="showClientModal" 
      :title="selectedClient ? selectedClient.name : ''"
      size="large"
      @update:show="showClientModal = false"
    >
      <ClientDetails 
        v-if="selectedClient" 
        :client="selectedClient"
        @close="showClientModal = false"
        @book="bookAppointmentFromModal"
      />
    </ModalDialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import ModalDialog from '@/components/common/ModalDialog.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import ClientDetails from '@/components/barber/ClientDetails.vue';

export default {
  name: 'BarberClientsView',
  components: {
    ModalDialog,
    LoadingSpinner,
    ClientDetails
  },
  setup() {
    const loading = ref(true);
    const showClientModal = ref(false);
    const selectedClient = ref(null);
    const searchQuery = ref('');
    const statusFilter = ref('');
    const sortBy = ref('name');
    const currentPage = ref(1);
    const itemsPerPage = ref(8);
    
    const stats = ref({
      totalClients: 154,
      newThisMonth: 12,
      regularClients: 87,
      averageRevenue: 750
    });
    
    const clients = ref([
      {
        id: 1,
        name: "Carlos Rodríguez",
        email: "carlos@email.com",
        phone: "+54 11 1234-5678",
        avatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-1.2.1&auto=format&fit=crop&w=200&q=80",
        visits: 15,
        lastVisit: "2023-11-20",
        status: "active",
        notes: "Prefiere cortes clásicos, cliente desde 2021"
      },
      {
        id: 2,
        name: "María González",
        email: "maria@email.com",
        phone: "+54 11 2345-6789",
        avatar: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-1.2.1&auto=format&fit=crop&w=200&q=80",
        visits: 8,
        lastVisit: "2023-11-18",
        status: "active",
        notes: "Siempre pide coloración, muy puntual"
      },
      {
        id: 3,
        name: "Luis Fernández",
        email: "luis@email.com",
        phone: "+54 11 3456-7890",
        avatar: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?ixlib=rb-1.2.1&auto=format&fit=crop&w=200&q=80",
        visits: 22,
        lastVisit: "2023-11-15",
        status: "regular",
        notes: "Cliente frecuente, viene cada 3 semanas"
      },
      {
        id: 4,
        name: "Ana Martínez",
        email: "ana@email.com",
        phone: "+54 11 4567-8901",
        avatar: "https://images.unsplash.com/photo-1517841905240-472988babdf9?ixlib=rb-1.2.1&auto=format&fit=crop&w=200&q=80",
        visits: 3,
        lastVisit: "2023-10-28",
        status: "new",
        notes: "Nueva cliente, recomendada por Carlos"
      },
      {
        id: 5,
        name: "Jorge Silva",
        email: "jorge@email.com",
        phone: "+54 11 5678-9012",
        avatar: "https://images.unsplash.com/photo-1519345182560-3f2917c472ef?ixlib=rb-1.2.1&auto=format&fit=crop&w=200&q=80",
        visits: 18,
        lastVisit: "2023-10-10",
        status: "inactive",
        notes: "No viene hace 2 meses, seguir contacto"
      },
      {
        id: 6,
        name: "Laura Díaz",
        email: "laura@email.com",
        phone: "+54 11 6789-0123",
        avatar: "https://images.unsplash.com/photo-1517282009859-f000ec3b26fe?ixlib=rb-1.2.1&auto=format&fit=crop&w=200&q=80",
        visits: 7,
        lastVisit: "2023-11-22",
        status: "active",
        notes: "Siempre pide tratamientos capilares"
      },
      {
        id: 7,
        name: "Roberto Pérez",
        email: "roberto@email.com",
        phone: "+54 11 7890-1234",
        avatar: "https://images.unsplash.com/photo-1508341591423-4347099e1f19?ixlib=rb-1.2.1&auto=format&fit=crop&w=200&q=80",
        visits: 12,
        lastVisit: "2023-11-19",
        status: "active",
        notes: "Solo cortes, muy específico con su estilo"
      },
      {
        id: 8,
        name: "Sofía López",
        email: "sofia@email.com",
        phone: "+54 11 8901-2345",
        avatar: "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?ixlib=rb-1.2.1&auto=format&fit=crop&w=200&q=80",
        visits: 5,
        lastVisit: "2023-11-12",
        status: "new",
        notes: "Cliente nueva, muy satisfecha con el último servicio"
      }
    ]);
    
    const filteredClients = computed(() => {
      let filtered = clients.value;
      
      // Filtrar por búsqueda
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        filtered = filtered.filter(client => 
          client.name.toLowerCase().includes(query) || 
          client.email.toLowerCase().includes(query) ||
          client.phone.toLowerCase().includes(query)
        );
      }
      
      // Filtrar por estado
      if (statusFilter.value) {
        filtered = filtered.filter(client => client.status === statusFilter.value);
      }
      
      // Ordenar
      if (sortBy.value === 'name') {
        filtered.sort((a, b) => a.name.localeCompare(b.name));
      } else if (sortBy.value === 'recent') {
        filtered.sort((a, b) => new Date(b.lastVisit) - new Date(a.lastVisit));
      } else if (sortBy.value === 'visits') {
        filtered.sort((a, b) => b.visits - a.visits);
      }
      
      return filtered;
    });
    
    const paginatedClients = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage.value;
      return filteredClients.value.slice(start, start + itemsPerPage.value);
    });
    
    const totalPages = computed(() => {
      return Math.ceil(filteredClients.value.length / itemsPerPage.value);
    });
    
    const loadClients = async () => {
      try {
        loading.value = true;
        // Simular carga de datos
        await new Promise(resolve => setTimeout(resolve, 800));
      } catch (error) {
        console.error('Error loading clients:', error);
      } finally {
        loading.value = false;
      }
    };
    
    const viewClientDetails = (client) => {
      selectedClient.value = client;
      showClientModal.value = true;
    };
    
    const bookAppointment = (client) => {
      // Navegar a la vista de agendar turno con el cliente pre-seleccionado
      console.log('Agendar turno para:', client.name);
      // this.$router.push({ name: 'BarberAppointments', query: { client: client.id } });
    };
    
    const bookAppointmentFromModal = (client) => {
      showClientModal.value = false;
      bookAppointment(client);
    };
    
    const contactClient = (client) => {
      console.log('Contactar a:', client.name);
      // Aquí se podría integrar con WhatsApp, email, etc.
    };
    
    const formatDate = (dateString) => {
      const date = new Date(dateString);
      return date.toLocaleDateString('es-ES');
    };
    
    const getStatusText = (status) => {
      const statusMap = {
        active: 'Activo',
        new: 'Nuevo',
        inactive: 'Inactivo',
        regular: 'Regular'
      };
      return statusMap[status] || status;
    };
    
    const changePage = (page) => {
      if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page;
      }
    };
    
    onMounted(() => {
      loadClients();
    });
    
    return {
      loading,
      showClientModal,
      selectedClient,
      searchQuery,
      statusFilter,
      sortBy,
      currentPage,
      stats,
      clients: paginatedClients,
      filteredClients,
      totalPages,
      viewClientDetails,
      bookAppointment,
      bookAppointmentFromModal,
      contactClient,
      formatDate,
      getStatusText,
      changePage
    };
  }
};
</script>

<style scoped>
.clients-header {
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

.search-control {
  position: relative;
  width: 300px;
}

.search-control input {
  padding-left: 40px;
}

.search-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #6c757d;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  margin-right: 15px;
}

.stat-icon.clients {
  background: rgba(111, 66, 193, 0.1);
  color: #6f42c1;
}

.stat-icon.new {
  background: rgba(32, 201, 151, 0.1);
  color: #20c997;
}

.stat-icon.regular {
  background: rgba(255, 193, 7, 0.1);
  color: #ffc107;
}

.stat-icon.revenue {
  background: rgba(13, 110, 253, 0.1);
  color: #0d6efd;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
}

.stat-label {
  color: #6c757d;
  font-size: 0.9rem;
}

.clients-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.client-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: white;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.client-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.client-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 15px;
  flex-shrink: 0;
}

.client-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.client-info {
  flex: 1;
}

.client-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.client-contact {
  display: flex;
  gap: 15px;
  margin-bottom: 6px;
}

.client-contact span {
  font-size: 0.85rem;
  color: #6c757d;
}

.client-contact i {
  margin-right: 4px;
}

.client-meta {
  display: flex;
  gap: 15px;
  align-items: center;
}

.client-meta span {
  font-size: 0.8rem;
}

.visits {
  color: #6f42c1;
  font-weight: 500;
}

.last-visit {
  color: #6c757d;
}

.status {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
}

.status.active {
  background: rgba(25, 135, 84, 0.1);
  color: #198754;
}

.status.new {
  background: rgba(13, 110, 253, 0.1);
  color: #0d6efd;
}

.status.inactive {
  background: rgba(108, 117, 125, 0.1);
  color: #6c757d;
}

.status.regular {
  background: rgba(255, 193, 7, 0.1);
  color: #ffc107;
}

.client-actions {
  display: flex;
  gap: 8px;
}

.client-actions .btn {
  border-radius: 8px;
  padding: 6px 10px;
}

.pagination-container {
  margin-top: 30px;
}

@media (max-width: 768px) {
  .clients-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .search-control {
    width: 100%;
  }
  
  .client-item {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
    text-align: center;
  }
  
  .client-avatar {
    align-self: center;
    margin-right: 0;
  }
  
  .client-contact {
    flex-direction: column;
    gap: 5px;
  }
  
  .client-meta {
    flex-direction: column;
    gap: 8px;
  }
  
  .client-actions {
    justify-content: center;
  }
  
  .stat-card {
    flex-direction: column;
    text-align: center;
  }
  
  .stat-icon {
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style>