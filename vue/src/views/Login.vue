<template>
  <div class="div background_div">
    <div class="login_div">
      <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" :size="formSize" status-icon>
        <h1>Heaven MS</h1>
        <el-form-item prop="account">
          <el-input class="input" v-model="ruleForm.account" placeholder="账号" maxlength="20" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input class="input" type="password" v-model="ruleForm.password" placeholder="密码" maxlength="10"
            show-password />
        </el-form-item>
        <el-form-item>
          <el-button class="button" type="primary" @click="submitForm(ruleFormRef)"
            :disabled="loginButtonDisabled">登录</el-button>
          <!-- <el-button @click="resetForm(ruleFormRef)">重置</el-button> -->
        </el-form-item>
        <!-- <h5>没有账号？<el-button type="success" link @click="router.push('/register')">点击注册</el-button></h5> -->
      </el-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
// import { useCounterStore } from '@/stores/counter'

const router = useRouter(); // 获取路由router

// let account = ref(''); // 自定义响应式属性
// let password = ref(''); // 自定义响应式属性

// 可以在组件中的任意位置访问 `store` 变量 ✨
const store = useUserStore(); // 获取导入的Store
// const counterStore = useCounterStore(); // 获取导入的Store
// counterStore.increment() // 使用store的方法
// console.log(counterStore.count); // 使用store的属性

const formSize = ref('large')
const ruleFormRef = ref<FormInstance>()
const ruleForm = reactive({
  account: '',
  password: ''
})
const loginButtonDisabled = ref(false);

const rules = reactive<FormRules>({
  account: [
    { required: true, message: 'Please input Activity account', trigger: 'change' },
    { min: 5, max: 20, message: 'Length should be 5 to 20', trigger: 'change' },
  ],
  password: [
    { required: true, message: 'Please input password', trigger: 'change', },
    { min: 6, max: 10, message: 'Length should be 6 to 10', trigger: 'change' },
  ]
})

const submitForm = async (formEl: FormInstance | undefined) => {
  loginButtonDisabled.value = true;
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      // console.log('submit!')
      login();
    } else {
      loginButtonDisabled.value = false;
      console.log('error submit!', fields)
    }
  })
}

// const resetForm = (formEl: FormInstance | undefined) => {
//   if (!formEl) return
//   formEl.resetFields()
// }

async function login() {
  try {
    const data = {
      userName: ruleForm.account,
      password: ruleForm.password
    }
    await store.reqUserLogin(data);
    // const result = await store.userLogin(data);  // 有返回值
    // router.push('/main');
    let toPath: string = <string>router.currentRoute.value.query.redirect || '/main';
    router.push(toPath);
  } catch (error) {
    loginButtonDisabled.value = false;
    alert(error);
  }
}
</script >

<style lang="less" scoped>
.background_div {
  // background-image: url(../src/assets/img/ly_qcz.png);
  background-image: url('@/assets/img/ms_mfml.jpg');
  /* background-repeat: no-repeat; */
  background-position: center;
  background-size: 100% 100%;
}

.login_div {
  text-align: center;
  border-radius: 20px;
  width: 20rem;
  height: 20rem;
  position: absolute;
  left: 75%;
  top: 40%;
  transform: translate(-50%, -50%);
  // box-shadow: 4px 4px 4px #2fef52, -4px -4px 4px #2fef52;

  h1 {
    color: #fcf953;
    margin: 1rem 0;
  }

  .input {
    width: 14rem;
  }

  .button {
    width: 5rem;
  }

  h5,
  a {
    color: #f5fc68;
  }

  :deep(.el-form-item__content) {
    justify-content: center;

    .el-form-item__error {
      left: 3rem;
    }
  }
}
</style>
