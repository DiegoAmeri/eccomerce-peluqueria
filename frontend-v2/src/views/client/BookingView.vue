<template>
  <div class="booking-view">
    <!-- Header -->
    <div class="booking-header">
      <div class="header-content">
        <h1>Reservar Turno</h1>
        <nav aria-label="breadcrumb">
          <ol class="breadcrumb">
            <li class="breadcrumb-item">
              <router-link to="/salons">Peluquerías</router-link>
            </li>
            <li class="breadcrumb-item">
              <router-link :to="`/salons/${salonId}`">{{ salonName }}</router-link>
            </li>
            <li class="breadcrumb-item active">Reservar</li>
          </ol>
        </nav>
      </div>
    </div>

    <!-- Booking Process -->
    <div class="booking-process">
      <div class="process-steps">
        <div class="step" :class="{ active: currentStep === 1, completed: currentStep > 1 }">
          <div class="step-number">1</div>
          <div class="step-label">Servicio</div>
        </div>
        <div class="step-connector" :class="{ completed: currentStep > 1 }"></div>
        <div class="step" :class="{ active: currentStep === 2, completed: currentStep > 2 }">
          <div class="step-number">2</div>
          <div class="step-label">Profesional</div>
        </div>
        <div class="step-connector" :class="{ completed: currentStep > 2 }"></div>
        <div class="step" :class="{ active: currentStep === 3, completed: currentStep > 3 }">
          <div class="step-number">3</div>
          <div class="step-label">Fecha y Hora</div>
        </div>
        <div class="step-connector" :class="{ completed: currentStep > 3 }"></div>
        <div class="step" :class="{ active: currentStep === 4, completed: currentStep > 4 }">
          <div class="step-number">4</div>
          <div class="step-label">Confirmación</div>
        </div>
      </div>
    </div>

    <!-- Step Content -->
    <div class="booking-content">
      <!-- Step 1: Service Selection -->
      <div v-if="currentStep === 1" class="step-content fade-in">
        <div class="row">
          <div class="col-lg-8">
            <div class="card">
              <div class="card-header">
                <h5 class="mb-0">Selecciona un Servicio</h5>
              </div>
              <div class="card-body">
                <div class="service-categories">
                  <button v-for="category in categories" :key="category" 
                          class="category-btn" :class="{ active: selectedCategory === category }"
                          @click="selectedCategory = category">
                    {{ category }}
                  </button>
                </div>

                <div class="services-list">
                  <div v-for="service in filteredServices" :key="service.id" 
                       class="service-item" :class="{ selected: selectedService?.id === service.id }"
                       @click="selectService(service)">
                    <div class="service-info">
                      <h6>{{ service.name }}</h6>
                      <p class="service-description">{{ service.description }}</p>
                      <div class="service-meta">
                        <span class="duration">
                          <i class="fas fa-clock me-1"></i>{{ service.duration }} min
                        </span>
                        <span class="price">${{ service.price }}</span>
                      </div>
                    </div>
                    <div class="service-select">
                      <i class="fas fa-check-circle"></i>
                    </div>
                  </div>
                </div>

                <div v-if="filteredServices.length === 0" class="text-center py-4">
                  <i class="fas fa-scissors fa-3x text-muted mb-3"></i>
                  <p>No hay servicios disponibles en esta categoría</p>
                </div>
              </div>
            </div>
          </div>

          <div class="col-lg-4">
            <div class="card booking-summary">
              <div class="card-header">
                <h6 class="mb-0">Resumen de Reserva</h6>
              </div>
              <div class="card-body">
                <div v-if="!selectedService" class="text-center text-muted py-3">
                  <p>Selecciona un servicio para continuar</p>
                </div>
                <div v-else>
                  <div class="selected-service">
                    <h6>{{ selectedService.name }}</h6>
                    <p class="text-muted">{{ selectedService.description }}</p>
                    <div class="service-details">
                      <span class="duration">{{ selectedService.duration }} min</span>
                      <span class="price">${{ selectedService.price }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="card-footer">
                <button class="btn btn-primary w-100" :disabled="!selectedService" @click="nextStep">
                  Continuar <i class="fas fa-arrow-right ms-2"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Step 2: Professional Selection -->
      <div v-if="currentStep === 2" class="step-content fade-in">
        <div class="row">
          <div class="col-lg-8">
            <div class="card">
              <div class="card-header">
                <h5 class="mb-0">Selecciona un Profesional</h5>
                <p class="text-muted mb-0">Profesionales disponibles para {{ selectedService?.name }}</p>
              </div>
              <div class="card-body">
                <div class="professionals-grid">
                  <div v-for="professional in professionals" :key="professional.id"
                       class="professional-card" :class="{ selected: selectedProfessional?.id === professional.id }"
                       @click="selectProfessional(professional)">
                    <img :src="professional.avatar" :alt="professional.name" class="professional-avatar">
                    <div class="professional-info">
                      <h6>{{ professional.name }}</h6>
                      <p class="specialty">{{ professional.specialty }}</p>
                      <div class="rating">
                        <i class="fas fa-star text-warning"></i>
                        <span>{{ professional.rating }}</span>
                        <small class="text-muted">({{ professional.reviews }})</small>
                      </div>
                    </div>
                    <div class="professional-select">
                      <i class="fas fa-check-circle"></i>
                    </div>
                  </div>
                </div>

                <div v-if="professionals.length === 0" class="text-center py-4">
                  <i class="fas fa-user-slash fa-3x text-muted mb-3"></i>
                  <p>No hay profesionales disponibles para este servicio</p>
                </div>
              </div>
            </div>
          </div>

          <div class="col-lg-4">
            <div class="card booking-summary">
              <div class="card-header">
                <h6 class="mb-0">Resumen de Reserva</h6>
              </div>
              <div class="card-body">
                <div class="selected-service">
                  <h6>{{ selectedService?.name }}</h6>
                  <p class="text-muted">{{ selectedService?.description }}</p>
                  <div class="service-details">
                    <span class="duration">{{ selectedService?.duration }} min</span>
                    <span class="price">${{ selectedService?.price }}</span>
                  </div>
                </div>
                <hr>
                <div v-if="selectedProfessional" class="selected-professional">
                  <h6>Con: {{ selectedProfessional.name }}</h6>
                  <p class="text-muted">{{ selectedProfessional.specialty }}</p>
                  <div class="rating">
                    <i class="fas fa-star text-warning"></i>
                    <span>{{ selectedProfessional.rating }}</span>
                    <small class="text-muted">({{ selectedProfessional.reviews }})</small>
                  </div>
                </div>
              </div>
              <div class="card-footer">
                <div class="d-flex gap-2">
                  <button class="btn btn-outline-primary flex-fill" @click="prevStep">
                    <i class="fas fa-arrow-left me-2"></i>Atrás
                  </button>
                  <button class="btn btn-primary flex-fill" :disabled="!selectedProfessional" @click="nextStep">
                    Continuar <i class="fas fa-arrow-right ms-2"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Step 3: Date and Time Selection -->
      <div v-if="currentStep === 3" class="step-content fade-in">
        <div class="row">
          <div class="col-lg-8">
            <div class="card">
              <div class="card-header">
                <h5 class="mb-0">Selecciona Fecha y Hora</h5>
                <p class="text-muted mb-0">Disponibilidad de {{ selectedProfessional?.name }}</p>
              </div>
              <div class="card-body">
                <div class="date-selection">
                  <div class="calendar-nav">
                    <button class="btn btn-outline-secondary btn-sm" @click="previousWeek">
                      <i class="fas fa-chevron-left"></i>
                    </button>
                    <h6 class="current-week">{{ currentWeekRange }}</h6>
                    <button class="btn btn-outline-secondary btn-sm" @click="nextWeek">
                      <i class="fas fa-chevron-right"></i>
                    </button>
                  </div>

                  <div class="week-days">
                    <div v-for="day in weekDays" :key="day.date" class="day-column">
                      <div class="day-header" :class="{ today: day.isToday, selected: selectedDate === day.date }">
                        <div class="day-name">{{ day.name }}</div>
                        <div class="day-number">{{ day.number }}</div>
                      </div>
                      <div class="time-slots">
                        <div v-for="slot in day.timeSlots" :key="slot.time"
                             class="time-slot" :class="{ available: slot.available, selected: selectedSlot === slot }"
                             @click="selectTimeSlot(slot, day)">
                          {{ slot.time }}
                        </div>
                        <div v-if="day.timeSlots.length === 0" class="no-slots">
                          No hay horarios disponibles
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="col-lg-4">
            <div class="card booking-summary">
              <div class="card-header">
                <h6 class="mb-0">Resumen de Reserva</h6>
              </div>
              <div class="card-body">
                <div class="selected-service">
                  <h6>{{ selectedService?.name }}</h6>
                  <p class="text-muted">{{ selectedService?.description }}</p>
                  <div class="service-details">
                    <span class="duration">{{ selectedService?.duration }} min</span>
                    <span class="price">${{ selectedService?.price }}</span>
                  </div>
                </div>
                <hr>
                <div class="selected-professional">
                  <h6>Con: {{ selectedProfessional?.name }}</h6>
                  <p class="text-muted">{{ selectedProfessional?.specialty }}</p>
                </div>
                <hr>
                <div v-if="selectedDate && selectedSlot" class="selected-time">
                  <h6>Fecha y Hora</h6>
                  <p class="text-muted">{{ formattedSelectedDate }}</p>
                  <p class="text-muted">{{ selectedSlot.time }}</p>
                </div>
              </div>
              <div class="card-footer">
                <div class="d-flex gap-2">
                  <button class="btn btn-outline-primary flex-fill" @click="prevStep">
                    <i class="fas fa-arrow-left me-2"></i>Atrás
                  </button>
                  <button class="btn btn-primary flex-fill" :disabled="!selectedSlot" @click="nextStep">
                    Continuar <i class="fas fa-arrow-right ms-2"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Step 4: Confirmation -->
      <div v-if="currentStep === 4" class="step-content fade-in">
        <div class="row justify-content-center">
          <div class="col-lg-8">
            <div class="card">
              <div class="card-header text-center">
                <h5 class="mb-0">Confirmar Reserva</h5>
                <p class="text-muted">Revisa los detalles de tu reserva antes de confirmar</p>
              </div>
              <div class="card-body">
                <div class="booking-details">
                  <div class="detail-section">
                    <h6>Servicio</h6>
                    <div class="detail-content">
                      <div class="service-icon">
                        <i class="fas fa-scissors"></i>
                      </div>
                      <div class="detail-text">
                        <strong>{{ selectedService?.name }}</strong>
                        <p class="mb-0">{{ selectedService?.description }}</p>
                        <small class="text-muted">{{ selectedService?.duration }} min • ${{ selectedService?.price }}</small>
                      </div>
                    </div>
                  </div>

                  <div class="detail-section">
                    <h6>Profesional</h6>
                    <div class="detail-content">
                      <img :src="selectedProfessional?.avatar" :alt="selectedProfessional?.name" class="professional-avatar-sm">
                      <div class="detail-text">
                        <strong>{{ selectedProfessional?.name }}</strong>
                        <p class="mb-0">{{ selectedProfessional?.specialty }}</p>
                        <div class="rating">
                          <i class="fas fa-star text-warning"></i>
                          <span>{{ selectedProfessional?.rating }}</span>
                          <small class="text-muted">({{ selectedProfessional?.reviews }} reviews)</small>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="detail-section">
                    <h6>Fecha y Hora</h6>
                    <div class="detail-content">
                      <div class="date-icon">
                        <i class="fas fa-calendar-alt"></i>
                      </div>
                      <div class="detail-text">
                        <strong>{{ formattedSelectedDate }}</strong>
                        <p class="mb-0">{{ selectedSlot?.time }}</p>
                        <small class="text-muted">{{ selectedSalon?.name }}</small>
                      </div>
                    </div>
                  </div>

                  <div class="detail-section">
                    <h6>Información de Contacto</h6>
                    <div class="contact-info">
                      <div class="info-item">
                        <i class="fas fa-user"></i>
                        <span>{{ userInfo.name }}</span>
                      </div>
                      <div class="info-item">
                        <i class="fas fa-phone"></i>
                        <span>{{ userInfo.phone }}</span>
                      </div>
                      <div class="info-item">
                        <i class="fas fa-envelope"></i>
                        <span>{{ userInfo.email }}</span>
                      </div>
                    </div>
                  </div>

                  <div class="detail-section">
                    <h6>Notas Adicionales (Opcional)</h6>
                    <textarea v-model="bookingNotes" class="form-control" rows="3"
                              placeholder="Agrega alguna nota especial para el profesional..."></textarea>
                  </div>
                </div>
              </div>
              <div class="card-footer">
                <div class="booking-total">
                  <div class="total-line">
                    <span>Subtotal:</span>
                    <span>${{ selectedService?.price }}</span>
                  </div>
                  <div class="total-line">
                    <span>Impuestos:</span>
                    <span>${{ taxes }}</span>
                  </div>
                  <div class="total-line total">
                    <strong>Total:</strong>
                    <strong>${{ totalPrice }}</strong>
                  </div>
                </div>
                <div class="d-flex gap-2 mt-3">
                  <button class="btn btn-outline-primary flex-fill" @click="prevStep">
                    <i class="fas fa-arrow-left me-2"></i>Atrás
                  </button>
                  <button class="btn btn-primary flex-fill" @click="confirmBooking" :disabled="bookingLoading">
                    <span v-if="bookingLoading" class="spinner-border spinner-border-sm me-2"></span>
                    {{ bookingLoading ? 'Confirmando...' : 'Confirmar Reserva' }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Success Modal -->
    <ModalDialog :show="showSuccessModal" title="¡Reserva Confirmada!" @update:show="showSuccessModal = false">
      <div class="text-center">
        <i class="fas fa-check-circle fa-5x text-success mb-3"></i>
        <h4>¡Tu reserva ha sido confirmada!</h4>
        <p class="text-muted">Hemos enviado los detalles a tu correo electrónico</p>
        
        <div class="booking-confirmation-details">
          <p><strong>Número de Reserva:</strong> #{{ bookingConfirmation.bookingNumber }}</p>
          <p><strong>Servicio:</strong> {{ bookingConfirmation.serviceName }}</p>
          <p><strong>Profesional:</strong> {{ bookingConfirmation.professionalName }}</p>
          <p><strong>Fecha:</strong> {{ bookingConfirmation.date }}</p>
          <p><strong>Hora:</strong> {{ bookingConfirmation.time }}</p>
          <p><strong>Lugar:</strong> {{ bookingConfirmation.salonName }}</p>
        </div>

        <div class="d-flex justify-content-center gap-2 mt-4">
          <button class="btn btn-outline-primary" @click="goToDashboard">
            <i class="fas fa-home me-2"></i>Ir al Inicio
          </button>
          <button class="btn btn-primary" @click="viewAppointment">
            <i class="fas fa-calendar me-2"></i>Ver mi Turno
          </button>
        </div>
      </div>
    </ModalDialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useStore } from 'vuex';
import ModalDialog from '@/components/common/ModalDialog.vue';

export default {
  name: 'BookingView',
  components: {
    ModalDialog
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const store = useStore();

    const currentStep = ref(1);
    const salonId = ref(route.params.salonId);
    const salonName = ref('Peluquería');
    const selectedCategory = ref('Todos');
    const selectedService = ref(null);
    const selectedProfessional = ref(null);
    const selectedDate = ref(null);
    const selectedSlot = ref(null);
    const bookingNotes = ref('');
    const bookingLoading = ref(false);
    const showSuccessModal = ref(false);
    const currentWeekStart = ref(new Date());

    // Datos de ejemplo (en una app real vendrían de la API)
    const categories = ref(['Todos', 'Corte', 'Color', 'Tratamientos', 'Barba', 'Uñas']);
    const services = ref([
      {
        id: 1,
        name: 'Corte de Cabello',
        description: 'Corte moderno y personalizado',
        category: 'Corte',
        duration: 30,
        price: 500,
        professionals: [1, 2]
      },
      {
        id: 2,
        name: 'Corte + Barba',
        description: 'Corte completo con arreglo de barba',
        category: 'Corte',
        duration: 45,
        price: 700,
        professionals: [1, 3]
      },
      {
        id: 3,
        name: 'Coloración Completa',
        description: 'Tinte completo con productos premium',
        category: 'Color',
        duration: 120,
        price: 1200,
        professionals: [2]
      }
    ]);

    const professionals = ref([
      {
        id: 1,
        name: 'Carlos Rodríguez',
        specialty: 'Especialista en cortes modernos',
        rating: 4.8,
        reviews: 124,
        avatar: 'https://ui-avatars.com/api/?name=Carlos+Rodriguez&background=6f42c1&color=fff'
      },
      {
        id: 2,
        name: 'Ana Martínez',
        specialty: 'Colorista profesional',
        rating: 4.9,
        reviews: 89,
        avatar: 'https://ui-avatars.com/api/?name=Ana+Martinez&background=d63384&color=fff'
      },
      {
        id: 3,
        name: 'Miguel Ángel',
        specialty: 'Barbería clásica',
        rating: 4.7,
        reviews: 156,
        avatar: 'https://ui-avatars.com/api/?name=Miguel+Angel&background=20c997&color=fff'
      }
    ]);

    const selectedSalon = ref({
      id: salonId.value,
      name: 'Estilo Urbano',
      address: 'Av. Siempre Viva 742',
      phone: '+54 11 1234-5678'
    });

    const userInfo = computed(() => store.getters.userData || {
      name: 'Juan Pérez',
      email: 'juan@example.com',
      phone: '+54 11 1234-5678'
    });

    const filteredServices = computed(() => {
      if (selectedCategory.value === 'Todos') {
        return services.value;
      }
      return services.value.filter(service => service.category === selectedCategory.value);
    });

    const weekDays = computed(() => {
      const days = [];
      const startDate = new Date(currentWeekStart.value);
      
      for (let i = 0; i < 7; i++) {
        const date = new Date(startDate);
        date.setDate(startDate.getDate() + i);
        
        const isToday = date.toDateString() === new Date().toDateString();
        const timeSlots = generateTimeSlots(date);
        
        days.push({
          date: date.toISOString().split('T')[0],
          name: date.toLocaleDateString('es-ES', { weekday: 'short' }),
          number: date.getDate(),
          month: date.toLocaleDateString('es-ES', { month: 'short' }),
          isToday,
          timeSlots
        });
      }
      
      return days;
    });

    const currentWeekRange = computed(() => {
      const start = new Date(currentWeekStart.value);
      const end = new Date(start);
      end.setDate(start.getDate() + 6);
      
      return `${start.getDate()} ${start.toLocaleDateString('es-ES', { month: 'short' })} - ${end.getDate()} ${end.toLocaleDateString('es-ES', { month: 'short' })}`;
    });

    const formattedSelectedDate = computed(() => {
      if (!selectedDate.value) return '';
      const date = new Date(selectedDate.value);
      return date.toLocaleDateString('es-ES', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    });

    const taxes = computed(() => {
      return selectedService.value ? Math.round(selectedService.value.price * 0.21) : 0;
    });

    const totalPrice = computed(() => {
      return selectedService.value ? selectedService.value.price + taxes.value : 0;
    });

    const bookingConfirmation = computed(() => ({
      bookingNumber: 'RES' + Math.random().toString().substr(2, 6),
      serviceName: selectedService.value?.name,
      professionalName: selectedProfessional.value?.name,
      date: formattedSelectedDate.value,
      time: selectedSlot.value?.time,
      salonName: selectedSalon.value?.name
    }));

    const generateTimeSlots = (date) => {
      const slots = [];
      const startHour = 9;
      const endHour = 18;
      const interval = 30;
      
      for (let hour = startHour; hour < endHour; hour++) {
        for (let minute = 0; minute < 60; minute += interval) {
          // Simular disponibilidad aleatoria
          const available = Math.random() > 0.3;
          
          slots.push({
            time: `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`,
            available,
            date: date.toISOString().split('T')[0]
          });
        }
      }
      
      return slots;
    };

    const selectService = (service) => {
      selectedService.value = service;
    };

    const selectProfessional = (professional) => {
      selectedProfessional.value = professional;
    };

    const selectTimeSlot = (slot, day) => {
      if (slot.available) {
        selectedSlot.value = slot;
        selectedDate.value = day.date;
      }
    };

    const nextStep = () => {
      if (currentStep.value < 4) {
        currentStep.value++;
      }
    };

    const prevStep = () => {
      if (currentStep.value > 1) {
        currentStep.value--;
      }
    };

    const previousWeek = () => {
      const date = new Date(currentWeekStart.value);
      date.setDate(date.getDate() - 7);
      currentWeekStart.value = date;
    };

    const nextWeek = () => {
      const date = new Date(currentWeekStart.value);
      date.setDate(date.getDate() + 7);
      currentWeekStart.value = date;
    };

    const confirmBooking = async () => {
      bookingLoading.value = true;
      
      try {
        // Simular llamada a la API
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        showSuccessModal.value = true;
      } catch (error) {
        console.error('Error confirming booking:', error);
        alert('Error al confirmar la reserva. Por favor, intenta nuevamente.');
      } finally {
        bookingLoading.value = false;
      }
    };

    const goToDashboard = () => {
      router.push('/dashboard');
    };

    const viewAppointment = () => {
      router.push('/my-appointments');
    };

    onMounted(() => {
      // Cargar datos de la peluquería
      salonName.value = selectedSalon.value.name;
    });

    return {
      currentStep,
      salonId,
      salonName,
      selectedCategory,
      selectedService,
      selectedProfessional,
      selectedDate,
      selectedSlot,
      bookingNotes,
      bookingLoading,
      showSuccessModal,
      categories,
      services,
      professionals,
      filteredServices,
      weekDays,
      currentWeekRange,
      formattedSelectedDate,
      taxes,
      totalPrice,
      bookingConfirmation,
      userInfo,
      selectService,
      selectProfessional,
      selectTimeSlot,
      nextStep,
      prevStep,
      previousWeek,
      nextWeek,
      confirmBooking,
      goToDashboard,
      viewAppointment
    };
  }
};
</script>

<style scoped>
.booking-view {
  padding: 20px;
}

.booking-header {
  margin-bottom: 30px;
}

.booking-header h1 {
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 10px;
}

.breadcrumb {
  margin-bottom: 0;
}

.breadcrumb-item a {
  color: #6f42c1;
  text-decoration: none;
}

.breadcrumb-item.active {
  color: #6c757d;
}

.booking-process {
  margin-bottom: 30px;
}

.process-steps {
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 600px;
  margin: 0 auto;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 2;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #dee2e6;
  color: #6c757d;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.step.active .step-number {
  background-color: #6f42c1;
  color: white;
  transform: scale(1.1);
}

.step.completed .step-number {
  background-color: #20c997;
  color: white;
}

.step-label {
  font-size: 0.8rem;
  color: #6c757d;
  font-weight: 500;
}

.step.active .step-label {
  color: #6f42c1;
  font-weight: 600;
}

.step-connector {
  flex: 1;
  height: 2px;
  background-color: #dee2e6;
  margin: 0 10px;
  transition: all 0.3s ease;
}

.step-connector.completed {
  background-color: #20c997;
}

.step-content {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.service-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.category-btn {
  padding: 8px 16px;
  border: 2px solid #e9ecef;
  border-radius: 20px;
  background: white;
  color: #6c757d;
  font-weight: 500;
  transition: all 0.3s ease;
  cursor: pointer;
}

.category-btn:hover,
.category-btn.active {
  border-color: #6f42c1;
  background: #6f42c1;
  color: white;
}

.services-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.service-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.service-item:hover {
  border-color: #6f42c1;
  transform: translateY(-2px);
}

.service-item.selected {
  border-color: #6f42c1;
  background: rgba(111, 66, 193, 0.05);
}

.service-info {
  flex: 1;
}

.service-info h6 {
  margin-bottom: 4px;
  color: #2c3e50;
}

.service-description {
  color: #6c757d;
  font-size: 0.9rem;
  margin-bottom: 8px;
}

.service-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.duration {
  color: #6c757d;
  font-size: 0.85rem;
}

.price {
  font-weight: 600;
  color: #198754;
  font-size: 1.1rem;
}

.service-select {
  color: #20c997;
  font-size: 1.2rem;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.service-item.selected .service-select {
  opacity: 1;
}

.booking-summary {
  position: sticky;
  top: 20px;
}

.professionals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
}

.professional-card {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.professional-card:hover {
  border-color: #6f42c1;
  transform: translateY(-2px);
}

.professional-card.selected {
  border-color: #6f42c1;
  background: rgba(111, 66, 193, 0.05);
}

.professional-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 16px;
}

.professional-info h6 {
  margin-bottom: 4px;
  color: #2c3e50;
}

.specialty {
  color: #6c757d;
  font-size: 0.9rem;
  margin-bottom: 4px;
}

.rating {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.85rem;
}

.date-selection {
  margin-bottom: 20px;
}

.calendar-nav {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 20px;
}

.current-week {
  margin: 0;
  font-weight: 600;
  color: #2c3e50;
  min-width: 200px;
  text-align: center;
}

.week-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.day-column {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  overflow: hidden;
}

.day-header {
  padding: 12px;
  text-align: center;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.day-header.today {
  background: #e7f5ff;
  border-bottom: 2px solid #0d6efd;
}

.day-header.selected {
  background: #6f42c1;
  color: white;
}

.day-name {
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.8rem;
  margin-bottom: 4px;
}

.day-number {
  font-size: 1.2rem;
  font-weight: 700;
}

.time-slots {
  padding: 8px;
  max-height: 200px;
  overflow-y: auto;
}

.time-slot {
  padding: 8px;
  margin-bottom: 4px;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  text-align: center;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.time-slot.available {
  background: white;
  color: #495057;
}

.time-slot.available:hover {
  border-color: #6f42c1;
  background: rgba(111, 66, 193, 0.1);
}

.time-slot.selected {
  background: #6f42c1;
  color: white;
  border-color: #6f42c1;
}

.time-slot:not(.available) {
  background: #f8f9fa;
  color: #6c757d;
  cursor: not-allowed;
  text-decoration: line-through;
}

.no-slots {
  text-align: center;
  color: #6c757d;
  font-size: 0.9rem;
  padding: 16px;
}

.booking-details {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-section {
  padding: 16px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
}

.detail-section h6 {
  color: #6f42c1;
  margin-bottom: 12px;
  font-weight: 600;
}

.detail-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.service-icon,
.date-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(111, 66, 193, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6f42c1;
  font-size: 1.2rem;
}

.professional-avatar-sm {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.detail-text {
  flex: 1;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-item i {
  width: 16px;
  color: #6f42c1;
}

.booking-total {
  border-top: 1px solid #e9ecef;
  padding-top: 16px;
}

.total-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.total-line.total {
  border-top: 1px solid #e9ecef;
  padding-top: 8px;
  margin-top: 8px;
  font-size: 1.1rem;
}

.booking-confirmation-details {
  text-align: left;
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  margin: 20px 0;
}

.booking-confirmation-details p {
  margin-bottom: 8px;
}

@media (max-width: 992px) {
  .booking-view {
    padding: 15px;
  }
  
  .process-steps {
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .step-connector {
    display: none;
  }
  
  .week-days {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .professionals-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .service-meta {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .week-days {
    grid-template-columns: 1fr;
  }
  
  .detail-content {
    flex-direction: column;
    text-align: center;
  }
  
  .contact-info {
    align-items: center;
  }
}
</style>