<template>
  <div class="div background_div">
    <Header></Header>
    <div class="main_div">
      <Aside></Aside>
      <div class="view_div">
        <RouterView v-slot="{ Component }">
          <Transition name="fade" mode="out-in">
            <component :is="Component" />
          </Transition>
        </RouterView>
        <!-- <RouterView v-slot="{ Component, route }">
          <Transition name="fade" mode="out-in">
            <component :is="Component" :key="route.path" />
          </Transition>
        </RouterView> -->
        <!-- <RouterView></RouterView> -->
      </div>
    </div>
    <!-- <Footer></Footer> -->
  </div>
</template>

<script lang='ts' setup>
import Header from './Header.vue'
import Aside from './Aside.vue'
// import Footer from './Footer.vue'
</script>

<style lang="less" scoped>
.background_div {
  // background-image: url(../src/assets/img/ly_xd.jpg);
  background-image: url('@/assets/img/ms_pkb.jpg');
  /* background-repeat: no-repeat; */
  background-position: center;
  background-size: 100% 100%;
}

.main_div {
  display: flex;
  height: calc(100% - 5rem);

  .view_div {
    width: 100%;
    overflow-x: hidden;
    overflow-y: auto;
  }
}

.fade-enter-to,
.fade-leave-from {
  opacity: 1;
  transform: none;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateX(1rem);
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.5s;
}

</style>
