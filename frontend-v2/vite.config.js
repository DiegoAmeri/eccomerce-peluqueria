import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    hmr: {
      overlay: false
    },
    fs: {
      strict: false
    }
  },
  optimizeDeps: {
    exclude: ['html2pdf.js']
  },
  // Añadir esta configuración:
  build: {
    rollupOptions: {
      input: {
        main: resolve(__dirname, 'index.html')
      }
    }
  }
})