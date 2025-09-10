<template>
  <div class="employee-manager">
    <!-- Header -->
    <div class="manager-header">
      <div class="header-content">
        <h1>Gestión de Empleados</h1>
        <p class="text-muted">Administra el equipo de tus peluquerías</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" @click="openCreateModal">
          <i class="fas fa-user-plus me-2"></i> Nuevo Empleado
        </button>
      </div>
    </div>

    <!-- Statistics Overview -->
    <div class="stats-overview">
      <div class="row">
        <div class="col-xl-2 col-md-4 col-6 mb-3">
          <div class="stat-item">
            <div class="stat-value">{{ totalEmployees }}</div>
            <div class="stat-label">Total</div>
          </div>
        </div>
        <div class="col-xl-2 col-md-4 col-6 mb-3">
          <div class="stat-item">
            <div class="stat-value">{{ activeEmployees }}</div>
            <div class="stat-label">Activos</div>
          </div>
        </div>
        <div class="col-xl-2 col-md-4 col-6 mb-3">
          <div class="stat-item">
            <div class="stat-value">{{ onVacation }}</div>
            <div class="stat-label">De Vacaciones</div>
          </div>
        </div>
        <div class="col-xl-2 col-md-4 col-6 mb-3">
          <div class="stat-item">
            <div class="stat-value">{{ averageRating }}</div>
            <div class="stat-label">Rating Promedio</div>
          </div>
        </div>
        <div class="col-xl-2 col-md-4 col-6 mb-3">
          <div class="stat-item">
            <div class="stat-value">{{ totalAppointments }}</div>
            <div class="stat-label">Turnos este Mes</div>
          </div>
        </div>
        <div class="col-xl-2 col-md-4 col-6 mb-3">
          <div class="stat-item">
            <div class="stat-value">${{ totalRevenue.toLocaleString() }}</div>
            <div class="stat-label">Ingresos Generados</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters and Controls -->
    <div class="filters-section">
      <div class="row">
        <div class="col-md-4">
          <div class="search-box">
            <i class="fas fa-search"></i>
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="Buscar empleados..." 
              class="form-control"
            >
          </div>
        </div>
        <div class="col-md-8">
          <div class="filter-controls">
            <select v-model="salonFilter" class="form-select me-2">
              <option value="">Todas las peluquerías</option>
              <option v-for="salon in salons" :key="salon.id" :value="salon.id">
                {{ salon.name }}
              </option>
            </select>
            <select v-model="statusFilter" class="form-select me-2">
              <option value="">Todos los estados</option>
              <option value="active">Activos</option>
              <option value="inactive">Inactivos</option>
              <option value="vacation">De vacaciones</option>
            </select>
            <select v-model="sortBy" class="form-select">
              <option value="name">Ordenar por nombre</option>
              <option value="rating">Ordenar por rating</option>
              <option value="appointments">Ordenar por turnos</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Employees Table -->
    <div class="employees-table">
      <div class="card">
        <div class="card-body">
          <div v-if="loading" class="text-center py-5">
            <LoadingSpinner message="Cargando empleados..." />
          </div>

          <div v-else-if="filteredEmployees.length === 0" class="text-center py-5">
            <i class="fas fa-users-slash fa-3x text-muted mb-3"></i>
            <h5>No se encontraron empleados</h5>
            <p class="text-muted">Intenta ajustar tus filtros de búsqueda</p>
          </div>

          <div v-else class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>Empleado</th>
                  <th>Peluquería</th>
                  <th>Especialidad</th>
                  <th>Rating</th>
                  <th>Turnos</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="employee in filteredEmployees" :key="employee.id">
                  <td>
                    <div class="employee-info">
                      <img :src="employee.avatar" :alt="employee.name" class="employee-avatar">
                      <div>
                        <div class="employee-name">{{ employee.name }}</div>
                        <div class="employee-email">{{ employee.email }}</div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <span class="salon-badge">{{ employee.salon }}</span>
                  </td>
                  <td>
                    <div class="specialties">
                      <span v-for="specialty in employee.specialties.slice(0, 2)" 
                            :key="specialty" class="specialty-tag">
                        {{ specialty }}
                      </span>
                      <span v-if="employee.specialties.length > 2" class="specialty-more">
                        +{{ employee.specialties.length - 2 }}
                      </span>
                    </div>
                  </td>
                  <td>
                    <div class="rating">
                      <i class="fas fa-star text-warning"></i>
                      <span>{{ employee.rating }}</span>
                      <small class="text-muted">({{ employee.totalReviews }})</small>
                    </div>
                  </td>
                  <td>
                    <div class="appointments-count">
                      {{ employee.appointmentsThisMonth }}
                    </div>
                  </td>
                  <td>
                    <span class="status-badge" :class="employee.status">
                      {{ statusLabels[employee.status] }}
                    </span>
                  </td>
                  <td>
                    <div class="action-buttons">
                      <button class="btn btn-sm btn-outline-primary" 
                              @click="openEditModal(employee)"
                              title="Editar">
                        <i class="fas fa-edit"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-info" 
                              @click="viewSchedule(employee.id)"
                              title="Ver horario">
                        <i class="fas fa-calendar-alt"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-warning" 
                              @click="viewPerformance(employee.id)"
                              title="Rendimiento">
                        <i class="fas fa-chart-line"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-danger" 
                              @click="confirmDelete(employee.id)"
                              title="Eliminar">
                        <i class="fas fa-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="pagination-section" v-if="filteredEmployees.length > 0">
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
      <EmployeeForm 
        :employee="editingEmployee" 
        :salons="salons"
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
        <h5>¿Estás seguro de que quieres eliminar este empleado?</h5>
        <p class="text-muted">
          Esta acción afectará todos los turnos asignados a este empleado.
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
import { useRouter } from 'vue-router';
import ModalDialog from '@/components/common/ModalDialog.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import EmployeeForm from '@/components/owner/EmployeeForm.vue';

export default {
  name: 'EmployeeManager',
  components: {
    ModalDialog,
    LoadingSpinner,
    EmployeeForm
  },
  setup() {
    const router = useRouter();
    
    const loading = ref(true);
    const showModal = ref(false);
    const showDeleteModal = ref(false);
    const editingEmployee = ref(null);
    const deletingEmployeeId = ref(null);
    const searchQuery = ref('');
    const salonFilter = ref('');
    const statusFilter = ref('');
    const sortBy = ref('name');
    const currentPage = ref(1);
    const itemsPerPage = ref(10);
    
    const salons = ref([
      { id: 1, name: "Estilo Urbano" },
      { id: 2, name: "Corte Perfecto" },
      { id: 3, name: "Belleza Integral" }
    ]);
    
    const statusLabels = {
      'active': 'Activo',
      'inactive': 'Inactivo',
      'vacation': 'Vacaciones'
    };
    
    const employees = ref([
      {
        id: 1,
        name: "Carlos Rodríguez",
        email: "carlos@stylecut.com",
        avatar: "https://ui-avatars.com/api/?name=Carlos+Rodriguez&background=6f42c1&color=fff",
        salon: "Estilo Urbano",
        salonId: 1,
        specialties: ["Corte de cabello", "Barba", "Tintura"],
        rating: 4.8,
        totalReviews: 124,
        appointmentsThisMonth: 45,
        status: "active",
        phone: "+54 11 1234-5678",
        hireDate: "2023-01-15",
        salary: 45000
      },
      {
        id: 2,
        name: "Ana Martínez",
        email: "ana@stylecut.com",
        avatar: "https://ui-avatars.com/api/?name=Ana+Martinez&background=d63384&color=fff",
        salon: "Corte Perfecto",
        salonId: 2,
        specialties: ["Corte de cabello", "Alisado", "Peinados"],
        rating: 4.6,
        totalReviews: 89,
        appointmentsThisMonth: 38,
        status: "active",
        phone: "+54 11 2345-6789",
        hireDate: "2023-03-10",
        salary: 42000
      },
      {
        id: 3,
        name: "Miguel Ángel",
        email: "miguel@stylecut.com",
        avatar: "https://ui-avatars.com/api/?name=Miguel+Angel&background=20c997&color=fff",
        salon: "Belleza Integral",
        salonId: 3,
        specialties: ["Manicura", "Pedicura", "Uñas acrílicas"],
        rating: 4.9,
        totalReviews: 156,
        appointmentsThisMonth: 52,
        status: "vacation",
        phone: "+54 11 3456-7890",
        hireDate: "2022-11-20",
        salary: 38000
      }
    ]);
    
    const filteredEmployees = computed(() => {
      let filtered = employees.value;
      
      // Filtrar por búsqueda
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        filtered = filtered.filter(employee => 
          employee.name.toLowerCase().includes(query) ||
          employee.email.toLowerCase().includes(query) ||
          employee.phone.toLowerCase().includes(query)
        );
      }
      
      // Filtrar por peluquería
      if (salonFilter.value) {
        filtered = filtered.filter(employee => employee.salonId.toString() === salonFilter.value);
      }
      
      // Filtrar por estado
      if (statusFilter.value) {
        filtered = filtered.filter(employee => employee.status === statusFilter.value);
      }
      
      // Ordenar
      switch (sortBy.value) {
        case 'name':
          filtered.sort((a, b) => a.name.localeCompare(b.name));
          break;
        case 'rating':
          filtered.sort((a, b) => b.rating - a.rating);
          break;
        case 'appointments':
          filtered.sort((a, b) => b.appointmentsThisMonth - a.appointmentsThisMonth);
          break;
      }
      
      return filtered;
    });
    
    const totalEmployees = computed(() => employees.value.length);
    const activeEmployees = computed(() => employees.value.filter(e => e.status === 'active').length);
    const onVacation = computed(() => employees.value.filter(e => e.status === 'vacation').length);
    const averageRating = computed(() => {
      const total = employees.value.reduce((sum, emp) => sum + emp.rating, 0);
      return (total / employees.value.length).toFixed(1);
    });
    const totalAppointments = computed(() => 
      employees.value.reduce((sum, emp) => sum + emp.appointmentsThisMonth, 0)
    );
    const totalRevenue = computed(() => 
      employees.value.reduce((sum, emp) => sum + (emp.appointmentsThisMonth * 500), 0)
    );
    
    const totalPages = computed(() => Math.ceil(filteredEmployees.value.length / itemsPerPage.value));
    
    const paginatedEmployees = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage.value;
      const end = start + itemsPerPage.value;
      return filteredEmployees.value.slice(start, end);
    });
    
    const modalTitle = computed(() => 
      editingEmployee.value ? 'Editar Empleado' : 'Nuevo Empleado'
    );
    
    const loadEmployees = async () => {
      try {
        loading.value = true;
        // Simular carga de datos
        await new Promise(resolve => setTimeout(resolve, 1000));
      } catch (error) {
        console.error('Error loading employees:', error);
      } finally {
        loading.value = false;
      }
    };
    
    const openCreateModal = () => {
      editingEmployee.value = null;
      showModal.value = true;
    };
    
    const openEditModal = (employee) => {
      editingEmployee.value = { ...employee };
      showModal.value = true;
    };
    
    const confirmDelete = (employeeId) => {
      deletingEmployeeId.value = employeeId;
      showDeleteModal.value = true;
    };
    
    const confirmDeleteAction = () => {
      employees.value = employees.value.filter(emp => emp.id !== deletingEmployeeId.value);
      showDeleteModal.value = false;
      deletingEmployeeId.value = null;
    };
    
    const handleSubmit = (employeeData) => {
      if (editingEmployee.value) {
        // Editar empleado existente
        const index = employees.value.findIndex(e => e.id === editingEmployee.value.id);
        if (index !== -1) {
          employees.value[index] = { ...employees.value[index], ...employeeData };
        }
      } else {
        // Crear nuevo empleado
        const newEmployee = {
          id: Math.max(...employees.value.map(e => e.id)) + 1,
          ...employeeData,
          rating: 0,
          totalReviews: 0,
          appointmentsThisMonth: 0,
          status: 'active'
        };
        employees.value.push(newEmployee);
      }
      
      showModal.value = false;
      editingEmployee.value = null;
    };
    
    const viewSchedule = (employeeId) => {
      router.push(`/owner/employee/${employeeId}/schedule`);
    };
    
    const viewPerformance = (employeeId) => {
      router.push(`/owner/employee/${employeeId}/performance`);
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
      loadEmployees();
    });
    
    return {
      loading,
      showModal,
      showDeleteModal,
      editingEmployee,
      searchQuery,
      salonFilter,
      statusFilter,
      sortBy,
      currentPage,
      salons,
      statusLabels,
      filteredEmployees: paginatedEmployees,
      totalEmployees,
      activeEmployees,
      onVacation,
      averageRating,
      totalAppointments,
      totalRevenue,
      totalPages,
      modalTitle,
      openCreateModal,
      openEditModal,
      confirmDelete,
      confirmDeleteAction,
      handleSubmit,
      viewSchedule,
      viewPerformance,
      goToPage,
      nextPage,
      prevPage
    };
  }
};
</script>

<style scoped>
.employee-manager {
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

.stats-overview {
  margin-bottom: 30px;
}

.stats-overview .row {
  margin: 0 -8px;
}

.stats-overview .col-xl-2 {
  padding: 0 8px;
}

.stat-item {
  text-align: center;
  padding: 15px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: 100%;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #6f42c1;
  margin-bottom: 5px;
}

.stat-label {
  color: #6c757d;
  font-weight: 500;
  font-size: 0.9rem;
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

.employees-table {
  margin-bottom: 30px;
}

.employee-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.employee-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.employee-name {
  font-weight: 600;
  color: #2c3e50;
}

.employee-email {
  font-size: 0.85rem;
  color: #6c757d;
}

.salon-badge {
  background: #e9ecef;
  color: #495057;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 500;
}

.specialties {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.specialty-tag {
  background: rgba(111, 66, 193, 0.1);
  color: #6f42c1;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.8rem;
}

.specialty-more {
  color: #6c757d;
  font-size: 0.8rem;
  font-style: italic;
}

.rating {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 600;
}

.rating small {
  font-size: 0.8rem;
}

.appointments-count {
  font-weight: 700;
  color: #6f42c1;
  font-size: 1.1rem;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.status-badge.active {
  background: #d1e7dd;
  color: #0f5132;
}

.status-badge.inactive {
  background: #f8d7da;
  color: #842029;
}

.status-badge.vacation {
  background: #fff3cd;
  color: #856404;
}

.action-buttons {
  display: flex;
  gap: 6px;
}

.action-buttons .btn {
  padding: 4px 8px;
  border-radius: 6px;
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

@media (max-width: 1200px) {
  .employees-table .table {
    font-size: 0.9rem;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }
}

@media (max-width: 992px) {
  .employee-manager {
    padding: 15px;
  }
  
  .manager-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .filter-controls {
    flex-direction: column;
    gap: 10px;
  }
  
  .stats-overview .col-xl-2 {
    margin-bottom: 16px;
  }
}

@media (max-width: 768px) {
  .employees-table {
    overflow-x: auto;
  }
  
  .table {
    min-width: 800px;
  }
  
  .filters-section {
    padding: 15px;
  }
  
  .stat-item {
    padding: 12px;
  }
  
  .stat-value {
    font-size: 1.3rem;
  }
}
</style>