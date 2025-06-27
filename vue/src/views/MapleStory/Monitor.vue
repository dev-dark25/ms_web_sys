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

    实时作弊程序监控-单次最高伤害，时间段累计伤害
  </div>
</template>

<script lang='ts' setup>
import { ref, reactive, onMounted, computed, inject, onBeforeUnmount } from 'vue'
import { getMSRealtimeLoggedCount } from '@/axios/api'

const echarts: any = inject('echarts'); // 从父级组件中获取(APP.vue中)

onMounted(() => {
  initRealtimeSortBar();
})

onBeforeUnmount(() => {
  if (realtimeSortBarInterval1) {
    clearInterval(realtimeSortBarInterval1);
  }
})

let loggedCount = ref();

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
  data: ['频道5', '频道4', '频道3', '频道2', '频道1'],
  inverse: true,
  animationDuration: 300,
  animationDurationUpdate: 300,
  max: 4 // only the largest 5 bars will be displayed
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
        }
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
  }, 3000);
}

const updateRealtimeSortData = async () => {
  let data1 = realtimeSortOption1.series[0].data;
  let data2 = realtimeSortOption2.series[0].data;
  const res = await getMSRealtimeLoggedCount();
  if (res.code !== '200') {
    alert('获取数据异常');
  }
  data1.splice(0, data1.length, ...res.list1);
  data2.splice(0, data2.length, ...res.list2);
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
</style>
