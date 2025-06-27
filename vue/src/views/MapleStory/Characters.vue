<template>
  <div class="default_div">
    <h2>Characters</h2>
    <el-table v-loading="loading" element-loading-text="Loading..." :data="filterTableData" height="300"
      style="width: 100%">
      <el-table-column fixed prop="id" label="id" />
      <el-table-column prop="accountid" label="accountid" width="100" />
      <el-table-column prop="world" label="world" />
      <el-table-column prop="name" label="name" />
      <el-table-column prop="level" label="level" />
      <el-table-column prop="exp" label="exp" />
      <el-table-column prop="gachaexp" label="gachaexp" width="100" />
      <el-table-column prop="str" label="str" />
      <el-table-column prop="dex" label="dex" />
      <el-table-column prop="luk" label="luk" />
      <el-table-column prop="int" label="int" />
      <el-table-column prop="hp" label="hp" />
      <el-table-column prop="mp" label="mp" />
      <el-table-column prop="maxhp" label="maxhp" />
      <el-table-column prop="maxmp" label="maxmp" />
      <el-table-column prop="meso" label="meso" width="120" />
      <el-table-column prop="hpMpUsed" label="hpMpUsed" width="100" />
      <el-table-column prop="job" label="job" />
      <el-table-column prop="skincolor" label="skincolor" width="100" />
      <el-table-column prop="gender" label="gender" />
      <el-table-column prop="fame" label="fame" />
      <el-table-column prop="fquest" label="fquest" />
      <el-table-column prop="hair" label="hair" />
      <el-table-column prop="face" label="face" />
      <el-table-column prop="ap" label="ap" />
      <el-table-column prop="sp" label="sp" width="100" />
      <el-table-column prop="map" label="map" />
      <el-table-column prop="spawnpoint" label="spawnpoint" width="120" />
      <el-table-column prop="gm" label="gm" />
      <el-table-column prop="party" label="party" />
      <el-table-column fixed="buddyCapacity" label="buddyCapacity" />
      <el-table-column fixed="createdate" label="createdate" />
      <el-table-column fixed="equipslots" label="equipslots" />
      <el-table-column fixed="useslots" label="useslots" />
      <el-table-column fixed="setupslots" label="setupslots" />
      <el-table-column fixed="etcslots" label="etcslots" />
      <el-table-column fixed="familyId" label="familyId" />
      <el-table-column prop="dojoPoints" label="dojoPoints" width="100" />
      <el-table-column prop="lastLogoutTime" label="lastLogoutTime" width="160" />
      <el-table-column prop="lastExpGainTime" label="lastExpGainTime" width="160" />
      <el-table-column prop="partySearch" label="partySearch" width="120" />
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

    <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 30, 40]"
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
    </div>
  </div>
</template>

<script lang='ts' setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getMSCharacters } from '@/axios/api'

const loading = ref(true);
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

const currentPage = ref(1)
const pageSize = ref(10)
const small = ref(false)
const background = ref(true)
const disabled = ref(false)
const total = ref(0)

const handleSizeChange = (val: number) => {
  console.log(`${val} items per page`)
}
const handleCurrentChange = (val: number) => {
  console.log(`current page: ${val}`)
}

const getCharacters = async () => {
  const data = {
    currentPage: currentPage.value,
    pageSize: pageSize.value
  }
  try {
    const res = await getMSCharacters(data);
    if (res.code === '200') {
      list.splice(0, list.length, ...res.list);
      total.value = res.total;
      loading.value = false;
    }
  } catch (error) {
    alert(error);
  }
}

onMounted(() => {
  setTimeout(() => {
    getCharacters();
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
