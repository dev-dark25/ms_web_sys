import { createRouter, createWebHistory, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),  // 默认history模式
  // history: createWebHashHistory(import.meta.env.BASE_URL),  // hash模式
  routes: [
    // 配置静态路由，所有找不到的页面会跳到这里
    {
      path: '/:pathMatch(.*)',
      // name: 'NotFound', // 动态添加的路由页面，刷新后会有警告，注释该行解决
      component: () => import('../views/NotFound.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue')
    },
    {
      path: '/',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/main',
      name: 'main',
      component: () => import('../views/Main.vue'),
      redirect: { name: 'default' },  // 根据name属性指定默认页面
      children: [
        {
          path: '/default',  // 指定的默认页面
          name: 'default',
          component: () => import('../views/Default.vue'),
        },
        {
          path: '/simple',
          name: 'simple',
          component: () => import('../views/Pages/Simple.vue'),
        },
        {
          path: '/experiences',
          name: 'experiences',
          component: () => import('../views/Pages/Experiences.vue'),
        },
      ]
    },
  ]
})

let isAddRoute: boolean = true; // 是否添加路由

router.beforeEach(async (to, from, next) => {
  if (to.path === '/index.html') {
    next({ path: '/' });  // 首页
    return;
  }

  const store = useUserStore();
  if (store.token) {
    if (to.path === '/' || to.path === '/index.html') {
      // 登录后访问login
      const path = from.path === '/' ? '/main' : from.path
      next({ path: path });
    } else {
      // 以下代码块适用于token保存在localStorage中，关掉或打开新页面登录状态还在的场景
      if (!store.userInfo.name) {
        try {
          // 登录后userInfo信息失效，重新获取
          await store.reqUserInfo();
        } catch (error) {
          await store.reqUserLogout();
          sessionStorage.clear();
          localStorage.clear();
          next({ path: '/' });
          return;
        }
      }
      // 以上代码块适用于token保存在localStorage中，关掉或打开新页面登录状态还在的场景

      //  动态添加路由
      if (isAddRoute) {
        const routeList = store.routeList;
        routeList.forEach((item: any) => {
          router.addRoute('main', {
            path: item.path,
            name: item.name,
            component: () => import(`../views/${item.directory}/${item.vuePage}.vue`)
          })
        })
        isAddRoute = false;
        next({ ...to, replace: true });
      } else {
        next();
      }
    }
  } else {
    // 未登录
    isAddRoute = true;  // 退出后重置，切换账号登录不会动态添加路由
    sessionStorage.clear();
    localStorage.clear();
    if (to.path !== '/' && to.path !== '/register' && to.path.indexOf('/login_')) {  // 这样写，所有未登录找不到的页面统一跳到登录页
      next({ path: '/', query: { redirect: to.path } });
    } else {
      next();
    }
  }
})

export default router
