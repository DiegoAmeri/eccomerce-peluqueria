<template>
  <div class="stat-card" :class="[theme, layout, { clickable: clickable }]" @click="handleClick">
    <!-- Icon Layout -->
    <div v-if="layout === 'icon'" class="stat-card-icon-layout">
      <div class="icon-container" :style="iconContainerStyle">
        <i :class="icon" class="stat-icon"></i>
      </div>
      <div class="stat-content">
        <div class="stat-value">{{ formattedValue }}</div>
        <div class="stat-label">{{ label }}</div>
        <div class="stat-trend" v-if="showTrend">
          <i :class="trendIcon" :style="{ color: trendColor }"></i>
          <span :style="{ color: trendColor }">{{ trendValue }}</span>
        </div>
      </div>
    </div>

    <!-- Compact Layout -->
    <div v-else-if="layout === 'compact'" class="stat-card-compact-layout">
      <div class="stat-header">
        <div class="stat-label">{{ label }}</div>
        <div class="stat-icon-small" :style="iconContainerStyle">
          <i :class="icon"></i>
        </div>
      </div>
      <div class="stat-value">{{ formattedValue }}</div>
      <div class="stat-trend" v-if="showTrend">
        <i :class="trendIcon" :style="{ color: trendColor }"></i>
        <span :style="{ color: trendColor }">{{ trendValue }}</span>
      </div>
    </div>

    <!-- Detailed Layout -->
    <div v-else class="stat-card-detailed-layout">
      <div class="stat-main">
        <div class="icon-container" :style="iconContainerStyle">
          <i :class="icon" class="stat-icon"></i>
        </div>
        <div class="stat-values">
          <div class="stat-value">{{ formattedValue }}</div>
          <div class="stat-label">{{ label }}</div>
        </div>
      </div>
      <div class="stat-details" v-if="showTrend || description">
        <div class="stat-trend" v-if="showTrend">
          <i :class="trendIcon" :style="{ color: trendColor }"></i>
          <span :style="{ color: trendColor }">{{ trendValue }}</span>
        </div>
        <div class="stat-description" v-if="description">
          {{ description }}
        </div>
        <div class="stat-progress" v-if="progress !== null">
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: progress + '%' }"></div>
          </div>
          <span class="progress-text">{{ progress }}%</span>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="stat-card-loading">
      <div class="loading-shimmer"></div>
    </div>

    <!-- Action Button -->
    <button v-if="actionText" class="stat-action-btn" @click.stop="handleAction">
      {{ actionText }}
      <i class="fas fa-arrow-right"></i>
    </button>
  </div>
</template>

<script>
import { computed } from 'vue';

export default {
  name: 'StatCard',
  props: {
    value: {
      type: [Number, String],
      required: true
    },
    label: {
      type: String,
      required: true
    },
    icon: {
      type: String,
      default: 'fas fa-chart-line'
    },
    iconColor: {
      type: String,
      default: '#6f42c1'
    },
    iconBgColor: {
      type: String,
      default: 'rgba(111, 66, 193, 0.1)'
    },
    theme: {
      type: String,
      default: 'default',
      validator: (value) => ['default', 'primary', 'success', 'warning', 'danger', 'info'].includes(value)
    },
    layout: {
      type: String,
      default: 'icon',
      validator: (value) => ['icon', 'compact', 'detailed'].includes(value)
    },
    trend: {
      type: Number,
      default: null // Positive or negative percentage
    },
    description: {
      type: String,
      default: ''
    },
    progress: {
      type: Number,
      default: null // 0-100 percentage
    },
    format: {
      type: String,
      default: 'number', // number, currency, percent, decimal
      validator: (value) => ['number', 'currency', 'percent', 'decimal'].includes(value)
    },
    currency: {
      type: String,
      default: '$'
    },
    decimals: {
      type: Number,
      default: 0
    },
    loading: {
      type: Boolean,
      default: false
    },
    clickable: {
      type: Boolean,
      default: false
    },
    actionText: {
      type: String,
      default: ''
    }
  },
  emits: ['click', 'action'],
  setup(props, { emit }) {
    const formattedValue = computed(() => {
      if (props.loading) return '--';
      
      const numValue = typeof props.value === 'string' ? parseFloat(props.value) : props.value;
      
      switch (props.format) {
        case 'currency':
          return `${props.currency}${numValue.toLocaleString('es-ES', {
            minimumFractionDigits: props.decimals,
            maximumFractionDigits: props.decimals
          })}`;
        case 'percent':
          return `${numValue.toFixed(props.decimals)}%`;
        case 'decimal':
          return numValue.toLocaleString('es-ES', {
            minimumFractionDigits: props.decimals,
            maximumFractionDigits: props.decimals
          });
        default:
          return numValue.toLocaleString('es-ES');
      }
    });
    
    const showTrend = computed(() => props.trend !== null);
    
    const trendIcon = computed(() => {
      if (props.trend > 0) return 'fas fa-arrow-up';
      if (props.trend < 0) return 'fas fa-arrow-down';
      return 'fas fa-minus';
    });
    
    const trendValue = computed(() => {
      if (props.trend === null) return '';
      return `${Math.abs(props.trend)}%`;
    });
    
    const trendColor = computed(() => {
      if (props.trend > 0) return '#28a745'; // Success green
      if (props.trend < 0) return '#dc3545'; // Danger red
      return '#6c757d'; // Neutral gray
    });
    
    const iconContainerStyle = computed(() => ({
      backgroundColor: props.iconBgColor,
      color: props.iconColor
    }));
    
    const handleClick = () => {
      if (props.clickable) {
        emit('click');
      }
    };
    
    const handleAction = () => {
      emit('action');
    };
    
    return {
      formattedValue,
      showTrend,
      trendIcon,
      trendValue,
      trendColor,
      iconContainerStyle,
      handleClick,
      handleAction
    };
  }
};
</script>

<style scoped>
.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-card.clickable {
  cursor: pointer;
}

.stat-card.clickable:hover {
  border-color: #6f42c1;
}

/* Theme Variants */
.stat-card.primary {
  background: linear-gradient(135deg, #6f42c1 0%, #8e44ad 100%);
  color: white;
}

.stat-card.success {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
}

.stat-card.warning {
  background: linear-gradient(135deg, #ffc107 0%, #fd7e14 100%);
  color: white;
}

.stat-card.danger {
  background: linear-gradient(135deg, #dc3545 0%, #e74c3c 100%);
  color: white;
}

.stat-card.info {
  background: linear-gradient(135deg, #17a2b8 0%, #3498db 100%);
  color: white;
}

/* Icon Layout */
.stat-card-icon-layout {
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon-container {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.stat-card:hover .icon-container {
  transform: scale(1.1);
}

.stat-icon {
  font-size: 1.5rem;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.9rem;
  color: #6c757d;
  font-weight: 500;
  margin-bottom: 8px;
}

.stat-card.primary .stat-label,
.stat-card.success .stat-label,
.stat-card.warning .stat-label,
.stat-card.danger .stat-label,
.stat-card.info .stat-label {
  color: rgba(255, 255, 255, 0.9);
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.8rem;
  font-weight: 600;
}

/* Compact Layout */
.stat-card-compact-layout {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.stat-label {
  margin: 0;
  flex: 1;
}

.stat-icon-small {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-value {
  font-size: 2rem;
  margin: 0;
}

/* Detailed Layout */
.stat-card-detailed-layout {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-main {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-values {
  flex: 1;
}

.stat-details {
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  padding-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-card.primary .stat-details,
.stat-card.success .stat-details,
.stat-card.warning .stat-details,
.stat-card.danger .stat-details,
.stat-card.info .stat-details {
  border-top-color: rgba(255, 255, 255, 0.2);
}

.stat-description {
  font-size: 0.85rem;
  color: #6c757d;
  line-height: 1.4;
}

.stat-card.primary .stat-description,
.stat-card.success .stat-description,
.stat-card.warning .stat-description,
.stat-card.danger .stat-description,
.stat-card.info .stat-description {
  color: rgba(255, 255, 255, 0.8);
}

.stat-progress {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
  overflow: hidden;
}

.stat-card.primary .progress-bar,
.stat-card.success .progress-bar,
.stat-card.warning .progress-bar,
.stat-card.danger .progress-bar,
.stat-card.info .progress-bar {
  background: rgba(255, 255, 255, 0.3);
}

.progress-fill {
  height: 100%;
  background: currentColor;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.8rem;
  font-weight: 600;
  min-width: 35px;
  text-align: right;
}

/* Loading State */
.stat-card-loading {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.loading-shimmer {
  width: 60%;
  height: 24px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  border-radius: 12px;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* Action Button */
.stat-action-btn {
  position: absolute;
  bottom: 16px;
  right: 16px;
  background: rgba(111, 66, 193, 0.1);
  border: none;
  color: #6f42c1;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 4px;
  opacity: 0;
  transform: translateY(10px);
}

.stat-card:hover .stat-action-btn {
  opacity: 1;
  transform: translateY(0);
}

.stat-action-btn:hover {
  background: #6f42c1;
  color: white;
}

.stat-card.primary .stat-action-btn,
.stat-card.success .stat-action-btn,
.stat-card.warning .stat-action-btn,
.stat-card.danger .stat-action-btn,
.stat-card.info .stat-action-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.stat-card.primary .stat-action-btn:hover,
.stat-card.success .stat-action-btn:hover,
.stat-card.warning .stat-action-btn:hover,
.stat-card.danger .stat-action-btn:hover,
.stat-card.info .stat-action-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* Responsive */
@media (max-width: 768px) {
  .stat-card {
    padding: 20px;
  }
  
  .stat-card-icon-layout {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
  
  .icon-container {
    width: 50px;
    height: 50px;
  }
  
  .stat-icon {
    font-size: 1.3rem;
  }
  
  .stat-value {
    font-size: 1.5rem;
  }
  
  .stat-card-detailed-layout {
    gap: 12px;
  }
  
  .stat-main {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
  
  .stat-action-btn {
    position: static;
    opacity: 1;
    transform: none;
    margin-top: 12px;
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 576px) {
  .stat-card {
    padding: 16px;
    border-radius: 12px;
  }
  
  .stat-value {
    font-size: 1.3rem;
  }
  
  .stat-label {
    font-size: 0.8rem;
  }
}
</style>