<template>
  <div class="default_div">
    <el-space direction="vertical" :fill=true style="width:100%;">
      <h2>Command</h2>
      <el-space wrap :size="size">
        <el-input v-model="input" style="width: 200px" placeholder="command/description" clearable />
        <el-select v-model="selectValue" placeholder="Select" style="width: 100px">
          <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button :icon="Search" type="primary" circle @click="getCommand()" />
      </el-space>
      <el-table v-loading="loading" element-loading-text="Loading..." :data="filterTableData" height="300"
        style="width: 100%">
        <el-table-column fixed prop="id" label="id" />
        <el-table-column prop="command" label="command" />
        <el-table-column prop="description" label="description" width="200" />
        <el-table-column prop="gmLevelPrimitive" label="gmLevelPrimitive" />
        <el-table-column prop="gmLevel" label="gmLevel" />
        <el-table-column label="isWork">
          <template #default="scope">
            <el-tag :type="scope.row.isWork ? 'success' : 'danger'">{{ isWorkFormat(scope.row.isWork)
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="Operations" width="180">
          <template #header>
            <el-input v-model="search" size="small" placeholder="Type to search description" />
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

    <el-dialog v-model="dialogFormVisible" title="update command" width="500">
      <el-form :model="form">
        <el-form-item label="command" :label-width="formLabelWidth">
          <el-input v-model="form.command" disabled />
        </el-form-item>
        <el-form-item label="description" :label-width="formLabelWidth">
          <el-input v-model="form.description" disabled />
        </el-form-item>
        <el-form-item label="gmLevelPrimitive" :label-width="formLabelWidth">
          <el-input v-model="form.gmLevelPrimitive" disabled />
        </el-form-item>
        <el-form-item label="gmLevel" :label-width="formLabelWidth">
          <el-input v-model="form.gmLevel" />
        </el-form-item>
        <el-form-item label="isWork" :label-width="formLabelWidth">
          <el-select v-model="form.isWork" placeholder="Select">
            <el-option label="是" :value=true />
            <el-option label="否" :value=false />
          </el-select>
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

    <el-dialog v-model="dialogForm1Visible" title="delete command" width="500">
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
import { getMSCommand, updataMSCommand } from '@/axios/api'
import { Search } from '@element-plus/icons-vue'

const size = ref(10)
const input = ref('')
const selectValue = ref(-1)
const options = [
  {
    value: -1,
    label: 'all',
  },
  {
    value: 0,
    label: '0',
  },
  {
    value: 1,
    label: '1',
  },
  {
    value: 2,
    label: '2',
  },
  {
    value: 3,
    label: '3',
  },
  {
    value: 4,
    label: '4',
  },
  {
    value: 5,
    label: '5',
  },
  {
    value: 6,
    label: '6',
  }
]
const loading = ref(true);
const search = ref('');
let list: any[] = reactive([]);
const filterTableData = computed(() =>
  list.filter(
    (data) =>
      !search.value ||
      data.description.toLowerCase().includes(search.value.toLowerCase())
  )
)

const dialogFormVisible = ref(false)
const dialogForm1Visible = ref(false)
const formLabelWidth = '120px'
const form = reactive({
  id: null,
  command: '',
  description: '',
  gmLevelPrimitive: '',
  gmLevel: '',
  isWork: ''
  // boolean: false,
  // type: []
})

const isWorkFormat = (param: any) => {
  return param ? '是' : '否'
}

const handleEdit = (index: number, row: any) => {
  console.log(index, row)
  dialogFormVisible.value = true
  form.id = row.id
  form.command = row.command
  form.description = row.description
  form.gmLevelPrimitive = row.gmLevelPrimitive
  form.gmLevel = row.gmLevel
  form.isWork = row.isWork
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
  getCommand();
}
const handleCurrentChange = (val: number) => {
  console.log(`current page: ${val}`)
  currentPage.value = val
  getCommand();
}

const getCommand = async () => {
  console.log('getCommand')
  const data = {
    currentPage: currentPage.value - 1,
    pageSize: pageSize.value,
    parameter: input.value,
    gmLevel: selectValue.value
  }
  try {
    const res = await getMSCommand(data);
    if (res.code === '200') {
      list.splice(0, list.length, ...res.list);
      total.value = res.total + 1;
      loading.value = false;
    }
  } catch (error) {
    alert(error);
  }
}

const updataConfig = async () => {
  console.log('updataCommand')
  try {
    const res = await updataMSCommand(form);
    if (res.code === '200') {
      dialogFormVisible.value = false
      ElMessage({
        message: 'update command config success.',
        type: 'success',
      })
      getCommand()
    }
  } catch (error) {
    alert(error);
  }
}

const deleteConfig = async () => {
  dialogForm1Visible.value = false
  ElMessage({
    message: 'delete command config success.',
    type: 'success',
  })
  getCommand()
}

onMounted(() => {
  setTimeout(() => {
    getCommand();
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
