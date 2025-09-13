<template>
  <div class="home-view">
    <!-- Navbar Pública -->
    <nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm">
      <div class="container">
        <router-link to="/" class="navbar-brand fw-bold text-primary">
          <i class="fas fa-scissors me-2"></i>StyleCut Pro
        </router-link>
        
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <router-link to="/" class="nav-link active">Inicio</router-link>
            </li>
            <li class="nav-item">
              <a href="#salons" class="nav-link">Peluquerías</a>
            </li>
            <li class="nav-item">
              <a href="#how-it-works" class="nav-link">Cómo Funciona</a>
            </li>
          </ul>
          
          <div class="d-flex">
                <router-link to="/auth/login" class="btn btn-outline-primary me-2">
                Iniciar Sesión
                </router-link>
                <router-link to="/auth/register" class="btn btn-primary">
                Registrarse
                </router-link>
            </div>
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero-section bg-primary text-white py-5">
      <div class="container py-5">
        <div class="row align-items-center">
          <div class="col-lg-6">
            <h1 class="display-4 fw-bold mb-4">Encuentra tu estilo perfecto</h1>
            <p class="lead mb-4">Descubre las mejores peluquerías y barberías cerca de ti. Reserva tu turno en segundos.</p>
            <div class="d-flex flex-wrap gap-2">
              <a href="#salons" class="btn btn-light btn-lg">Ver Peluquerías</a>
              <router-link to="/register" class="btn btn-outline-light btn-lg">Crear Cuenta</router-link>
            </div>
          </div>
          <div class="col-lg-6 text-center">
            <img src="https://images.unsplash.com/photo-1562322140-8baeececf3df?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80" 
                 alt="Salón de belleza" class="img-fluid rounded shadow">
          </div>
        </div>
      </div>
    </section>

    <!-- Cómo Funciona -->
    <section id="how-it-works" class="py-5 bg-light">
      <div class="container py-5">
        <div class="text-center mb-5">
          <h2 class="fw-bold">Cómo Funciona</h2>
          <p class="lead text-muted">Reserva tu turno en 3 simples pasos</p>
        </div>
        
        <div class="row">
          <div class="col-md-4 text-center mb-4">
            <div class="step-icon mx-auto bg-primary text-white rounded-circle d-flex align-items-center justify-content-center">
              <i class="fas fa-search fa-2x"></i>
            </div>
            <h4 class="mt-4">1. Encuentra</h4>
            <p class="text-muted">Busca peluquerías por ubicación, servicios o valoraciones.</p>
          </div>
          
          <div class="col-md-4 text-center mb-4">
            <div class="step-icon mx-auto bg-primary text-white rounded-circle d-flex align-items-center justify-content-center">
              <i class="fas fa-calendar-check fa-2x"></i>
            </div>
            <h4 class="mt-4">2. Reserva</h4>
            <p class="text-muted">Elige fecha, hora y profesional para tu servicio.</p>
          </div>
          
          <div class="col-md-4 text-center mb-4">
            <div class="step-icon mx-auto bg-primary text-white rounded-circle d-flex align-items-center justify-content-center">
              <i class="fas fa-gem fa-2x"></i>
            </div>
            <h4 class="mt-4">3. Disfruta</h4>
            <p class="text-muted">Disfruta de tu servicio y luce tu nuevo estilo.</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Lista de Peluquerías -->
    <section id="salons" class="py-5">
      <div class="container py-5">
        <div class="d-flex justify-content-between align-items-center mb-5">
          <div>
            <h2 class="fw-bold">Peluquerías Disponibles</h2>
            <p class="text-muted">Descubre los mejores establecimientos cerca de ti</p>
          </div>
          <div class="d-flex gap-2">
            <div class="dropdown">
              <button class="btn btn-outline-primary dropdown-toggle" type="button" data-bs-toggle="dropdown">
                <i class="fas fa-filter me-2"></i>Filtrar
              </button>
              <ul class="dropdown-menu">
                <li><a class="dropdown-item" href="#">Mejor Valoradas</a></li>
                <li><a class="dropdown-item" href="#">Más Cercanas</a></li>
                <li><a class="dropdown-item" href="#">Precio: Menor a Mayor</a></li>
                <li><a class="dropdown-item" href="#">Precio: Mayor a Menor</a></li>
              </ul>
            </div>
            <div class="input-group" style="width: 250px;">
              <span class="input-group-text bg-transparent"><i class="fas fa-search"></i></span>
              <input type="text" class="form-control" placeholder="Buscar peluquería..." v-model="searchQuery">
            </div>
          </div>
        </div>

        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Cargando...</span>
          </div>
          <p class="mt-3">Cargando peluquerías...</p>
        </div>

        <div v-else-if="filteredSalons.length === 0" class="text-center py-5">
          <i class="fas fa-store-slash fa-3x text-muted mb-3"></i>
          <h4>No se encontraron peluquerías</h4>
          <p class="text-muted">Intenta con otros términos de búsqueda</p>
        </div>

        <div v-else class="row">
          <div class="col-lg-4 col-md-6 mb-4" v-for="salon in filteredSalons" :key="salon.id">
            <div class="card h-100 salon-card shadow-sm">
              <img :src="salon.image" class="card-img-top" :alt="salon.name" style="height: 200px; object-fit: cover;">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-start mb-2">
                  <h5 class="card-title">{{ salon.name }}</h5>
                  <span class="badge bg-primary">{{ salon.category }}</span>
                </div>
                <p class="card-text text-muted">{{ salon.description }}</p>
                
                <div class="d-flex align-items-center mb-2">
                  <div class="rating">
                    <i class="fas fa-star text-warning" v-for="n in 5" :key="n" 
                       :class="{'text-muted': n > Math.round(salon.rating)}"></i>
                  </div>
                  <span class="ms-2">{{ salon.rating }} ({{ salon.reviews }} reviews)</span>
                </div>
                
                <div class="d-flex align-items-center text-muted mb-3">
                  <i class="fas fa-map-marker-alt me-2"></i>
                  <small>{{ salon.address }}</small>
                </div>
                
                <div class="salon-services mb-3">
                  <span class="badge bg-light text-dark me-1" v-for="service in salon.services.slice(0, 3)" :key="service">
                    {{ service }}
                  </span>
                  <span v-if="salon.services.length > 3" class="badge bg-light text-dark">
                    +{{ salon.services.length - 3 }}
                  </span>
                </div>
              </div>
              <div class="card-footer bg-white border-0 pt-0">
                <router-link :to="`/salon/${salon.id}`" class="btn btn-primary w-100">
                  Ver Detalles
                </router-link>
              </div>
            </div>
          </div>
        </div>

        <div v-if="!loading && filteredSalons.length > 0" class="text-center mt-5">
          <button class="btn btn-outline-primary" @click="loadMore" v-if="hasMore">
            Cargar más peluquerías
          </button>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="bg-dark text-white py-5">
      <div class="container">
        <div class="row">
          <div class="col-lg-4 mb-4">
            <h5 class="fw-bold">StyleCut Pro</h5>
            <p>La mejor manera de encontrar y reservar turnos en peluquerías y barberías.</p>
            <div class="social-links">
              <a href="#" class="text-white me-3"><i class="fab fa-facebook-f"></i></a>
              <a href="#" class="text-white me-3"><i class="fab fa-instagram"></i></a>
              <a href="#" class="text-white me-3"><i class="fab fa-twitter"></i></a>
            </div>
          </div>
          
          <div class="col-lg-2 col-md-4 mb-4">
            <h6 class="fw-bold">Enlaces Rápidos</h6>
            <ul class="list-unstyled">
              <li><a href="#" class="text-white text-decoration-none">Inicio</a></li>
              <li><a href="#salons" class="text-white text-decoration-none">Peluquerías</a></li>
              <li><a href="#how-it-works" class="text-white text-decoration-none">Cómo Funciona</a></li>
            </ul>
          </div>
          
          <div class="col-lg-2 col-md-4 mb-4">
            <h6 class="fw-bold">Para Clientes</h6>
            <ul class="list-unstyled">
              <li><router-link to="/register" class="text-white text-decoration-none">Registrarse</router-link></li>
              <li><router-link to="/login" class="text-white text-decoration-none">Iniciar Sesión</router-link></li>
              <li><a href="#" class="text-white text-decoration-none">Ayuda</a></li>
            </ul>
          </div>
          
          <div class="col-lg-4 col-md-4 mb-4">
            <h6 class="fw-bold">Suscríbete a nuestro newsletter</h6>
            <p>Recibe ofertas exclusivas y novedades.</p>
            <div class="input-group">
              <input type="email" class="form-control" placeholder="Tu correo electrónico">
              <button class="btn btn-primary">Suscribirse</button>
            </div>
          </div>
        </div>
        
        <hr class="my-4">
        
        <div class="text-center">
          <p class="mb-0">&copy; 2023 StyleCut Pro. Todos los derechos reservados.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';

export default {
  name: 'HomeView',
  setup() {
    const loading = ref(true);
    const searchQuery = ref('');
    const salons = ref([]);
    const currentPage = ref(1);
    const itemsPerPage = ref(6);
    const hasMore = ref(true);

    // Datos de ejemplo (en una app real vendrían de una API)
    const sampleSalons = [
      {
        id: 1,
        name: "Estilo Urbano",
        image: "https://images.unsplash.com/photo-1585747861815-0468562f7d4f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        description: "Especialistas en cortes modernos y tendencias urbanas.",
        rating: 4.8,
        reviews: 124,
        address: "Av. Siempre Viva 742, Ciudad",
        category: "Barbería",
        services: ["Corte de cabello", "Arreglo de barba", "Afeitado clásico", "Tinte", "Mascarillas"]
      },
      {
        id: 2,
        name: "Corte Perfecto",
        image: "https://images.unsplash.com/photo-1562322140-8baeececf3df?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        description: "Peluquería unisex con los mejores estilistas de la ciudad.",
        rating: 4.5,
        reviews: 89,
        address: "Calle Falsa 123, Ciudad",
        category: "Peluquería",
        services: ["Corte de cabello", "Coloración", "Peinados", "Tratamientos", "Extensiones"]
      },
      {
        id: 3,
        name: "Belleza Integral",
        image: "https://images.unsplash.com/photo-1599351431202-1e0f0137899a?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        description: "Salón de belleza completo con servicios de peluquería y estética.",
        rating: 4.9,
        reviews: 156,
        address: "Boulevard Central 456, Ciudad",
        category: "Salón de Belleza",
        services: ["Corte de cabello", "Manicura", "Pedicura", "Depilación", "Maquillaje", "Masajes"]
      },
      {
        id: 4,
        name: "Barba Elegante",
        image: "https://images.unsplash.com/photo-1622286342621-4bd786c2447c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        description: "Especialistas en el cuidado y estilo de barbas.",
        rating: 4.7,
        reviews: 78,
        address: "Plaza Central 789, Ciudad",
        category: "Barbería",
        services: ["Arreglo de barba", "Afeitado tradicional", "Tratamientos para barba", "Depilación"]
      },
      {
        id: 5,
        name: "Color Express",
        image: "https://images.unsplash.com/photo-1516975080664-ed2fc6a32937?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        description: "Especialistas en coloración y técnicas modernas de tinte.",
        rating: 4.6,
        reviews: 92,
        address: "Av. del Sol 321, Ciudad",
        category: "Peluquería",
        services: ["Coloración", "Balayage", "Mechas", "Reflejos", "Decoloración"]
      },
      {
        id: 6,
        name: "Estilo Clásico",
        image: "https://images.unsplash.com/photo-1503951914875-452162b0f3d1?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=600&q=80",
        description: "Tradición y elegancia en cada corte desde 1985.",
        rating: 4.9,
        reviews: 203,
        address: "Calle Tradición 654, Ciudad",
        category: "Barbería Clásica",
        services: ["Corte clásico", "Afeitado con navaja", "Servicio de hot towels", "Tratamientos vintage"]
      }
    ];

    // Simular carga de datos
    onMounted(() => {
      setTimeout(() => {
        salons.value = sampleSalons;
        loading.value = false;
      }, 1000);
    });

    const filteredSalons = computed(() => {
      let result = salons.value;
      
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        result = result.filter(salon => 
          salon.name.toLowerCase().includes(query) || 
          salon.description.toLowerCase().includes(query) ||
          salon.category.toLowerCase().includes(query) ||
          salon.services.some(service => service.toLowerCase().includes(query))
        );
      }
      
      // Paginación simple
      return result.slice(0, currentPage.value * itemsPerPage.value);
    });

    const loadMore = () => {
      if (currentPage.value * itemsPerPage.value < salons.value.length) {
        currentPage.value++;
      } else {
        hasMore.value = false;
      }
    };

    return {
      loading,
      searchQuery,
      filteredSalons,
      loadMore,
      hasMore
    };
  }
};
</script>

<style scoped>
.step-icon {
  width: 80px;
  height: 80px;
  margin-bottom: 1rem;
}

.salon-card {
  transition: transform 0.3s ease;
}

.salon-card:hover {
  transform: translateY(-5px);
}

.rating {
  display: inline-flex;
}

.hero-section {
  background: linear-gradient(135deg, #4e54c8 0%, #8f94fb 100%);
}

.navbar-brand {
  font-size: 1.5rem;
}

.navbar-nav .nav-link {
  font-weight: 500;
}

.btn {
  border-radius: 8px;
}

.card {
  border-radius: 12px;
  overflow: hidden;
}

.badge {
  border-radius: 6px;
}
</style>