<template>
  <div class="aside_div">
    <!-- 支持二级目录 -->
    <el-menu :default-active="currentPath" background-color="#ff000000" text-color="rgb(255 0 185)" active-text-color="#74f585"
      @open="handleOpen" @close="handleClose" router>
      <div v-for="item in menuList">
        <div v-if="item.isMenu">
          <el-sub-menu :index="item.path">
            <template #title>
              <el-icon>
                <icon-menu v-if="item.icon === 'icon-menu'" />
                <setting v-if="item.icon === 'setting'" />
                <location v-if="item.icon === 'location'" />
                <document v-if="item.icon === 'document'" />
                <money v-if="item.icon === 'money'" />
                <place v-if="item.icon === 'place'" />
                <food v-if="item.icon === 'food'" />
                <ice-tea v-if="item.icon === 'ice-tea'" />
                <Lollipop v-if="item.icon === 'lollipop'"/>
              </el-icon>
              {{ item.title }}
            </template>
            <template v-for="item1 in item.list">
              <el-menu-item :index="item1.path">
                <el-icon>
                  <icon-menu v-if="item1.icon === 'icon-menu'" />
                  <setting v-if="item1.icon === 'setting'" />
                  <location v-if="item1.icon === 'location'" />
                  <document v-if="item1.icon === 'document'" />
                  <money v-if="item1.icon === 'money'" />
                  <place v-if="item1.icon === 'place'" />
                  <food v-if="item1.icon === 'food'" />
                  <ice-tea v-if="item1.icon === 'ice-tea'" />
                  <Lollipop v-if="item1.icon === 'lollipop'"/>
                </el-icon>
                {{ item1.title }}
              </el-menu-item>
            </template>
          </el-sub-menu>
        </div>
        <div v-else>
          <el-menu-item :index="item.path">
            <el-icon>
              <icon-menu v-if="item.icon === 'icon-menu'" />
              <setting v-if="item.icon === 'setting'" />
              <location v-if="item.icon === 'location'" />
              <document v-if="item.icon === 'document'" />
              <money v-if="item.icon === 'money'" />
              <place v-if="item.icon === 'place'" />
              <food v-if="item.icon === 'food'" />
              <ice-tea v-if="item.icon === 'ice-tea'" />
              <promotion v-if="item.icon === 'promotion'" />
            </el-icon>
            {{ item.title }}
          </el-menu-item>
        </div>
      </div>
    </el-menu>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter, onBeforeRouteUpdate } from 'vue-router'
// import { storeToRefs } from 'pinia'
import { useUserStore } from '@/stores/user'
import { Document, Menu as IconMenu, Location, Setting, Money, Place, Food, IceTea, Promotion, Lollipop } from '@element-plus/icons-vue'
import type { MenuInfo } from '@/interface/common'
const currentPath = ref('');
currentPath.value = useRouter().currentRoute.value.path;  // 获取当前路由path
const store = useUserStore(); // 获取导入的Store
let menuList: MenuInfo[] = store.menuList;
// console.log('store.routeList.length: ' + store.menuList.length);

// 使用storeToRefs可以保证解构出来的数据也是响应式的
// const { userInfo, menuList } = storeToRefs(store);
// console.log('menuList.length: ' + menuList.value.length);

onBeforeRouteUpdate(to => {
  // if (to.path === '/default') {
  currentPath.value = to.path;
  // }
})

// 函数两种写法，任选其一
// function handleOpen(key: string, keyPath: string[]) {
//   console.log(key, keyPath)
// }
const handleOpen = (key: string, keyPath: string[]) => {
  // console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  // console.log(key, keyPath)
}
</script>

<style lang="less" scoped>
.aside_div {
  width: 12rem;
  // padding-top: 1rem;
  overflow-x: hidden;
  overflow-y: auto;

  :deep(.el-menu) {
    // background-color: #ff000000;
    border: none;

    // .el-menu-item,
    // .el-sub-menu__title {
    //   color: #ffc0cb;
    // }

    // .is-active {
    //   color: #7fffd4;
    // }

    .el-menu-item:hover,
    .el-sub-menu__title:hover {
      // background-color: #7fffd4;
      background-color: #409eff;
    }

  }
}
</style>
