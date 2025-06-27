<template>
  <div class="default_div">
    <el-button type="success" link @click="setupDialogVisible = true">
      <h3>setup()中注册生命周期钩子</h3>
    </el-button>
    <el-divider />
    <h3>使用导入的组合式API的属性：{{ testName }}</h3>
    <el-button type="primary" @click="testComposables()">调用导入的组合式API的方法</el-button>
    <el-button type="primary" @click="testComposables1()">调用组合式API的方法</el-button>    

    <el-dialog v-model="setupDialogVisible" title="setup()中注册生命周期钩子" width="30%">
      <span>
        在 setup 中，可以通过 onX 的方式注册 生命周期钩子。<br />
        1、beforeCreate -> 使用 setup()<br />
        2、created -> 使用 setup()<br />
        3、beforeMount -> onBeforeMount<br />
        4、mounted -> onMounted<br />
        5、beforeUpdate -> onBeforeUpdate<br />
        6、updated -> onUpdated<br />
        7、beforeDestroy -> onBeforeUnmount<br />
        8、destroyed -> onUnmounted<br />
        9、errorCaptured -> onErrorCaptured<br />
        实现参考：https://cn.vuejs.org/api/composition-api-lifecycle.html
      </span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="setupDialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="setupDialogVisible = false">
            Confirm
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang='ts' setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { testName, storeUserInfo, testComposables, increment } from '@/composables/api' // 导入组合式API属性和功能

const setupDialogVisible = ref(false);

const router = useRouter(); // 获取路由router
const store = useUserStore(); // 获取导入的Store

// console.log('store.doubleCount: ' + store.doubleCount); // 使用导入的store的state
// console.log('store.userInfo.name: ' + store.userInfo.name); // 使用导入的store的state
// console.log('storeUserInfo.name: ' + storeUserInfo.name); // 使用导入的组合式API的属性

function testComposables1() {
  // console.log('testComposables1: ' + increment(1)); // 调用导入的组合式API的方法
}

// 监听属性
watch(setupDialogVisible, async (newValue, oldValue) => {
  console.log('setupDialogVisible: ' + oldValue + ' -> ' + newValue);
})


</script>

<style></style>
