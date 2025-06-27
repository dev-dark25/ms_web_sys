import { ref, reactive, computed } from 'vue'
import { defineStore } from 'pinia'
import { login, logout, getUserInfo, getMenuInfo } from '@/axios/api'
import type { UserInfo, MenuInfo, Test } from '@/interface/common'
import { assembleMenuList, assembleRouteList } from '@/composables/array'

export const useUserStore = defineStore('user', () => {

  let count = ref(1);
  let token = ref(null);
  let userInfo: UserInfo = reactive({
    name: null,
    age: undefined
  });
  let routeList: any = ref([]);
  let menuList: any = ref([]);

  // 计算属性
  let doubleCount = computed(() => count.value * 2);
  let squareCount = computed(() => count.value * count.value);

  async function reqUserLogin(params: object) {
    const result = await login(params);
    if (result.code === '200') {
      token.value = result.token;
      userInfo.name = result.userInfo.name;
      userInfo.age = result.userInfo.age;
    } else {
      return Promise.reject(new Error(result.message));
    }
    // 动态获取路由
    const menuRes = await getMenuInfo();
    if (menuRes.code === '200') {
      routeList.value = assembleRouteList(menuRes.menuList);
      menuList.value = assembleMenuList(menuRes.menuList);
    } else {
      return Promise.reject(new Error(menuRes.message));
    }
  }

  async function reqUserLogout() {
    const result = await logout();
    if (result.code === '200') {
      token.value = null;
      userInfo.name = null;
      userInfo.age = null;
    } else {
      return Promise.reject(new Error(result.message));
    }
  }

  async function reqUserInfo() {
    const result = await getUserInfo();
    if (result.code === '200') {
      token.value = result.token;
      userInfo.name = result.userInfo.name;
      userInfo.age = result.userInfo.age;
    } else {
      return Promise.reject(new Error(result.message));
    }

    // 动态获取路由
    const routeRes = await getMenuInfo();
    if (routeRes.code === '200') {
      routeList.value = assembleRouteList(routeRes.menuList);
      menuList.value = assembleMenuList(routeRes.menuList);
    } else {
      return Promise.reject(new Error(result.message));
    }
  }

  async function testCall(params: Test | null) {
    console.log('call user store function testCall: ' + params);
  }

  return { token, userInfo, doubleCount, menuList, routeList, reqUserLogin, reqUserLogout, reqUserInfo, testCall }
}, {
  // 默认配置，开启持久化，默认保存在localStorage
  // persist: true,
  // 自定义配置
  persist: [
    // 持久化到localStorage，不需要注释整个对象
    {
      storage: localStorage,
      paths: ['token']
    },
    {
      // key: 'my-custom-key',  // 这个 Store 将被持久化存储在 storage中 中的 my-custom-key key 中
      storage: sessionStorage,  // 这个 store 将被持久化存储在 sessionStorage中，不配置默认localStorage
      // paths: ['token', 'userInfo.name', 'routeList'], // 该 store 中, 只有 token 和 userInfo.name 被持久化，而 userInfo.age 不会被持久化
      paths: ['userInfo', 'routeList', 'menuList'] // 该 store 中, 持久化token,userInfo,routeList
    }
  ]
})