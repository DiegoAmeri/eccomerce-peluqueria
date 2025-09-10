<template>
  <div class="main-layout">
    <SideBar v-if="showSidebar" />
    <div :class="['content', { 'with-sidebar': showSidebar }]">
      <router-view />
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import SideBar from '@/components/common/SideBar.vue'

export default {
  name: 'MainLayout',
  components: {
    SideBar
  },
  setup() {
    const route = useRoute()
    
    const showSidebar = computed(() => {
      return !route.meta.hideSidebar
    })

    return {
      showSidebar
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
}

.content.with-sidebar {
  margin-left: 250px;
}

@media (max-width: 768px) {
  .content.with-sidebar {
    margin-left: 0;
  }
}
</style>