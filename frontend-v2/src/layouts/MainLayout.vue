<template>
  <div class="main-layout">
    <SideBar v-if="showSidebar" :class="{ 'mobile-hidden': !showSidebarMobile }" />
    <div :class="['content', { 'with-sidebar': showSidebar && !isMobile }]">
      <router-view />
    </div>
  </div>
</template>

<script>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import SideBar from '@/components/common/SideBar.vue'

export default {
  name: 'MainLayout',
  components: {
    SideBar
  },
  setup() {
    const route = useRoute()
    const isMobile = ref(false)
    
    const showSidebar = computed(() => {
      return !route.meta.hideSidebar
    })

    const showSidebarMobile = computed(() => {
      return showSidebar.value && !isMobile.value
    })

    const checkMobile = () => {
      isMobile.value = window.innerWidth < 992
    }

    onMounted(() => {
      checkMobile()
      window.addEventListener('resize', checkMobile)
    })

    onUnmounted(() => {
      window.removeEventListener('resize', checkMobile)
    })

    return {
      showSidebar,
      showSidebarMobile,
      isMobile
    }
  }
}
</script>

<style scoped>
.main-layout {
  display: flex;
  min-height: 100vh;
}

.content {
  flex: 1;
  padding: 20px;
  transition: all 0.3s ease;
  width: 100%;
}

.content.with-sidebar {
  margin-left: 250px;
  width: calc(100% - 250px);
}

@media (max-width: 992px) {
  .content.with-sidebar {
    margin-left: 0;
    width: 100%;
  }
  
  .mobile-hidden {
    display: none;
  }
}

@media (max-width: 768px) {
  .content {
    padding: 15px;
  }
}
</style>