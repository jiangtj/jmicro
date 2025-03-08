<script setup lang="ts">
import { menu } from '@/core/menu'
import MainAside from '@/components/MainAside.vue'
import MainHeader from '@/components/MainHeader.vue'
import MainContainer from '@/components/MainContainer.vue'
</script>

<template>
  <el-container class="main-layout-container">
    <el-aside class="main-layout-aside" width="auto">
      <MainAside :menus="menu" />
    </el-aside>

    <el-container>
      <el-header class="main-layout-header" height="auto">
        <MainHeader />
      </el-header>

      <el-main class="main-layout-main">
        <MainContainer />
      </el-main>
    </el-container>
  </el-container>
</template>

<style>
.main-layout-container {
  height: 100vh;
}
.main-layout-aside {
  position: relative;
  color: var(--el-text-color-primary);
  background-color: var(--main-aside-bg-color);
}
.main-layout-aside .el-menu {
  border-right: none;
}
.main-layout-header {
  position: relative;
  padding: 0;
  background-color: var(--el-bg-color-page);
  color: var(--el-text-color-primary);
}
.main-layout-main {
  padding: 0;
  background-color: var(--el-bg-color);
}
</style>
