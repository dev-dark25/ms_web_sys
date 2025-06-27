<template>
  <div class="default_div">
    <el-space direction="vertical" :fill=true style="width:100%;">
      <h2>服务启停</h2>
      <div>
        <el-button type="primary" @click="start()">Start</el-button>
        <el-button type="danger" @click="stop()">Stop</el-button>
        <el-button type="success" @click="restart()">Restart</el-button>
      </div>
      <div>
        <codemirror v-model="code" placeholder="Code goes here..." :style="{ height: '500px', color: 'black' }"
          :disabled="true" :autofocus="false" :indent-with-tab="false" :tab-size="2" @ready="handleReady"
          @change="log('change', $event)" @focus="log('focus', $event)" @blur="log('blur', $event)" />
      </div>
    </el-space>
  </div>
</template>

<script lang='ts' setup>
import { ref, onMounted, onBeforeUnmount, shallowRef } from 'vue'
import { Codemirror } from 'vue-codemirror'
import { startMS, stopMS, restartMS } from '@/axios/api'

let ws: WebSocket | null = null;
onMounted(() => {
  initWebSocket();
})

onBeforeUnmount(() => {
  ws?.close();
})

const initWebSocket = () => {
  ws = new WebSocket('ws://localhost:8080/heaven-ms/myWs/1');
  ws.onopen = () => {
    console.log('网络连接成功');
  }
  ws.onmessage = (e) => {
    console.log('接收到服务器发送的数据: ' + e.data);
    // const msg = JSON.parse(e.data);
    code.value += '\n' + e.data;
  }
  ws.onerror = (e) => {
    console.log('当websocket建立网络连接失败时触发', e);
  }
  ws.onclose = (e) => {
    console.log('当websocket被关闭时时触发', e);
  }
}

const sendMessage = (msg: any) => {
  ws?.send(msg);
}

const code = ref(`console.log('Hello, world!')`)  // TODO 刷新后，默认读取log日志  
const view = shallowRef()
const handleReady = (payload: any) => {
  view.value = payload.view
}

const log = (type: any, e: any) => {
  console.log(type);
}

const start = () => {
  startMS();
}

const stop = () => {
  stopMS();
}

const restart = async () => {
  restartMS();
}

</script>

<style lang="less" scoped></style>
