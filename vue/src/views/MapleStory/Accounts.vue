<template>
  <div class="default_div">
      <h2>Account</h2>
      <el-table v-loading="loading" element-loading-text="Loading..." :data="filterTableData" height="300"
        style="width: 100%">
        <el-table-column fixed prop="id" label="id" />
        <el-table-column prop="name" label="name" />
        <el-table-column prop="password" label="password" width="460" />
        <el-table-column prop="pin" label="pin" />
        <el-table-column prop="pic" label="pic" />
        <el-table-column prop="loggedin" label="loggedin" width="100" />
        <el-table-column prop="lastlogin" label="lastlogin" width="160" />
        <el-table-column prop="createdat" label="createdat" width="160" />
        <el-table-column prop="birthday" label="birthday" width="100" />
        <el-table-column prop="banned" label="banned" />
        <el-table-column prop="banreason" label="banreason" width="100" />
        <el-table-column prop="macs" label="macs" width="280" />
        <el-table-column prop="nxCredit" label="nxCredit" width="100" />
        <el-table-column prop="maplePoint" label="maplePoint" width="100" />
        <el-table-column prop="nxPrepaid" label="nxPrepaid" width="100" />
        <el-table-column prop="characterslots" label="characterslots" width="120" />
        <el-table-column prop="gender" label="gender" />
        <el-table-column prop="tempban" label="tempban" width="160" />
        <el-table-column prop="greason" label="greason" />
        <el-table-column prop="tos" label="tos" />
        <el-table-column prop="sitelogged" label="sitelogged" width="100" />
        <el-table-column prop="webadmin" label="webadmin" width="100" />
        <el-table-column prop="nick" label="nick" />
        <el-table-column prop="mute" label="mute" />
        <el-table-column prop="email" label="email" width="120" />
        <el-table-column prop="ip" label="ip" />
        <el-table-column prop="rewardpoints" label="rewardpoints" width="120" />
        <el-table-column prop="votepoints" label="votepoints" width="100" />
        <el-table-column prop="hwid" label="hwid" width="100" />
        <el-table-column prop="language" label="language" width="100" />
        <el-table-column fixed="right" label="Operations" width="180">
          <template #header>
            <el-input v-model="search" size="small" placeholder="Type to search name" />
          </template>
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.$index, scope.row)">Edit</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 30, 40]"
      :small="small" :disabled="disabled" :background="background" layout="total, sizes, prev, pager, next, jumper"
      :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    <div class="flex items-center mb-4">
      <el-radio-group v-model="small" class="mr-4">
        <el-radio-button :label="false">default</el-radio-button>
        <el-radio-button :label="true">small</el-radio-button>
      </el-radio-group>
      <div>
        background:
        <el-switch v-model="background" class="ml-2" />
      </div>
      <div class="ml-4">
        disabled: <el-switch v-model="disabled" class="ml-2" />
      </div>
    </div> -->

  </div>
</template>

<script lang='ts' setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getMSAccounts } from '@/axios/api'

let loading = ref(true);
const search = ref('');
let list: any[] = reactive([]);
const filterTableData = computed(() =>
  list.filter(
    (data) =>
      !search.value ||
      data.name.toLowerCase().includes(search.value.toLowerCase())
  )
)

const handleEdit = (index: number, row: any) => {
  console.log(index, row)
}
const handleDelete = (index: number, row: any) => {
  console.log(index, row)
}

// const currentPage = ref(4)
// const pageSize = ref(10)
// const small = ref(false)
// const background = ref(false)
// const disabled = ref(false)
// const total = ref(77)

// const handleSizeChange = (val: number) => {
//   console.log(`${val} items per page`)
// }
// const handleCurrentChange = (val: number) => {
//   console.log(`current page: ${val}`)
// }



const getAccounts = async () => {
  try {
    const res = await getMSAccounts();
    if (res.code === '200') {
      list.splice(0, list.length, ...res.list);
      loading.value = false;
    }
  } catch (error) {
    alert(error);
  }
}

onMounted(() => {
  setTimeout(() => {
    getAccounts();
  }, 1000);
})

</script>

<style lang="less" scoped>
.el-table {
  color: black;

  --el-table-bg-color: #ff000000; // 表背景色
  --el-table-tr-bg-color: #ff000000; // 行背景色
  --el-table-header-bg-color: #ff000000; // 表头背景色
  --el-table-header-text-color: black; // 表头字体色

  // 左右固定列背景色
  :deep(td.el-table-fixed-column--left),
  :deep(td.el-table-fixed-column--right) {
    background: #ff000000;
  }
}

.el-pagination {
  --el-text-color-regular: #1e90ff; // 分页栏字体色
}
</style>
