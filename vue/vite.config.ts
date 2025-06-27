import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default defineConfig({
  base: '/dist',
  build: {
    outDir: '../nginx/dist/', // build文件输出至nginx部署目录
    emptyOutDir: true,  // 清空构建输出目录
    // chunkSizeWarningLimit: 1500,
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.toString().split('node_modules/')[1].split('/')[0].includes('element-plus')) {
              return 'element-plus';
            } else if (id.toString().split('node_modules/')[1].split('/')[0].includes('echarts')) {
              return 'echarts';
            } else {
              return id.toString().split('node_modules/')[1].split('/')[0].toString();
            }
          }
        }
      }
    }
  },
  // 代理，开发环境生效
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8081/web',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, '')
      },
      // '/api1': {
      //   target: 'http://localhost:8081',
      //   changeOrigin: true,
      //   rewrite: (path) => path.replace(/^\/api/, '')
      // }
    }
  },
  plugins: [
    vue(),
    // 按需导入element+
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      // '@images': fileURLToPath(new URL('./img', import.meta.url)) // 指向public/img，public是root path
    }
  },
  css: {
    // css预处理器
    preprocessorOptions: {
      less: {
        charset: false,
        additionalData: '@import "@/assets/style/global.less";',  // less全局文件
      },
    },
  },
})
