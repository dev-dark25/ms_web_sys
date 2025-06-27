<template>
  <div class="default_div">
    <div id="bar" class="echarts"></div>
    <div id="realtimeSortBar" class="echarts"></div>
    <div id="lineBar" class="echarts"></div>
    <div id="pieBar" class="echarts"></div>
  </div>
</template>

<script lang='ts' setup>
import { onMounted, inject, onBeforeUnmount } from 'vue';
// import echarts1 from 'echarts'; // 局部直接获取

// const option: echarts.EChartsOption = {
//   title: {
//     text: 'ECharts 入门示例'
//   },
//   tooltip: {},
//   xAxis: {
//     data: ['衬衫', '羊毛衫', '雪纺衫', '裤子', '高跟鞋', '袜子']
//   },
//   yAxis: {},
//   series: [
//     {
//       name: '销量',
//       type: 'bar',
//       color: '#e69d87',
//       data: [5, 20, 36, 10, 10, 20]
//     },
//     {
//       type: 'bar',
//       data: [26, 24, 18, 22, 23, 20]
//     }
//   ]
// }


let barChart: any;

onMounted(() => {
  initBar();
  initRealtimeSortBar();
  initLine();
  initPie();
})

onBeforeUnmount(() => {
  if (realtimeSortBarInterval) {
    clearInterval(realtimeSortBarInterval);
  }
  if (pieInterval) {
    clearInterval(pieInterval);
  }
})

const echarts: any = inject('echarts'); // 从父级组件中获取(APP.vue中)
const initBar = () => {
  barChart = echarts.init(document.getElementById('bar'));
  // myChart = echarts1.init(document.getElementById('echarts') || new HTMLElement());

  // 根据页面大小自动响应图标大小
  window.addEventListener('resize', function () {
    barChart.resize();
  })

  // option && myChart.setOption(option);

  // 异步数据的加载
  barChart.setOption({
    title: {
      text: '树状图 异步加载数据',
      left: 'center'
    },
    tooltip: {},
    xAxis: {
      data: []
    },
    yAxis: {},
    series: [
      {
        name: '销量',
        type: 'bar',
        data: [],
        // stack: 'x'  // 堆叠柱状图
      },
      {
        name: 'test',
        type: 'bar',
        data: [],
        // stack: 'x'  // 堆叠柱状图
      },
      {
        name: 'test1',
        type: 'line',
        data: [],
        // stack: 'x'  // 堆叠柱状图
      },
    ]
  });
  // 添加鼠标点击事件 https://echarts.apache.org/handbook/zh/concepts/event
  barChart.on('click', function (params: any) {
    window.open('https://www.baidu.com/s?wd=' + encodeURIComponent(params.name));
  });
  fillBarData();
}

const fillBarData = () => {
  // 获取数据
  const barData = {
    categories: ["衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子"],
    values: [5, 20, 36, 10, 10, 20],
    values1: [15, 24, 16, 26, 20, 10],
  }
  // 填入数据
  barChart.setOption({
    xAxis: {
      data: barData.categories
    },
    series: [
      {
        // 根据名字对应到相应的系列
        name: '销量',
        data: barData.values
      },
      {
        // 根据名字对应到相应的系列
        name: 'test',
        data: barData.values1
      },
      {
        // 根据名字对应到相应的系列
        name: 'test1',
        data: barData.values1
      }
    ]
  });
}

// 动态排序柱状图
let realtimeSortBarChart: any;
let realtimeSortOption: any;
let realtimeSortBarInterval: any;
const initRealtimeSortBar = () => {
  let realtimeSortData: number[] = [];
  for (let i = 0; i < 5; ++i) {
    realtimeSortData.push(Math.round(Math.random() * 200));
  }

  realtimeSortOption = {
    title: {
      text: '动态排序柱状图',
      left: 'center'
    },
    xAxis: {
      max: 'dataMax'
    },
    yAxis: {
      type: 'category',
      data: ['A', 'B', 'C', 'D', 'E'],
      inverse: true,
      animationDuration: 300,
      animationDurationUpdate: 300,
      max: 4 // only the largest 5 bars will be displayed
    },
    series: [
      {
        realtimeSort: true,
        name: 'X',
        type: 'bar',
        data: realtimeSortData,
        label: {
          show: true,
          position: 'right',
          valueAnimation: true
        }
      }
    ],
    legend: {
      show: true
    },
    animationDuration: 3000,
    animationDurationUpdate: 3000,
    animationEasing: 'linear',
    animationEasingUpdate: 'linear'
  };
  realtimeSortBarChart = echarts.init(document.getElementById('realtimeSortBar'));
  realtimeSortBarInterval = setInterval(() => {
    updateRealtimeSortData();
    console.log('realtimeSortBarInterval');
  }, 3000);
}

const updateRealtimeSortData = () => {
  let data = realtimeSortOption.series[0].data;
  for (let i = 0; i < data.length; ++i) {
    if (Math.random() > 0.9) {
      data[i] += Math.round(Math.random() * 1000);
    } else {
      data[i] += Math.round(Math.random() * 100);
    }
  }
  realtimeSortBarChart.setOption(realtimeSortOption);
}

// 折线图
let lineChart: any;
let lineOption: any;
const initLine = () => {
  lineOption = {
    title: {
      text: '折线图',
      left: 'center'
    },
    xAxis: {
      data: ['A', 'B', 'C', 'D', 'E']
    },
    yAxis: {},
    series: [
      {
        data: [10, 22, 28, 28, 19],  // 在 ECharts 中，使用字符串 '-' 表示空数据，这对其他系列的数据也是适用的
        type: 'line',
        stack: 'x', // 堆叠折线图
        areaStyle: {}, // 区域填充色
        smooth: true, // 平滑曲线图
        // step: 'start',  // 阶梯线图，'start'、'middle' 和 'end'，分别表示在当前点，当前点与下个点的中间点，下个点拐弯
        lineStyle: {
          // 'normal' hierarchy in lineStyle has been removed since 4.0. All style properties are configured in lineStyle directly now.
          // normal: {
            color: 'red',
            width: 3,
            // type: 'dashed'
          // }
        },
        label: {
          show: true,
          position: 'bottom',
          // textStyle: {
          //   fontSize: 20
          // }
        }
      },
      {
        data: [5, 4, 3, 5, 10],
        type: 'line',
        stack: 'x', // 堆叠折线图
        areaStyle: {
          color: '#ff0',
          opacity: 0.5
        },  // 区域填充色
        smooth: true, // 平滑曲线图
        // step: 'middle',  // 阶梯线图，'start'、'middle' 和 'end'，分别表示在当前点，当前点与下个点的中间点，下个点拐弯
      }
    ]
  }
  lineChart = echarts.init(document.getElementById('lineBar'));
  lineChart.setOption(lineOption);
}

// 饼图
let pieChart: any;
let pieOption: any;
let pieInterval: any;
const initPie = () => {
  pieOption = {
    title: {
      text: '饼图',
      left: 'center',
      top: 'center'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: ['直接访问', '邮件营销', '联盟广告', '视频广告', '搜索引擎']
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c} ({d}%)'
    },
    series: [
      {
        name: '访问来源',
        type: 'pie',
        // stillShowZeroSum: false,
        // label: {
        //   show: false
        // },
        data: [
          { value: 335, name: '直接访问' },
          { value: 310, name: '邮件营销' },
          { value: 234, name: '联盟广告' },
          { value: 135, name: '视频广告' },
          { value: 1548, name: '搜索引擎' }
        ],
        // radius: '80%', // 饼图的半径
        radius: ['40%', '70%'], // 圆环图
      },
    ],
  }
  pieChart = echarts.init(document.getElementById('pieBar'));
  pieChart.setOption(pieOption);
  pieInterval = setInterval(() => {
    chartDispatchAction();
    console.log('pieInterval');
  }, 1000);
}


// 通过 dispatchAction 去轮流高亮饼图的每个扇形
let currentIndex = -1;
const chartDispatchAction = () => {
  const dataLen = pieOption.series[0].data.length;
  // 取消之前高亮的图形
  pieChart.dispatchAction({
    type: 'downplay',
    seriesIndex: 0,
    dataIndex: currentIndex
  });
  currentIndex = (currentIndex + 1) % dataLen;
  // 高亮当前图形
  pieChart.dispatchAction({
    type: 'highlight',
    seriesIndex: 0,
    dataIndex: currentIndex
  });
  // 显示 tooltip
  pieChart.dispatchAction({
    type: 'showTip',
    seriesIndex: 0,
    dataIndex: currentIndex
  });
}

</script>

<style>
.echarts {
  display: inline-block;
  width: 50%;
  height: 20rem;
}
</style>
