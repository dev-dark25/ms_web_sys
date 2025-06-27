<template>
  <div class="header_div">
    <div class="time_div">
      <h1 @click="changeView()">Welcome!</h1>
      <label>{{ nowTime }}</label>
    </div>
    <div class="info_div">
      <div>
        <el-icon>
          <UserFilled />
        </el-icon>
      </div>
      <label @click="clickUserName()">{{ userName }}</label>
      <el-button type="primary" link :disabled="logoutButtongDisabled" @click="logout()">退出</el-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { dateFormat } from '@/composables/date'

const router = useRouter();
const store = useUserStore();
const userName = store.userInfo.name;

const logoutButtongDisabled = ref(false);

const nowTime = ref('');
let timer: any;

onMounted(() => {
  timer = setInterval(() => {
    nowTime.value = dateFormat(new Date());
  }, 1000);
})
onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
})

const changeView = () => {
  if (router.currentRoute.value.fullPath !== '/default') {
    router.push('/default');
  }
}

const clickUserName = () => {
  alert('Hello ' + userName);
}

async function logout() {
  logoutButtongDisabled.value = true;
  try {
    await store.reqUserLogout();
    // store.$reset();
    router.push('/');
  } catch (error) {
    console.log(error);
    alert(error);
  }
}

</script>

<style lang="less" scoped>
.header_div {
  height: 5rem;
  text-align: center;

  .time_div {
    float: left;
    padding: 0 0 0 2rem;
    color: #deb887;

    h1:hover {
      cursor: pointer;
    }
  }

  .info_div {
    float: right;
    padding: 3rem 2rem 0 0;

    div {
      display: inline-block;
      vertical-align: middle;
    }

    label {
      margin: 0 0.5rem;
    }

    label:hover {
      cursor: pointer;
    }

    .el-button {
      vertical-align: baseline;
    }
  }
}
</style>
