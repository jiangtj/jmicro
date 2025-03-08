<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { menuKey, type Menu } from '@/core/menu'
import { config, toggleCollapse, collapseButton } from '@/core/state'
import MainMenu from '@/components/navigation/MainMenu.vue'
import { useElementSize } from '@vueuse/core'

const route = useRoute()

const props = defineProps<{
  menus: Menu[]
}>()

const isShowCollapse = computed(() => config.value.foldingButton === 'bottom')

const plane = ref()
const { height } = useElementSize(plane)
</script>

<template>
  <el-scrollbar>
    <div ref="plane" class="main-aside-plane"><slot /></div>
    <el-menu
      :class="{
        'main-aside-menu': true,
        'main-aside-menu-padding-bottom': isShowCollapse
      }"
      :style="{
        'padding-top': height + 'px'
      }"
      :default-active="route.meta?.menuIndex"
      :collapse="config.foldingMenu"
      unique-opened
      router
    >
      <main-menu v-for="m in props.menus" :key="menuKey(m)" :menu="m" />
    </el-menu>
  </el-scrollbar>
  <div v-if="isShowCollapse" class="main-aside-collapse" @click="toggleCollapse()">
    <el-icon class="main-aside-collapse-icon"><component :is="collapseButton"></component></el-icon>
  </div>
</template>

<style>
.main-aside-menu:not(.el-menu--collapse) {
  width: 200px;
}
.main-aside-plane {
  position: absolute;
  top: 0;
  z-index: 1;
  background-color: var(--el-bg-color);
  width: 100%;
}
.main-aside-menu-padding-bottom {
  padding-bottom: 30px;
}
.main-aside-collapse {
  position: absolute;
  display: flex;
  bottom: 0;
  align-items: center;
  justify-content: center;
  height: 30px;
  line-height: 30px;
  color: var(--main-aside-collapse-color);
  --color: var(--main-aside-collapse-color);
  background-color: var(--main-aside-collapse-bg-color);
  padding: 0 5px;
  cursor: pointer;
  width: 100%;
}
.main-aside-collapse:hover {
  background-color: var(--main-aside-collapse-bg-hover-color);
}
</style>
