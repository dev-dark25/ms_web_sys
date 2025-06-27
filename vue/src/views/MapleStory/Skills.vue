<template>
  <div class="default_div">
    <h2>Skill</h2>
    <el-input v-model="characterid" type="number" placeholder="Please input" clearable style="width: 200px" />
    <el-button key="danger" type="danger" :disabled="searchButton" :icon="Search" text @click="searchSkills">search by
      character id:</el-button>
    <el-table v-loading="loading" element-loading-text="Loading..." :data="filterTableData" height="300"
      style="width: 100%">
      <el-table-column fixed prop="id" label="id" />
      <el-table-column prop="characterid" label="characterid" />
      <el-table-column prop="skillid" label="skillid" />
      <el-table-column prop="skilllevel" label="skilllevel" />
      <el-table-column prop="masterlevel" label="masterlevel" />
      <el-table-column prop="expiration" label="expiration" />
      <el-table-column fixed="right" label="Operations" width="180">
        <template #header>
          <el-input v-model="search" size="small" placeholder="Type to search skillid" />
        </template>
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.$index, scope.row)">Edit</el-button>
          <el-button size="small" type="danger" disabled @click="handleDelete(scope.$index, scope.row)">Delete</el-button>
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

    <el-dialog v-model="dialogFormVisible" width="40%" title="Skills">
      <el-form :model="form">
        <el-form-item label="name demo" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off" /><!-- autocomplete自动补全属性 -->
        </el-form-item>
        <el-form-item label="Zones demo" :label-width="formLabelWidth">
          <el-select v-model="form.region" placeholder="Please select a zone">
            <el-option label="Zone No.1" value="shanghai" />
            <el-option label="Zone No.2" value="beijing" />
          </el-select>
        </el-form-item>

        <el-form-item label="id" :label-width="formLabelWidth">
          <label>{{ form.id }}</label>
        </el-form-item>
        <el-form-item label="characterid" :label-width="formLabelWidth">
          <label>{{ form.characterid }}</label>
        </el-form-item>
        <el-form-item label="skillid" :label-width="formLabelWidth">
          <label>{{ form.skillid }}</label>
        </el-form-item>
        <el-form-item label="skilllevel" :label-width="formLabelWidth">
          <el-input v-model="form.skilllevel" />
        </el-form-item>
        <el-form-item label="masterlevel" :label-width="formLabelWidth">
          <el-input v-model="form.masterlevel" />
        </el-form-item>

      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible = false">Cancel</el-button>
          <el-button type="primary" @click="dialogFormVisible = false">
            Confirm
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang='ts' setup>
import { Search } from '@element-plus/icons-vue'
import { ref, reactive, onMounted, onBeforeUnmount, computed, watch } from 'vue'
import { getMSSkills } from '@/axios/api'
import { debounce, throttle } from 'lodash';

const loading = ref(false);
const search = ref('');
let list: any[] = reactive([]);
const filterTableData = computed(() =>
  list.filter(
    (data) =>
      !search.value ||
      data.skillid.toString().toLowerCase().includes(search.value.toLowerCase())
  )
)

// 搜索框、按钮
const characterid = ref('');
const searchButton = ref(true);

const handleEdit = (index: number, row: any) => {
  console.log(index, row)

  dialogFormVisible.value = true;
  form.value = row;
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
  loading.value = true;
  pageSize.value = val;
  searchSkills();
}
const handleCurrentChange = (val: number) => {
  console.log(`current page: ${val}`)
  loading.value = true;
  currentPage.value = val;
  searchSkills();
}

// 防抖
const editCharacterId = debounce((newValue, oldValue) => {
  console.log(oldValue + '->' + newValue);
  // getSkills();
}, 3000);
// 节流
const searchSkills = throttle(() => {
  console.log('searchSkills');
  getSkills();
}, 10000);

const getSkills = async () => {
  const data = {
    characterId: parseInt(characterid.value),
    currentPage: currentPage.value,
    pageSize: pageSize.value
  }
  try {
    const res = await getMSSkills(data);
    if (res.code === '200') {
      // reactive([]) 修改原数组1
      // list.length = 0;
      // list.push(...res.list);
      // reactive([]) 修改原数组2
      list.splice(0, list.length, ...res.list);
      total.value = res.total;
      loading.value = false;
    }
  } catch (error) {
    alert(error);
  }
}

watch(characterid, async (newValue, oldValue) => {
  // console.log('characterid: ' + oldValue + ' -> ' + newValue);
  if (newValue) {
    searchButton.value = false;
  } else {
    searchButton.value = true;
  }
  editCharacterId(newValue, oldValue);
})

onMounted(() => {
  // setTimeout(() => {
  //   getSkills();
  // }, 1000);
})

// 在卸载组件之前，在beforeUnmount()钩子中调用xxxxx.cancel()，取消所有还在 pending 的函数调用。
onBeforeUnmount(() => {
  editCharacterId.cancel;
  searchSkills.cancel;
})

// dialog对话框
const dialogFormVisible = ref(false);
const formLabelWidth = '140px';

const form = ref({
  name: 'Edit',
  id: 0,
  characterid: 0,
  skillid: 0,
  skilllevel: 0,
  masterlevel: 0,
  region: ''
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

.el-button--text {
  margin-right: 15px;
}

.el-select {
  width: 300px;
}

.el-input {
  width: 300px;
}

.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>
