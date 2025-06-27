// 组合式API对外提供方法和属性    示例参考Simple.vue
import { useUserStore } from '@/stores/user'

const store = useUserStore(); // 获取导入的Store
const storeUserInfo = store.userInfo; // 赋值给自定义的storeUserInfo

const testName: string = 'Composables';

function testComposables() {
  console.log('testComposables method');
}

function increment(count: number) {
  return count++;
}

// 导出写法一
export { storeUserInfo, testName, testComposables, increment }

// 导出写法二
export function testComposables1() {
  console.log('testComposables1 method');
}
