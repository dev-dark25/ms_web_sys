<template>
  <div class="default_div">
    <el-space direction="vertical" :fill=true style="width:100%;">
      <h2>Config</h2>
      <el-space wrap :size="size">
        <el-input v-model="input" style="width: 200px" placeholder="configName/configDesc" clearable />
        <el-popover placement="top" :width="170" :auto-close="2000" :visible="popoverVisible"
          content="please input parameter">
          <template #reference>
            <el-button :icon="Search" type="primary" circle @click="searchByParameter()" />
          </template>
        </el-popover>
      </el-space>
      <el-table v-loading="loading" element-loading-text="Loading..." :data="filterTableData" height="300"
        style="width: 100%">
        <el-table-column fixed prop="id" label="id" />
        <el-table-column prop="configWorld" label="configWorld" />
        <el-table-column prop="configType" label="configType" />
        <el-table-column prop="configName" label="configName" width="120" />
        <el-table-column prop="configData" label="configData" width="120" />
        <el-table-column prop="configDataType" label="configDataType" width="130" />
        <el-table-column prop="configDesc" label="configDesc" width="160" />
        <!-- <el-table-column prop="popular" label="popular" :formatter="formatter" /> -->
        <el-table-column prop="simpleName" label="simpleName" />
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

    </el-space>

    <el-dialog v-model="dialogFormVisible" title="update config" width="500">
      <el-form :model="form">
        <el-form-item label="configName" :label-width="formLabelWidth">
          <el-input v-model="form.configName" disabled />
        </el-form-item>
        <el-form-item label="configData" :label-width="formLabelWidth">
          <el-input v-model="form.configData" />
        </el-form-item>
        <!-- <el-form-item label="popular" :label-width="formLabelWidth">
          <el-select v-model="form.popular" placeholder="Select">
            <el-option label="是" value="true" />
            <el-option label="否" value="false" />
          </el-select>
        </el-form-item> -->
        <el-form-item label="simpleName" :label-width="formLabelWidth">
          <el-input v-model="form.simpleName" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <!-- <el-button @click="dialogFormVisible = false">Cancel</el-button> -->
          <el-button type="primary" plain @click="updataConfig()">
            Confirm
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogForm1Visible" title="delete config" width="500">
      <span>This is a message</span>
      <template #footer>
        <div class="dialog-footer">
          <!-- <el-button @click="dialogFormVisible = false">Cancel</el-button> -->
          <el-button type="primary" plain @click="deleteConfig()">
            Confirm
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang='ts' setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getMSConfig, updataMSConfig } from '@/axios/api'
import { Search } from '@element-plus/icons-vue'

const size = ref(10)
const popoverVisible = ref(false)
const input = ref('')
const loading = ref(true);
const search = ref('');
let list: any[] = reactive([]);
const filterTableData = computed(() =>
  list.filter(
    (data) =>
      !search.value ||
      data.configName.toLowerCase().includes(search.value.toLowerCase())
  )
)

const dialogFormVisible = ref(false)
const dialogForm1Visible = ref(false)
const formLabelWidth = '120px'
const form = reactive({
  id: null,
  configName: '',
  configData: '',
  popular: '',
  simpleName: '',
  // boolean: false,
  // type: []
})

const formatter = (row: any) => {
  return row.popular == 'true' ? '是' : '否'
}

const handleEdit = (index: number, row: any) => {
  console.log(index, row)
  dialogFormVisible.value = true
  form.id = row.id
  form.configName = row.configName
  form.configData = row.configData
  form.popular = row.popular == null ? 'false' : row.popular
  form.simpleName = row.simpleName
}
const handleDelete = (index: number, row: any) => {
  console.log(index, row)
  dialogForm1Visible.value = true
}

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const small = ref(false)
const background = ref(true)
const disabled = ref(false)

const handleSizeChange = (val: number) => {
  console.log(`${val} items per page`)
  pageSize.value = val
  getConfig();
}
const handleCurrentChange = (val: number) => {
  console.log(`current page: ${val}`)
  currentPage.value = val
  getConfig();
}

const getConfig = async () => {
  console.log('getConfig')
  const data = {
    currentPage: currentPage.value - 1,
    pageSize: pageSize.value,
    parameter: input.value
  }
  try {
    const res = await getMSConfig(data);
    if (res.code === '200') {
      list.splice(0, list.length, ...res.list);
      total.value = res.total + 1;
      loading.value = false;
    }
  } catch (error) {
    alert(error);
  }
}

const searchByParameter = async () => {
  if (input && input.value != '' && input.value != null) {
    getConfig()
  } else {
    popoverVisible.value = true
    setTimeout(() => {
      popoverVisible.value = false
    }, 2000);
  }
}

const updataConfig = async () => {
  console.log('updataConfig')
  try {
    const res = await updataMSConfig(form);
    if (res.code === '200') {
      dialogFormVisible.value = false
      ElMessage({
        message: 'update server config success.',
        type: 'success',
      })
      getConfig()
    }
  } catch (error) {
    alert(error);
  }
}

const deleteConfig = async () => {
  dialogForm1Visible.value = false
  ElMessage({
    message: 'delete server config success.',
    type: 'success',
  })
  getConfig()
}

onMounted(() => {
  setTimeout(() => {
    getConfig();
  }, 1000);
})

</script>

<style lang="less" scoped>
.el-table {
  // color: #7fffd4;
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
