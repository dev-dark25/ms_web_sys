<template>
  <div class="default_div">
    <el-space direction="vertical" :fill=true style="width:100%;">
      <h2>Player</h2>
      <el-space wrap :size="size">
        <el-input v-model="input" style="width: 200px" placeholder="id/name" />
        <el-button :icon="Search" type="primary" circle @click="getPlayer()" />
      </el-space>
      <el-table v-loading="loading" element-loading-text="Loading..." :data="filterTableData" height="300"
        style="width: 100%">
        <el-table-column fixed prop="accountId" label="accountId" />
        <el-table-column prop="name" label="name" />
        <el-table-column prop="mapId" label="mapId" width="200" />
        <el-table-column prop="job" label="job" />
        <el-table-column prop="level" label="level" />
        <el-table-column prop="gmLevel" label="gmLevel" />
        <el-table-column prop="cash" label="cash" />
        <el-table-column prop="meso" label="meso" />
        <!-- <el-table-column label="isWork">
          <template #default="scope">
            <el-tag :type="scope.row.isWork ? 'success' : 'danger'">{{ isWorkFormat(scope.row.isWork)
            }}</el-tag>
          </template>
        </el-table-column> -->
        <el-table-column fixed="right" label="Operations" width="180">
          <template #header>
            <el-input v-model="search" size="small" placeholder="Type to search name" />
          </template>
          <template #default="scope">
            <!-- <el-button size="small" @click="handleOperate(scope.$index, scope.row)">operate</el-button> -->
            <el-select v-model="scope.row.operate" placeholder="Select" @change="handleOperate(scope.$index, scope.row)">
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </template>
        </el-table-column>
      </el-table>

      <!-- <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 30, 40]"
        :small="small" :disabled="disabled" :background="background" layout="total, sizes, prev, pager, next, jumper"
        :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" /> -->
    </el-space>

    <el-dialog v-model="dialogFormVisible" title="update player" width="500">
      <el-form :model="form">
        <el-form-item :label="operateFormat(form.operateValue, 0)" :label-width="formLabelWidth">
          <!-- <el-text class="mx-1" type="success">{{ operateFormat(form.operateValue, 0) }}</el-text> -->
        </el-form-item>
        <el-form-item label="accountId" :label-width="formLabelWidth">
          <el-input v-model="form.accountId" disabled />
        </el-form-item>
        <el-form-item label="name" :label-width="formLabelWidth">
          <el-input v-model="form.name" disabled />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 0" label="cash" :label-width="formLabelWidth">
          <el-input v-model="form.cash" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 1" label="exp" :label-width="formLabelWidth">
          <el-input v-model="form.exp" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 2" label="meso" :label-width="formLabelWidth">
          <el-input v-model="form.meso" type="number" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 3" label="gmLevel" :label-width="formLabelWidth">
          <el-select v-model="form.gmLevel" placeholder="Select">
            <el-option v-for="item in options1" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-show="form.operateValue === 4" label="item" :label-width="formLabelWidth">
          <el-input v-model="form.item" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 4" label="quantity" :label-width="formLabelWidth">
          <el-input v-model="form.quantity" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 5" label="equip" :label-width="formLabelWidth">
          <el-input v-model="form.equip" />
        </el-form-item>

        <el-form-item v-show="form.operateValue === 5" label="str" :label-width="formLabelWidth">
          <el-input v-model="form.str" :disabled="!form.equip" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 5" label="dex" :label-width="formLabelWidth">
          <el-input v-model="form.dex" :disabled="!form.equip" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 5" label="int" :label-width="formLabelWidth">
          <el-input v-model="form.int" :disabled="!form.equip" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 5" label="luk" :label-width="formLabelWidth">
          <el-input v-model="form.luk" :disabled="!form.equip" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 5" label="hp" :label-width="formLabelWidth">
          <el-input v-model="form.hp" :disabled="!form.equip" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 5" label="mp" :label-width="formLabelWidth">
          <el-input v-model="form.mp" :disabled="!form.equip" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 5" label="watk" :label-width="formLabelWidth">
          <el-input v-model="form.watk" :disabled="!form.equip" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 5" label="matk" :label-width="formLabelWidth">
          <el-input v-model="form.matk" :disabled="!form.equip" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 5" label="wdef" :label-width="formLabelWidth">
          <el-input v-model="form.wdef" :disabled="!form.equip" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 5" label="mdef" :label-width="formLabelWidth">
          <el-input v-model="form.mdef" :disabled="!form.equip" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 5" label="acc" :label-width="formLabelWidth">
          <el-input v-model="form.acc" :disabled="!form.equip" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 5" label="avoid" :label-width="formLabelWidth">
          <el-input v-model="form.avoid" :disabled="!form.equip" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 5" label="speed" :label-width="formLabelWidth">
          <el-input v-model="form.speed" :disabled="!form.equip" />
        </el-form-item>
        <el-form-item v-show="form.operateValue === 5" label="jump" :label-width="formLabelWidth">
          <el-input v-model="form.jump" :disabled="!form.equip" />
        </el-form-item>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <!-- <el-button @click="dialogFormVisible = false">Cancel</el-button> -->
          <el-button type="primary" plain @click="updataPlayer()">
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
          <el-button type="primary" plain @click="deletePlayer()">
            Confirm
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang='ts' setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getMSPlayer, updataMSPlayer } from '@/axios/api'
import { Search } from '@element-plus/icons-vue'

const size = ref(10)
const input = ref('')
const loading = ref(true);
const search = ref('');
const options = [
  {
    value: 0,
    label: 'send cash',
  },
  {
    value: 1,
    label: 'send exp',
  },
  {
    value: 2,
    label: 'send meso',
  },
  {
    value: 3,
    label: 'set gm level',
  },
  {
    value: 4,
    label: 'send item',
  },
  {
    value: 5,
    label: 'send equit',
  }
]
const options1 = [
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
const messages = [
  ['发送点券', '点券发送完成'],
  ['发送经验', '经验发送完成'],
  ['发送金币', '金币发送完成'],
  ['设置GM等级', 'GM等级设置完成'],
  ['发送物品', '物品发送完成'],
  ['发送装备', '装备发送完成']
]

let list: any[] = reactive([]);
const filterTableData = computed(() =>
  list.filter(
    (data) =>
      !search.value ||
      data.name.toLowerCase().includes(search.value.toLowerCase())
  )
)

const operateFormat = (param: any, param1: any) => {
  return messages[param][param1]
}

const dialogFormVisible = ref(false)
const dialogForm1Visible = ref(false)
const formLabelWidth = '80px'
const form = reactive({
  operateValue: null,
  accountId: null,
  parameter: null,
  name: null,
  cash: null,
  exp: null,
  meso: null,
  gmLevel: undefined,
  item: null,
  quantity: 0,
  equip: undefined,
  str: 0,
  dex: 0,
  int: 0,
  luk: 0,
  hp: 0,
  mp: 0,
  watk: 0,
  matk: 0,
  wdef: 0,
  mdef: 0,
  acc: 0,
  avoid: 0,
  speed: 0,
  jump: 0
})

const handleOperate = (index: number, row: any) => {
  console.log(index, row)
  form.operateValue = row.operate
  form.accountId = row.accountId
  form.parameter = row.accountId
  form.name = row.name
  // form.cash = row.cash
  // form.exp = row.exp
  // form.meso = row.meso
  // form.gmLevel = row.gmLevel
  // form.item = row.item
  // form.equip = row.equip
  dialogFormVisible.value = true
}
const handleEdit = (index: number, row: any) => {
  console.log(index, row)
  dialogFormVisible.value = true
  form.accountId = row.accountId
  form.name = row.name
  // form.cash = row.cash
  // form.exp = row.exp
  // form.meso = row.meso
  // form.gmLevel = row.gmLevel
  // form.item = row.item
  // form.equip = row.equip
}
const handleDelete = (index: number, row: any) => {
  console.log(index, row)
  dialogForm1Visible.value = true
}

// const currentPage = ref(1)
// const pageSize = ref(10)
const total = ref(0)
// const small = ref(false)
// const background = ref(true)
// const disabled = ref(false)

// const handleSizeChange = (val: number) => {
//   console.log(`${val} items per page`)
//   pageSize.value = val
//   getPlayer();
// }
// const handleCurrentChange = (val: number) => {
//   console.log(`current page: ${val}`)
//   currentPage.value = val
//   getPlayer();
// }

const getPlayer = async () => {
  console.log('getPlayer')
  const data = {
    parameter: input.value
  }
  try {
    const res = await getMSPlayer(data);
    if (res.code === '200') {
      list.splice(0, list.length, ...res.list);
      total.value = res.total;
      loading.value = false;
    } else {
      loading.value = false;
      ElMessage.error(res.message)
    }
  } catch (error) {
    alert(error);
  }
}

const updataPlayer = async () => {
  console.log('updataPlayer')
  try {
    const res = await updataMSPlayer(form);
    if (res.code === '200') {
      dialogFormVisible.value = false
      ElMessage({
        message: operateFormat(form.operateValue, 1),
        type: 'success',
      })
      getPlayer()
    }
  } catch (error) {
    alert(error);
  }
}

const deletePlayer = async () => {
  dialogForm1Visible.value = false
  ElMessage({
    message: 'delete player success.',
    type: 'success',
  })
  getPlayer()
}

onMounted(() => {
  setTimeout(() => {
    getPlayer();
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
