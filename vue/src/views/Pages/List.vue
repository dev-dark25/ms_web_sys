<template>
  <div class="default_div">
    <h2>Account List</h2>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column fixed prop="name" label="Name" width="100" />
      <el-table-column prop="age" label="Age" width="100" />
      <el-table-column prop="account" label="Account" width="100" />
      <el-table-column prop="roleName" label="RoleName" width="100" />
      <el-table-column prop="career" label="Career" width="100" />
      <el-table-column prop="lever" label="Lever" width="500" />
      <el-table-column prop="createTime" label="CreateTime" width="160" />
      <el-table-column prop="lastTime" label="LastTime" width="160" />
      <el-table-column fixed="right" label="Operations" width="120">
        <template #default="scope">
          <el-button link type="primary" size="small" @click.prevent="deleteRow(scope.$index)">
            Remove
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <h2 style="margin-top: 2rem;">Menu List</h2>
    <el-table v-loading="loading" element-loading-text="Loading..." :data="filterTableData" height="300"
      style="width: 100%">
      <el-table-column fixed prop="name" label="Name" width="120" />
      <el-table-column prop="path" label="Path" width="120" />
      <el-table-column prop="icon" label="Icon" width="120" />
      <el-table-column prop="title" label="Title" width="120">
        <template #default="scope">
          <el-popover effect="light" trigger="hover" placement="top" width="auto">
            <template #default>
              <div>中文: 目录名</div>
              <div>title: {{ scope.row.title }}</div>
            </template>
            <template #reference>
              <el-tag>{{ scope.row.title }}</el-tag>
            </template>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="isMenu" label="IsMenu" width="120" />
      <el-table-column prop="menuNum" label="MenuNum" width="120" />
      <el-table-column prop="vuePage" label="VuePage" />
      <el-table-column label="Operations" width="180">
        <template #header>
          <el-input v-model="search" size="small" placeholder="Type to search path" />
        </template>
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.$index, scope.row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script lang='ts' setup>
import { ref, reactive, onMounted, computed } from 'vue'
import type { MenuInfo } from '@/interface/common'
import { getMenuInfo } from '@/axios/api'

let loading = ref(true);
// let menuList: any = ref([]); // ref
let menuList: MenuInfo[] = reactive([]);  // reactive

const search = ref('');
const filterTableData = computed(() =>
  menuList.filter(
    (data) =>
      !search.value ||
      data.path.toLowerCase().includes(search.value.toLowerCase())
  )
)

const deleteRow = (index: number) => {
  tableData.splice(index, 1)
}

const handleEdit = (index: number, row: MenuInfo) => {
  console.log(index, row)
  console.log(row.icon)
}
const handleDelete = (index: number, row: MenuInfo) => {
  console.log(index, row)
}

const getMenuList = async () => {
  try {
    const menuRes = await getMenuInfo();
    if (menuRes.code === '200') {
      // menuList.value = menuRes.menuList; // ref
      menuList.push(...menuRes.menuList); // reactive
      loading.value = false;
    }
  } catch (error) {
    alert(error);
  }
}

onMounted(() => {
  setTimeout(() => {
    getMenuList();
  }, 1000);
})


interface User {
  name: string
  age: string
  account: string
  roleName: string
  career: string
  lever: string
  createTime: string
  lastTime: string
}

const tableData: User[] = reactive([
  {
    name: 'zz',
    age: '7',
    account: 'zz',
    roleName: 'soldier',
    career: 'soldier',
    lever: '2',
    createTime: '2023-1-1 12:12:12',
    lastTime: '2023-1-1 12:12:12',
  },
  {
    name: 'zz',
    age: '7',
    account: 'zz',
    roleName: 'soldier',
    career: 'soldier',
    lever: '2',
    createTime: '2023-1-1 12:12:12',
    lastTime: '2023-1-1 12:12:12',
  },
  {
    name: 'zz',
    age: '7',
    account: 'zz',
    roleName: 'soldier',
    career: 'soldier',
    lever: '2',
    createTime: '2023-1-1 12:12:12',
    lastTime: '2023-1-1 12:12:12',
  }
])
</script>

<style lang="less" scoped>
// :root {
//   --el-fill-color-blank: #ff000000;
//   --el-bg-color: #ff000000;
// }

.el-table {
  color: #7fffd4;

  thead {
    color: cyan;
  }

  --el-table-bg-color: #ff000000; // 表背景色
  --el-table-tr-bg-color: #ff000000; // 行背景色
  --el-table-header-bg-color: #ff000000; // 表头背景色

  // .el-table__cell {
  //   background: #ff000000 !important;
  // }

  // 左右固定列背景色
  :deep(td.el-table-fixed-column--left),
  :deep(td.el-table-fixed-column--right) {
    background: #ff000000;
  }
}
</style>
