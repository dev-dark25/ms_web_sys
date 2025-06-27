<template>
  <div class="default_div">
    <h2>Vue3开发项目，Pinia状态管理，pinia-plugin-persistedstate状态持久化，Vite构建，TypeScript支持</h2>
    <h2>UI组件Element+，Axios网络请求库，Less CSS预处理，Mock本地调试，及远程调用代理配置</h2>
    <h2>ESlint代码规则校验</h2>
    <el-button type="success" link @click="this.$router.push('/vue3Dev');">
      <h3>开发准备工作</h3>
    </el-button>
    <el-button type="success" link @click="configDialogVisible = true">
      <h3>相关配置文件</h3>
    </el-button>
    <el-divider />
    <h2>使用Vue2，选项式API开发，参考Default.vue</h2>
    <el-button type="success" link @click="dialogVisible = true">
      <h3>选项式API简单功能演示</h3>
    </el-button>
    <el-divider />
    <h2>建议使用setup()函数和组合式API，参考
      <el-button type="success" link @click="this.$router.push('/simple');">
        <h2>Simple.vue</h2>
      </el-button>
      ，Login.vue
    </h2>
    <el-divider />
    <h2>问题、解决、经验
      <el-button type="success" link @click="this.$router.push('/experiences');">
        <h2>Experiences.vue</h2>
      </el-button>
    </h2>

    <!-- dialog可以提取为一个组件 -->
    <el-dialog v-model="configDialogVisible" title="配置文件" width="30%">
      <div>环境变量：.env.development，.env.production</div>
      <div>Vite构建运行打包：vite.config.ts，package.json</div>
      <div>TypeScript：tsconfig.json</div>
      <div>ESlint代码规则校验：.eslintrc.cjs</div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click=" configDialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click=" configDialogVisible = false">
            Confirm
          </el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogVisible" title="选项式API简单功能" width="30%" destroy-on-close center>
      <div id="countLable">测试计算属性incrementCount: {{ incrementCount }}</div>
      <div>测试调用方法checkCount: {{ checkCount() }}</div>
      <div>动态时间nowTime: {{ nowTime }}</div>
      <div>测试侦听器：<input v-model="testWatch" /></div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click=" dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click=" dialogVisible = false">
            Confirm
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<!-- 不定义TypeScript，和setup函数 -->
<script>
import { mapState, mapActions } from 'pinia'
import { useUserStore } from '@/stores/user'
import { dateFormat } from '@/composables/date'

export default {
  // props: ['title','likes'],
  props: {
    title: String,
    likes: Number
  },
  data() {
    return {
      count: 0,
      userName: null,
      testWatch: null,
      timer: null,
      nowTime: null,
      configDialogVisible: false,
      dialogVisible: false,
    }
  },
  // created: 在模板渲染成html前调用，即通常初始化某些属性值，然后再渲染成视图。
  // mounted: 在模板渲染成html后调用，通常是初始化页面完成后，再对html的dom节点进行一些需要的操作。
  // 位于Vue组件生命周期中不同阶段， 通常created使用的次数多，而mounted通常是在一些插件的使用或者组件的使用中进行操作。
  created() {
    this.userName = this.userInfo.name; // 读取useUserStore.userInfo
    // console.log(document.getElementById('countLable')?.innerHTML) // 输出undefined
    this.count = 1  // 数据属性也可以被更改
    // 定时器
    this.timer = setInterval(() => {
      this.nowTime = dateFormat(new Date());
    }, 1000);
  },
  mounted() {
    // console.log(document.getElementById('countLable')?.innerHTML)
    this.count = 2  // 数据属性也可以被更改
  },
  beforeDestroy() {
    // 组件销毁前，必须清除定时器
    if (this.timer) {
      clearInterval(this.timer);
    }
  },
  // 侦听器
  watch: {
    // 每当 testWatch 改变时，这个函数就会执行
    testWatch(newValue, oldValue) {
      // 调用方法
      this.consoleLogMessage('testWatch is changed');
      if (newValue.includes('aaa')) {
        this.consoleLogMessage('testWatch is includes aaa');
      }
      this.count++;
    }
  },
  // 计算属性
  computed: {
    // 允许读取 this.userInfo
    ...mapState(useUserStore, ['userInfo']),
    // 一个计算属性的 getter
    incrementCount() {
      // return this.count++
      return this.count > 3 ? 'Yes' : 'No'
    }
  },
  methods: {
    // 允许访问 this.testCall()
    ...mapActions(useUserStore, ['testCall']),
    checkCount() {
      return this.count > 7 ? 'Yes' : 'No'
    },
    consoleLogMessage(str) {
      console.log(str);
      // this.callStoreFuc();
    },
    callStoreFuc() {
      this.testCall(); // 访问useUserStore.testCall()
    }
  }
}
</script>

<style></style>
