<template>
  <div class="default_div">
    <h2>Monitor</h2>
    <h4>当前在线人数：{{ loggedCount }}</h4>
    <!-- <h2 style="display: inline;float: right;">当天最高在线人数：{{  }}</h2> -->
    <!-- 实时分区分频在线人数 -->
    <!-- <div>一区实时在线人数</div> -->
    <div id="realtimeSortBar1" class="echarts"></div>
    <!-- <div>二区实时在线人数</div> -->
    <div id="realtimeSortBar2" class="echarts"></div>
    <h2>封禁账户信息</h2>
    <el-table v-loading="loading" element-loading-text="Loading..." :data="filterTableData" height="300"
      style="width: 100%">
      <el-table-column fixed prop="id" label="id" />
      <el-table-column prop="name" label="name" />
      <!-- <el-table-column prop="banned" label="banned" /> -->
      <el-table-column label="banned">
        <template #default="scope">
          <el-tag :type="scope.row.banned ? 'danger' : 'success'">{{ isBannedFormat(scope.row.banned)
          }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="banreason" label="banreason" width="100" />
      <el-table-column fixed="right" label="Operations" width="180">
        <template #header>
          <el-input v-model="search" size="small" placeholder="Type to search name" />
        </template>
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.$index, scope.row)">Edit</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 30, 40]"
      :background="true" layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
      @current-change="handleCurrentChange" class="el_pa" />

    <h2>警告账户信息</h2>
    <el-table v-loading="loading1" element-loading-text="Loading..." :data="warnList" height="300" style="width: 100%">
      <el-table-column fixed prop="accountId" label="accountId" />
      <el-table-column fixed prop="characterId" label="characterId" />
      <el-table-column fixed prop="characterName" label="characterName" />
      <el-table-column prop="banType" label="banType" />
      <el-table-column prop="count" label="count" />
      <el-table-column prop="lastTime" label="lastTime" />
      <el-table-column fixed="right" label="Operations" width="180">
        <template #header>
          <el-input v-model="search" size="small" placeholder="Type to search name" />
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="currentPage1" v-model:page-size="pageSize1" :page-sizes="[10, 20, 30, 40]"
      :background="true" layout="total, sizes, prev, pager, next, jumper" :total="total1" @size-change="handleSizeChange1"
      @current-change="handleCurrentChange1" class="el_pa" />

    <el-dialog v-model="dialogFormVisible" title="update account" width="500">
      <el-form :model="form">
        <el-form-item label="id" :label-width="formLabelWidth">
          <el-input v-model="form.id" disabled />
        </el-form-item>
        <el-form-item label="name" :label-width="formLabelWidth">
          <el-input v-model="form.name" disabled />
        </el-form-item>
        <el-form-item label="banned" :label-width="formLabelWidth">
          <el-select v-model="form.banned" placeholder="Select">
            <el-option label="是" :value=true />
            <el-option label="否" :value=false />
          </el-select>
        </el-form-item>
        <el-form-item label="banreason" :label-width="formLabelWidth">
          <el-input v-model="form.banreason" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <!-- <el-button @click="dialogFormVisible = false">Cancel</el-button> -->
          <el-button type="primary" plain @click="updataAccount()">
            Confirm
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang='ts' setup>
import { ref, reactive, onMounted, computed, inject, onBeforeUnmount } from 'vue'
import { getMSRealtimeLoggedCount, getMSAccounts, updateMSAccount, getMSWarnList } from '@/axios/api'

const loading = ref(true);
const loading1 = ref(true);
const search = ref('');
let list: any[] = reactive([]);
const filterTableData = computed(() =>
  list.filter(
    (data) =>
      !search.value ||
      data.name.toLowerCase().includes(search.value.toLowerCase())
  )
)
let warnList: any[] = reactive([]);

const dialogFormVisible = ref(false)
const formLabelWidth = '120px'
const form = reactive({
  id: null,
  name: '',
  banned: '',
  banreason: ''
})

const isBannedFormat = (param: any) => {
  return param ? '是' : '否'
}

const handleEdit = (index: number, row: any) => {
  console.log(index, row)
  dialogFormVisible.value = true
  form.id = row.id
  form.name = row.name
  form.banned = row.banned
  form.banreason = row.banreason
}

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const handleSizeChange = (val: number) => {
  console.log(`${val} items per page`)
  loading.value = true;
  pageSize.value = val;
  getAccounts();
}

const handleCurrentChange = (val: number) => {
  console.log(`current page: ${val}`)
  loading.value = true;
  currentPage.value = val;
  getAccounts();
}


const currentPage1 = ref(1)
const pageSize1 = ref(10)
const total1 = ref(0)

const handleSizeChange1 = (val: number) => {
  console.log(`${val} items per page`)
  loading1.value = true;
  pageSize1.value = val;
  getAccounts();
}

const handleCurrentChange1 = (val: number) => {
  console.log(`current page: ${val}`)
  loading1.value = true;
  currentPage1.value = val;
  getAccounts();
}

const getAccounts = async () => {
  const req = {
    banned: 1
  }
  try {
    const res = await getMSAccounts(req);
    if (res.code === '200') {
      list.splice(0, list.length, ...res.list);
      loading.value = false;
    }
  } catch (error) {
    alert(error);
  }
}

const updataAccount = async () => {
  console.log('updataAccount')
  try {
    const res = await updateMSAccount(form);
    if (res.code === '200') {
      dialogFormVisible.value = false
      ElMessage({
        message: 'update account success.',
        type: 'success',
      })
      getAccounts()
    }
  } catch (error) {
    alert(error);
  }
}

const getWarnList = async () => {
  try {
    const res = await getMSWarnList();
    if (res.code === '200') {
      warnList.splice(0, warnList.length, ...res.list);
      loading1.value = false;
    }
  } catch (error) {
    alert(error);
  }
}

onMounted(() => {
  initRealtimeSortBar();
  setTimeout(() => {
    getAccounts();
    getWarnList();
  }, 1000);
})

onBeforeUnmount(() => {
  if (realtimeSortBarInterval1) {
    clearInterval(realtimeSortBarInterval1);
  }
})

let loggedCount = ref();

const echarts: any = inject('echarts'); // 从父级组件中获取(APP.vue中)

// 动态排序柱状图
let realtimeSortBarChart1: any;
let realtimeSortOption1: any;
let realtimeSortBarInterval1: any;
let realtimeSortBarChart2: any;
let realtimeSortOption2: any;

const title1 = {
  text: '一区实时在线人数',
  left: 'center'
}
const title2 = {
  text: '二区实时在线人数',
  left: 'center'
}
const yAxis = {
  max: 'dataMax'
}
const xAxis = {
  type: 'category',
  data: ['频道3', '频道2', '频道1'],
  inverse: true,
  animationDuration: 300,
  animationDurationUpdate: 300,
  max: 2 // only the largest 3 bars will be displayed
}
const initRealtimeSortBar = () => {
  let realtimeSortData1: number[] = [];
  realtimeSortOption1 = {
    title: title1,
    yAxis: yAxis,
    xAxis: xAxis,
    series: [
      {
        realtimeSort: false,
        // name: 'X',
        type: 'bar',
        data: realtimeSortData1,
        label: {
          show: true,
          position: 'top',//bottom,left,right
          valueAnimation: true
        },
        itemStyle: {
          barBorderRadius: 5,
          borderWidth: 2,
          borderType: 'solid',
          borderColor: '#73c0de',
          shadowColor: '#5470c6',
          shadowBlur: 3
        },
        barWidth: '20%'
      }
    ],
    legend: {
      show: true
    },
    animationDuration: 1500,
    animationDurationUpdate: 3000,
    animationEasing: 'linear',
    animationEasingUpdate: 'linear'
  };
  realtimeSortBarChart1 = echarts.init(document.getElementById('realtimeSortBar1'));

  let realtimeSortData2: number[] = [];
  realtimeSortOption2 = {
    title: title2,
    yAxis: yAxis,
    xAxis: xAxis,
    series: [
      {
        realtimeSort: false,
        // name: 'X',
        type: 'bar',
        data: realtimeSortData2,
        label: {
          show: true,
          position: 'top',//bottom,left,right
          valueAnimation: true
        }
      }
    ],
    legend: {
      show: true
    },
    animationDuration: 1500,
    animationDurationUpdate: 5000,
    animationEasing: 'linear',
    animationEasingUpdate: 'linear'
  };
  realtimeSortBarChart2 = echarts.init(document.getElementById('realtimeSortBar2'));

  updateRealtimeSortData();
  realtimeSortBarInterval1 = setInterval(() => {
    updateRealtimeSortData();
    console.log('realtimeSortBarInterval');
  }, 600000);
}

const updateRealtimeSortData = async () => {
  let data1 = realtimeSortOption1.series[0].data;
  let data2 = realtimeSortOption2.series[0].data;
  const res = await getMSRealtimeLoggedCount();
  if (res.code !== '200') {
    alert('获取数据异常');
  }
  data1.splice(0, data1.length, ...[...res.list0].reverse()); //倒序
  data2.splice(0, data2.length, ...res.list0);
  realtimeSortBarChart1.setOption(realtimeSortOption1);
  realtimeSortBarChart2.setOption(realtimeSortOption2);
  loggedCount.value = res.loggedCount;
}

</script>

<style lang="less" scoped>
.echarts {
  display: inline-block;
  width: 50%;
  height: 20rem;
}

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
