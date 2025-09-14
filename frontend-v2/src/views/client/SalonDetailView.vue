<template>
  <div class="salon-detail-view">
    <!-- Loading State -->
    <div v-if="loading" class="text-center py-5">
      <LoadingSpinner :message="'Cargando información del salón...'" />
    </div>

    <!-- Salon Details -->
    <div v-else-if="salon" class="salon-detail-content">
      <!-- Header with Image -->
      <div class="salon-header">
        <div class="salon-hero-image">
          <img :src="salon.image" :alt="salon.name" class="hero-img">
          <div class="salon-hero-overlay">
            <div class="container">
              <div class="row align-items-center">
                <div class="col-md-8">
                  <h1 class="salon-name">{{ salon.name }}</h1>
                  <div class="salon-meta">
                    <div class="rating-badge">
                      <i class="fas fa-star"></i>
                      <span>{{ salon.rating }}</span>
                      <span class="reviews-count">({{ salon.reviews }} reseñas)</span>
                    </div>
                    <span class="category-badge">{{ getCategoryName(salon.category) }}</span>
                    <span class="distance-badge">
                      <i class="fas fa-map-marker-alt"></i>
                      {{ salon.distance }}
                    </span>
                  </div>
                </div>
                <div class="col-md-4 text-md-end">
                  <button class="btn btn-light me-2 favorite-btn" @click="toggleFavorite">
                    <i class="fas" :class="salon.isFavorite ? 'fa-heart text-danger' : 'fa-heart'"></i>
                  </button>
                  <router-link :to="`/salons/${salon.id}/booking`" class="btn btn-primary">
                    <i class="fas fa-calendar-plus me-2"></i>Reservar
                  </router-link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Main Content -->
      <div class="container mt-4">
        <div class="row">
          <!-- Left Column - Salon Info -->
          <div class="col-lg-8">
            <!-- About Section -->
            <div class="card mb-4">
              <div class="card-body">
                <h5 class="card-title">Acerca de nosotros</h5>
                <p class="card-text">{{ salon.description }}</p>
                
                <div class="salon-features">
                  <div class="feature-item" v-for="feature in salon.features" :key="feature">
                    <i class="fas fa-check text-success"></i>
                    <span>{{ feature }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Services Section -->
            <div class="card mb-4">
              <div class="card-body">
                <h5 class="card-title">Servicios</h5>
                <p class="text-muted">Precios aproximados - pueden variar según el estilista</p>
                
                <div class="services-list">
                  <div class="service-item" v-for="service in salon.services" :key="service.name">
                    <div class="service-info">
                      <h6 class="service-name">{{ service.name }}</h6>
                      <p class="service-duration text-muted">
                        <i class="fas fa-clock me-1"></i>{{ service.duration }} min
                      </p>
                    </div>
                    <div class="service-price">
                      {{ service.price }}
                    </div>
                    <div class="service-action">
                      <button class="btn btn-outline-primary btn-sm" @click="selectService(service)">
                        <i class="fas fa-plus me-1"></i>Seleccionar
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Stylists Section -->
            <div class="card mb-4" v-if="salon.stylists && salon.stylists.length > 0">
              <div class="card-body">
                <h5 class="card-title">Nuestros estilistas</h5>
                <p class="text-muted">Profesionales altamente capacitados</p>
                
                <div class="stylists-list">
                  <div class="stylist-item" v-for="stylist in salon.stylists" :key="stylist.id">
                    <img :src="stylist.image" :alt="stylist.name" class="stylist-image">
                    <div class="stylist-info">
                      <h6 class="stylist-name">{{ stylist.name }}</h6>
                      <p class="stylist-specialty text-muted">{{ stylist.specialty }}</p>
                      <div class="stylist-rating">
                        <i class="fas fa-star text-warning"></i>
                        <span>{{ stylist.rating }}</span>
                        <span class="reviews-count">({{ stylist.reviews }})</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Reviews Section -->
            <div class="card mb-4">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-4">
                  <h5 class="card-title mb-0">Reseñas</h5>
                  <button class="btn btn-outline-primary btn-sm" @click="showAllReviews = !showAllReviews">
                    {{ showAllReviews ? 'Ver menos' : 'Ver todas' }}
                  </button>
                </div>
                
                <div class="reviews-summary mb-4">
                  <div class="row">
                    <div class="col-md-3 text-center">
                      <div class="average-rating">{{ salon.rating }}</div>
                      <div class="stars">
                        <i class="fas fa-star" v-for="star in 5" :key="star" 
                          :class="star <= Math.floor(salon.rating) ? 'text-warning' : 'text-muted'"></i>
                      </div>
                      <div class="total-reviews">{{ salon.reviews }} reseñas</div>
                    </div>
                    <div class="col-md-9">
                      <div class="rating-bars">
                        <div class="rating-bar" v-for="n in 5" :key="n">
                          <div class="rating-level">
                            <span>{{ 6 - n }}</span>
                            <i class="fas fa-star text-warning"></i>
                          </div>
                          <div class="progress">
                            <div class="progress-bar bg-warning" 
                                 :style="{ width: getRatingPercentage(6 - n) + '%' }"></div>
                          </div>
                          <div class="rating-count">
                            {{ getRatingCount(6 - n) }}
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div class="reviews-list">
                  <div class="review-item" v-for="review in visibleReviews" :key="review.id">
                    <div class="review-header">
                      <div class="reviewer-info">
                        <img :src="review.avatar" :alt="review.author" class="reviewer-avatar">
                        <div>
                          <h6 class="reviewer-name">{{ review.author }}</h6>
                          <div class="review-rating">
                            <i class="fas fa-star" v-for="star in 5" :key="star" 
                              :class="star <= review.rating ? 'text-warning' : 'text-muted'"></i>
                          </div>
                        </div>
                      </div>
                      <span class="review-date">{{ formatDate(review.date) }}</span>
                    </div>
                    <p class="review-content">{{ review.content }}</p>
                  </div>
                </div>
                
                <div class="text-center mt-3" v-if="!showAllReviews && salon.reviews > 3">
                  <button class="btn btn-outline-primary" @click="showAllReviews = true">
                    Ver más reseñas
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Right Column - Booking & Info -->
          <div class="col-lg-4">
            <!-- Booking Card -->
            <div class="card mb-4 sticky-top" style="top: 20px;">
              <div class="card-body">
                <h5 class="card-title">Reservar cita</h5>
                
                <div class="selected-services mb-3" v-if="selectedServices.length > 0">
                  <h6>Servicios seleccionados</h6>
                  <div class="selected-service-item" v-for="service in selectedServices" :key="service.name">
                    <div class="d-flex justify-content-between">
                      <span>{{ service.name }}</span>
                      <span>{{ service.price }}</span>
                    </div>
                    <small class="text-muted">{{ service.duration }} min</small>
                  </div>
                  <hr>
                  <div class="d-flex justify-content-between fw-bold">
                    <span>Total:</span>
                    <span>{{ totalPrice }}</span>
                  </div>
                  <div class="d-flex justify-content-between text-muted">
                    <span>Tiempo total:</span>
                    <span>{{ totalDuration }} min</span>
                  </div>
                </div>
                
                <router-link 
                  :to="`/salons/${salon.id}/booking`" 
                  class="btn btn-primary w-100"
                  :class="{ disabled: selectedServices.length === 0 }"
                >
                  <i class="fas fa-calendar-plus me-2"></i>
                  {{ selectedServices.length > 0 ? 'Continuar con la reserva' : 'Selecciona un servicio' }}
                </router-link>
                
                <div class="text-center mt-3">
                  <small class="text-muted">Reserva fácil y rápida en pocos pasos</small>
                </div>
              </div>
            </div>

            <!-- Contact & Hours -->
            <div class="card mb-4">
              <div class="card-body">
                <h5 class="card-title">Información de contacto</h5>
                
                <div class="contact-item mb-3">
                  <div class="d-flex align-items-center">
                    <i class="fas fa-map-marker-alt text-primary me-3"></i>
                    <div>
                      <div class="fw-medium">Dirección</div>
                      <div class="text-muted">{{ salon.address }}</div>
                    </div>
                  </div>
                </div>
                
                <div class="contact-item mb-3">
                  <div class="d-flex align-items-center">
                    <i class="fas fa-phone text-primary me-3"></i>
                    <div>
                      <div class="fw-medium">Teléfono</div>
                      <div class="text-muted">{{ salon.phone }}</div>
                    </div>
                  </div>
                </div>
                
                <div class="contact-item mb-3">
                  <div class="d-flex align-items-center">
                    <i class="fas fa-envelope text-primary me-3"></i>
                    <div>
                      <div class="fw-medium">Email</div>
                      <div class="text-muted">{{ salon.email }}</div>
                    </div>
                  </div>
                </div>
                
                <div class="contact-item">
                  <div class="d-flex align-items-center">
                    <i class="fas fa-globe text-primary me-3"></i>
                    <div>
                      <div class="fw-medium">Sitio web</div>
                      <a :href="salon.website" target="_blank" class="text-muted">{{ salon.website }}</a>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Hours -->
            <div class="card mb-4">
              <div class="card-body">
                <h5 class="card-title">Horario de atención</h5>
                
                <div class="hours-list">
                  <div class="hour-item d-flex justify-content-between" v-for="(hours, day) in salon.hours" :key="day">
                    <span class="fw-medium">{{ getDayName(day) }}</span>
                    <span class="text-muted">{{ hours }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Location Map -->
            <div class="card">
              <div class="card-body">
                <h5 class="card-title">Ubicación</h5>
                <div class="map-placeholder">
                  <i class="fas fa-map-marked-alt fa-3x text-muted mb-3"></i>
                  <p class="text-muted">Mapa interactivo de ubicación</p>
                  <small class="text-muted">(Integración con Google Maps)</small>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Not Found State -->
    <div v-else class="text-center py-5">
      <i class="fas fa-store-slash fa-3x text-muted mb-3"></i>
      <h4>Salón no encontrado</h4>
      <p class="text-muted">El salón que buscas no existe o no está disponible</p>
      <router-link to="/salons" class="btn btn-primary mt-2">
        <i class="fas fa-arrow-left me-2"></i>Volver a peluquerías
      </router-link>
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
import { useRoute } from 'vue-router';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import NotificationToast from '@/components/common/NotificationToast.vue';

export default {
  name: 'SalonDetailView',
  components: {
    LoadingSpinner,
    NotificationToast
  },
  setup() {
    const route = useRoute();
    const salonId = route.params.id;
    
    const loading = ref(true);
    const salon = ref(null);
    const showAllReviews = ref(false);
    const showToast = ref(false);
    const toastType = ref('success');
    const toastTitle = ref('');
    const toastMessage = ref('');
    const selectedServices = ref([]);

    // Datos de ejemplo
    const salonsData = [
      {
        id: 1,
        name: "Estilo Urbano",
        image: "https://images.unsplash.com/photo-1585747861815-0468562f7d4f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1200&q=80",
        description: "Especialistas en cortes modernos y tendencias urbanas. Nuestro equipo de estilistas profesionales está dedicado a brindarte el mejor servicio con técnicas de vanguardia y productos de alta calidad. Con más de 10 años de experiencia, hemos transformado miles de looks y creado sonrisas.",
        rating: 4.8,
        reviews: 124,
        address: "Av. Siempre Viva 742, Ciudad",
        category: "barberia",
        services: [
          { name: "Corte de cabello", price: "$25.00", duration: 30 },
          { name: "Arreglo de barba", price: "$15.00", duration: 20 },
          { name: "Corte y barba", price: "$35.00", duration: 45 },
          { name: "Afeitado clásico", price: "$20.00", duration: 25 },
          { name: "Tinte de barba", price: "$18.00", duration: 30 },
          { name: "Mascarilla facial", price: "$15.00", duration: 15 }
        ],
        openTime: "09:00",
        closeTime: "20:00",
        distance: "1.2 km",
        phone: "+1 234-567-8901",
        email: "info@estilourbano.com",
        website: "https://estilourbano.com",
        hours: {
          monday: "9:00 AM - 8:00 PM",
          tuesday: "9:00 AM - 8:00 PM",
          wednesday: "9:00 AM - 8:00 PM",
          thursday: "9:00 AM - 8:00 PM",
          friday: "9:00 AM - 9:00 PM",
          saturday: "10:00 AM - 6:00 PM",
          sunday: "Cerrado"
        },
        features: [
          "Wi-Fi gratuito",
          "Estacionamiento",
          "Aceptamos tarjetas",
          "Ambiente climatizado",
          "Productos a la venta",
          "Servicio a domicilio"
        ],
        stylists: [
          {
            id: 1,
            name: "Carlos Rodríguez",
            specialty: "Cortes modernos y barbas",
            rating: 4.9,
            reviews: 78,
            image: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=200&q=80"
          },
          {
            id: 2,
            name: "Miguel Ángel",
            specialty: "Afeitado clásico",
            rating: 4.8,
            reviews: 56,
            image: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=200&q=80"
          }
        ],
        reviews: [
          {
            id: 1,
            author: "Juan Pérez",
            rating: 5,
            content: "Excelente servicio! Carlos es un profesional increíble. Mi barba nunca se había visto tan bien. Definitivamente volveré.",
            date: "2023-11-15",
            avatar: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=200&q=80"
          },
          {
            id: 2,
            author: "María García",
            rating: 4,
            content: "Muy buen lugar, atención de primera. El único detalle fue la espera, llegué a mi hora pero tuve que esperar unos 15 minutos extra.",
            date: "2023-11-10",
            avatar: "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=200&q=80"
          },
          {
            id: 3,
            author: "Roberto Silva",
            rating: 5,
            content: "Llevo yendo más de un año y siempre recibo un servicio excelente. Los recomiendo 100%.",
            date: "2023-11-05",
            avatar: "https://images.unsplash.com/photo-1633332755192-727a05c4013d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=200&q=80"
          },
          {
            id: 4,
            author: "Ana Martínez",
            rating: 4,
            content: "Buen servicio, precios justos. El lugar es muy acogedor y limpio.",
            date: "2023-10-28",
            avatar: "https://images.unsplash.com/photo-1623582854588-d60de57fa33f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=200&q=80"
          },
          {
            id: 5,
            author: "Luis Fernández",
            rating: 5,
            content: "Simplemente perfecto. Miguel es un artista con la navaja, el mejor afeitado que he tenido en mi vida.",
            date: "2023-10-20",
            avatar: "https://images.unsplash.com/photo-1599566150163-29194dcaad36?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=200&q=80"
          }
        ],
        isFavorite: true
      }
    ];

    const getCategoryName = (category) => {
      const categories = {
        'peluqueria': 'Peluquería',
        'barberia': 'Barbería',
        'estetica': 'Estética',
        'unas': 'Uñas'
      };
      return categories[category] || category;
    };

    const getDayName = (day) => {
      const days = {
        'monday': 'Lunes',
        'tuesday': 'Martes',
        'wednesday': 'Miércoles',
        'thursday': 'Jueves',
        'friday': 'Viernes',
        'saturday': 'Sábado',
        'sunday': 'Domingo'
      };
      return days[day] || day;
    };

    const visibleReviews = computed(() => {
      return showAllReviews.value ? salon.value.reviews : salon.value.reviews.slice(0, 3);
    });

    const totalPrice = computed(() => {
      return selectedServices.value.reduce((total, service) => {
        return total + parseFloat(service.price.replace('$', ''));
      }, 0).toFixed(2);
    });

    const totalDuration = computed(() => {
      return selectedServices.value.reduce((total, service) => {
        return total + service.duration;
      }, 0);
    });

    const getRatingPercentage = (rating) => {
      if (!salon.value) return 0;
      const count = salon.value.reviews.filter(r => r.rating === rating).length;
      return (count / salon.value.reviews.length) * 100;
    };

    const getRatingCount = (rating) => {
      if (!salon.value) return 0;
      return salon.value.reviews.filter(r => r.rating === rating).length;
    };

    const formatDate = (dateString) => {
      const options = { year: 'numeric', month: 'long', day: 'numeric' };
      return new Date(dateString).toLocaleDateString('es-ES', options);
    };

    const toggleFavorite = () => {
      salon.value.isFavorite = !salon.value.isFavorite;
      
      showToast.value = true;
      toastType.value = 'success';
      toastTitle.value = salon.value.isFavorite ? 'Agregado a favoritos' : 'Eliminado de favoritos';
      toastMessage.value = salon.value.isFavorite 
        ? `${salon.value.name} ha sido agregado a tus favoritos.` 
        : `${salon.value.name} ha sido eliminado de tus favoritos.`;
    };

    const selectService = (service) => {
      const index = selectedServices.value.findIndex(s => s.name === service.name);
      
      if (index === -1) {
        selectedServices.value.push(service);
        
        showToast.value = true;
        toastType.value = 'success';
        toastTitle.value = 'Servicio agregado';
        toastMessage.value = `${service.name} ha sido agregado a tu selección.`;
      } else {
        selectedServices.value.splice(index, 1);
        
        showToast.value = true;
        toastType.value = 'info';
        toastTitle.value = 'Servicio eliminado';
        toastMessage.value = `${service.name} ha sido eliminado de tu selección.`;
      }
    };

    onMounted(() => {
      // Simular carga de datos
      setTimeout(() => {
        const foundSalon = salonsData.find(s => s.id === parseInt(salonId));
        if (foundSalon) {
          salon.value = foundSalon;
        }
        loading.value = false;
      }, 1000);
    });

    return {
      loading,
      salon,
      showAllReviews,
      showToast,
      toastType,
      toastTitle,
      toastMessage,
      selectedServices,
      visibleReviews,
      totalPrice,
      totalDuration,
      getCategoryName,
      getDayName,
      getRatingPercentage,
      getRatingCount,
      formatDate,
      toggleFavorite,
      selectService
    };
  }
};
</script>

<style scoped>
.salon-hero-image {
  position: relative;
  height: 350px;
  overflow: hidden;
}

.hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.salon-hero-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.8));
  color: white;
  padding: 2rem 0;
}

.salon-name {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.salon-meta {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.rating-badge {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  background: rgba(255,255,255,0.2);
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-weight: 500;
}

.reviews-count {
  font-size: 0.9rem;
  opacity: 0.9;
}

.category-badge {
  background: rgba(255,255,255,0.2);
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
}

.distance-badge {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.favorite-btn {
  border-radius: 50%;
  width: 45px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
}

.card-title {
  font-weight: 600;
  margin-bottom: 1rem;
}

.salon-features {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 0.5rem;
  margin-top: 1rem;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.services-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.service-item {
  display: flex;
  justify-content: between;
  align-items: center;
  padding: 1rem;
  border: 1px solid #f1f3f4;
  border-radius: 8px;
  transition: all 0.2s;
}

.service-item:hover {
  border-color: #0d6efd;
  background-color: #f8f9fa;
}

.service-info {
  flex: 1;
}

.service-name {
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.service-duration {
  font-size: 0.85rem;
  margin-bottom: 0;
}

.service-price {
  font-weight: 600;
  margin: 0 1rem;
  color: #0d6efd;
}

.service-action {
  flex-shrink: 0;
}

.stylists-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1rem;
}

.stylist-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border: 1px solid #f1f3f4;
  border-radius: 8px;
}

.stylist-image {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
}

.stylist-name {
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.stylist-specialty {
  font-size: 0.9rem;
  margin-bottom: 0.25rem;
}

.stylist-rating {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.9rem;
}

.reviews-summary {
  background-color: #f8f9fa;
  padding: 1.5rem;
  border-radius: 12px;
}

.average-rating {
  font-size: 3rem;
  font-weight: 700;
  color: #0d6efd;
}

.stars {
  margin: 0.5rem 0;
}

.total-reviews {
  color: #6c757d;
}

.rating-bars {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.rating-bar {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.rating-level {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  width: 40px;
}

.progress {
  flex: 1;
  height: 8px;
  background-color: #e9ecef;
}

.rating-count {
  width: 30px;
  text-align: right;
  color: #6c757d;
  font-size: 0.9rem;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.review-item {
  padding: 1.5rem;
  border: 1px solid #f1f3f4;
  border-radius: 12px;
}

.review-header {
  display: flex;
  justify-content: between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex: 1;
}

.reviewer-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.reviewer-name {
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.review-rating {
  display: flex;
  gap: 0.1rem;
}

.review-date {
  color: #6c757d;
  font-size: 0.9rem;
}

.review-content {
  margin-bottom: 0;
  line-height: 1.6;
}

.selected-services {
  background-color: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
}

.selected-service-item {
  padding: 0.5rem 0;
  border-bottom: 1px solid #e9ecef;
}

.selected-service-item:last-child {
  border-bottom: none;
}

.contact-item {
  padding: 0.75rem 0;
  border-bottom: 1px solid #f1f3f4;
}

.contact-item:last-child {
  border-bottom: none;
}

.hours-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.hour-item {
  padding: 0.5rem 0;
}

.map-placeholder {
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fa;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .salon-hero-image {
    height: 250px;
  }
  
  .salon-name {
    font-size: 1.8rem;
  }
  
  .salon-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .service-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .service-price {
    margin: 0;
  }
  
  .stylists-list {
    grid-template-columns: 1fr;
  }
  
  .reviews-summary .row {
    flex-direction: column;
    gap: 1rem;
  }
  
  .review-header {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .review-date {
    align-self: flex-start;
  }
}
</style>