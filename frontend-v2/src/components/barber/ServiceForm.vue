<template>
  <form @submit.prevent="submitForm">
    <div class="mb-3">
      <label class="form-label">Nombre del Servicio</label>
      <input 
        type="text" 
        class="form-control" 
        v-model="formData.name" 
        required
        placeholder="Ej: Corte de Cabello"
      >
    </div>
    
    <div class="mb-3">
      <label class="form-label">Descripción</label>
      <textarea 
        class="form-control" 
        v-model="formData.description" 
        rows="3"
        placeholder="Describe brevemente el servicio"
      ></textarea>
    </div>
    
    <div class="row">
      <div class="col-md-6 mb-3">
        <label class="form-label">Categoría</label>
        <input 
          type="text" 
          class="form-control" 
          v-model="formData.category" 
          required
          placeholder="Ej: Cortes, Coloración, etc."
        >
      </div>
      
      <div class="col-md-6 mb-3">
        <label class="form-label">Icono</label>
        <select class="form-select" v-model="formData.icon">
          <option value="fas fa-cut">Tijeras (Corte)</option>
          <option value="fas fa-air-freshener">Ambientador (Barba)</option>
          <option value="fas fa-paint-brush">Pincel (Color)</option>
          <option value="fas fa-spa">Spa (Tratamiento)</option>
          <option value="fas fa-magic">Varita (Estilo)</option>
          <option value="fas fa-soap">Jabón (Lavado)</option>
        </select>
      </div>
    </div>
    
    <div class="row">
      <div class="col-md-6 mb-3">
        <label class="form-label">Precio ($)</label>
        <input 
          type="number" 
          class="form-control" 
          v-model="formData.price" 
          min="0"
          step="0.01"
          required
        >
      </div>
      
      <div class="col-md-6 mb-3">
        <label class="form-label">Duración (minutos)</label>
        <select class="form-select" v-model="formData.duration">
          <option value="15">15 minutos</option>
          <option value="30">30 minutos</option>
          <option value="45">45 minutos</option>
          <option value="60">60 minutos</option>
          <option value="90">90 minutos</option>
          <option value="120">120 minutos</option>
        </select>
      </div>
    </div>
    
    <div class="form-actions">
      <button type="button" class="btn btn-secondary" @click="$emit('cancel')">
        Cancelar
      </button>
      <button type="submit" class="btn btn-primary">
        {{ editing ? 'Actualizar' : 'Crear' }} Servicio
      </button>
    </div>
  </form>
</template>

<script>
import { ref, watch } from 'vue';

export default {
  name: 'ServiceForm',
  props: {
    service: {
      type: Object,
      default: null
    }
  },
  emits: ['submit', 'cancel'],
  setup(props, { emit }) {
    const editing = ref(!!props.service);
    
    const formData = ref({
      name: '',
      description: '',
      category: '',
      icon: 'fas fa-cut',
      price: 0,
      duration: 30
    });
    
    // Si estamos editando, llenar el formulario con los datos del servicio
    watch(() => props.service, (newService) => {
      if (newService) {
        formData.value = { ...newService };
        editing.value = true;
      } else {
        resetForm();
        editing.value = false;
      }
    }, { immediate: true });
    
    const resetForm = () => {
      formData.value = {
        name: '',
        description: '',
        category: '',
        icon: 'fas fa-cut',
        price: 0,
        duration: 30
      };
    };
    
    const submitForm = () => {
      emit('submit', formData.value);
      resetForm();
    };
    
    return {
      formData,
      editing,
      submitForm
    };
  }
};
</script>

<style scoped>
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>