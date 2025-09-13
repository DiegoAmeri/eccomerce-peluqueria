<template>
  <div class="favorites-view">
    <!-- Header -->
    <div class="favorites-header">
      <div class="header-content">
        <h1>Mis Favoritos</h1>
        <p class="text-muted">Tus peluquerías y servicios favoritos</p>
      </div>
    </div>

    <!-- Tabs -->
    <div class="favorites-tabs">
      <ul class="nav nav-tabs" role="tablist">
        <li class="nav-item" role="presentation">
          <button class="nav-link" :class="{ active: activeTab === 'salons' }" 
                  @click="activeTab = 'salons'">
            <i class="fas fa-store me-2"></i>Peluquerías
            <span class="badge bg-primary ms-2">{{ favoriteSalons.length }}</span>
          </button>
        </li>
        <li class="nav-item" role="presentation">
          <button class="nav-link" :class="{ active: activeTab === 'services' }" 
                  @click="activeTab = 'services'">
            <i class="fas fa-scissors me-2"></i>Servicios
            <span class="badge bg-primary ms-2">{{ favoriteServices.length }}</span>
          </button>
        </li>
        <li class="nav-item" role="presentation">
          <button class="nav-link" :class="{ active: activeTab === 'professionals' }" 
                  @click="activeTab = 'professionals'">
            <i class="fas fa-user me-2"></i>Profesionales
            <span class="badge bg-primary ms-2">{{ favoriteProfessionals.length }}</span>
          </button>
        </li>
      </ul>
    </div>

    <!-- Content -->
    <div class="favorites-content">
      <!-- Salons Tab -->
      <div v-if="activeTab === 'salons'" class="tab-content">
        <div v-if="loading" class="text-center py-5">
          <LoadingSpinner message="Cargando peluquerías favoritas..." />
        </div>

        <div v-else-if="favoriteSalons.length === 0" class="text-center py-5">
          <i class="fas fa-heart fa-3x text-muted mb-3"></i>
          <h5>No tienes peluquerías favoritas</h5>
          <p class="text-muted">Agrega peluquerías a tus favoritos para verlas aquí</p>
          <router-link to="/salons" class="btn btn-primary">
            <i class="fas fa-store me-2"></i>Explorar Peluquerías
          </router-link>
        </div>

        <div v-else class="row">
          <div class="col-xl-4 col-lg-6 mb-4" v-for="salon in favoriteSalons" :key="salon.id">
            <SalonCard :salon="salon" @favorite-removed="removeSalonFromFavorites" />
          </div>
        </div>
      </div>

      <!-- Services Tab -->
      <div v-if="activeTab === 'services'" class="tab-content">
        <div v-if="loading" class="text-center py-5">
          <LoadingSpinner message="Cargando servicios favoritos..." />
        </div>

        <div v-else-if="favoriteServices.length === 0" class="text-center py-5">
          <i class="fas fa-scissors fa-3x text-muted mb-3"></i>
          <h5>No tienes servicios favoritos</h5>
          <p class="text-muted">Agrega servicios a tus favoritos para verlos aquí</p>
        </div>

        <div v-else class="row">
          <div class="col-lg-6 mb-4" v-for="service in favoriteServices" :key="service.id">
            <ServiceCard :service="service" @favorite-removed="removeServiceFromFavorites" />
          </div>
        </div>
      </div>

      <!-- Professionals Tab -->
      <div v-if="activeTab === 'professionals'" class="tab-content">
        <div v-if="loading" class="text-center py-5">
          <LoadingSpinner message="Cargando profesionales favoritos..." />
        </div>

        <div v-else-if="favoriteProfessionals.length === 0" class="text-center py-5">
          <i class="fas fa-user fa-3x text-muted mb-3"></i>
          <h5>No tienes profesionales favoritos</h5>
          <p class="text-muted">Agrega profesionales a tus favoritos para verlos aquí</p>
        </div>

        <div v-else class="row">
          <div class="col-xl-3 col-lg-4 col-md-6 mb-4" v-for="professional in favoriteProfessionals" :key="professional.id">
            <ProfessionalCard :professional="professional" @favorite-removed="removeProfessionalFromFavorites" />
          </div>
        </div>
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
import { ref, onMounted } from 'vue';
import { useStore } from 'vuex';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import NotificationToast from '@/components/common/NotificationToast.vue';
import SalonCard from '@/components/client/SalonCard.vue';
import ServiceCard from '@/components/client/ServiceCard.vue';
import ProfessionalCard from '@/components/client/ProfessionalCard.vue';

export default {
  name: 'FavoritesView',
  components: {
    LoadingSpinner,
    NotificationToast,
    SalonCard,
    ServiceCard,
    ProfessionalCard
  },
  setup() {
    const store = useStore();
    
    const activeTab = ref('salons');
    const loading = ref(true);
    const showToast = ref(false);
    const toastType = ref('success');
    const toastTitle = ref('');
    const toastMessage = ref('');
    
    // Datos de ejemplo
    const favoriteSalons = ref([
      {
        id: 1,
        name: "Estilo Urbano",
        image: "https://images.unsplash.com/photo-1585747861815-0468562f7d4f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        address: "Av. Siempre Viva 742",
        rating: 4.8,
        reviews: 124,
        category: "Barbería",
        isFavorite: true
      },
      {
        id: 2,
        name: "Corte Perfecto",
        image: "https://images.unsplash.com/photo-1562322140-8baeececf3df?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        address: "Calle Falsa 123",
        rating: 4.5,
        reviews: 89,
        category: "Peluquería",
        isFavorite: true
      }
    ]);
    
    const favoriteServices = ref([
      {
        id: 1,
        name: "Corte de Cabello Premium",
        description: "Corte moderno con técnicas avanzadas",
        price: 600,
        duration: 45,
        category: "Corte",
        salon: "Estilo Urbano",
        isFavorite: true
      },
      {
        id: 2,
        name: "Coloración Completa",
        description: "Tinte completo con productos premium",
        price: 1200,
        duration: 120,
        category: "Color",
        salon: "Corte Perfecto",
        isFavorite: true
      }
    ]);
    
    const favoriteProfessionals = ref([
      {
        id: 1,
        name: "Carlos Rodríguez",
        specialty: "Especialista en cortes modernos",
        rating: 4.8,
        reviews: 124,
        salon: "Estilo Urbano",
        avatar: "https://ui-avatars.com/api/?name=Carlos+Rodriguez&background=6f42c1&color=fff",
        isFavorite: true
      },
      {
        id: 2,
        name: "Ana Martínez",
        specialty: "Colorista profesional",
        rating: 4.9,
        reviews: 89,
        salon: "Corte Perfecto",
        avatar: "https://ui-avatars.com/api/?name=Ana+Martinez&background=d63384&color=fff",
        isFavorite: true
      }
    ]);
    
    const loadFavorites = async () => {
      try {
        loading.value = true;
        // Simular carga de datos
        await new Promise(resolve => setTimeout(resolve, 1000));
      } catch (error) {
        console.error('Error loading favorites:', error);
      } finally {
        loading.value = false;
      }
    };
    
    const removeSalonFromFavorites = (salonId) => {
      favoriteSalons.value = favoriteSalons.value.filter(salon => salon.id !== salonId);
      showToastMessage('success', 'Eliminado de favoritos', 'Peluquería eliminada de tus favoritos');
    };
    
    const removeServiceFromFavorites = (serviceId) => {
      favoriteServices.value = favoriteServices.value.filter(service => service.id !== serviceId);
      showToastMessage('success', 'Eliminado de favoritos', 'Servicio eliminado de tus favoritos');
    };
    
    const removeProfessionalFromFavorites = (professionalId) => {
      favoriteProfessionals.value = favoriteProfessionals.value.filter(pro => pro.id !== professionalId);
      showToastMessage('success', 'Eliminado de favoritos', 'Profesional eliminado de tus favoritos');
    };
    
    const showToastMessage = (type, title, message) => {
      toastType.value = type;
      toastTitle.value = title;
      toastMessage.value = message;
      showToast.value = true;
    };
    
    onMounted(() => {
      loadFavorites();
    });
    
    return {
      activeTab,
      loading,
      showToast,
      toastType,
      toastTitle,
      toastMessage,
      favoriteSalons,
      favoriteServices,
      favoriteProfessionals,
      removeSalonFromFavorites,
      removeServiceFromFavorites,
      removeProfessionalFromFavorites
    };
  }
};
</script>

<style scoped>
.favorites-view {
  padding: 20px;
}

.favorites-header {
  margin-bottom: 30px;
}

.favorites-header h1 {
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 5px;
}

.favorites-tabs {
  margin-bottom: 30px;
}

.nav-tabs {
  border-bottom: 2px solid #dee2e6;
}

.nav-tabs .nav-link {
  border: none;
  color: #6c757d;
  font-weight: 500;
  padding: 12px 20px;
  transition: all 0.3s ease;
}

.nav-tabs .nav-link:hover {
  color: #6f42c1;
  border: none;
}

.nav-tabs .nav-link.active {
  color: #6f42c1;
  background: none;
  border: none;
  border-bottom: 3px solid #6f42c1;
}

.nav-tabs .badge {
  font-size: 0.7rem;
  padding: 4px 8px;
}

.tab-content {
  min-height: 400px;
}

@media (max-width: 768px) {
  .favorites-view {
    padding: 15px;
  }
  
  .nav-tabs .nav-link {
    padding: 8px 12px;
    font-size: 0.9rem;
  }
  
  .nav-tabs .badge {
    font-size: 0.6rem;
    padding: 2px 6px;
  }
}
</style>