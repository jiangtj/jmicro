<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { logout as logoutAction, useAuth } from '@/core/auth'
import UserFilled from '~icons/ep/user-filled'
import { config, toggleCollapse, collapseButton, toggleSettingPanel } from '@/core/state'
import { breadcrumbMenu } from '@/core/breadcrumb'
import MainBreadcrumbItem from '@/components/navigation/MainBreadcrumbItem.vue'
import { indexToMenu } from '@/core/menu'

const router = useRouter()

const { user } = useAuth()

const logout = () => {
  logoutAction()
  router.push('/login')
}
const isShowCollapse = computed(() => config.value.foldingButton === 'header')
const isShowTabs = computed(() => config.value.isShowTabs && config.value.header !== 'tabs')

const convertTabName = (index: string) => {
  const m = indexToMenu.value.get(index)
  return m?.name
}
</script>

<template>
  <div>
    <el-row class="main-header">
      <div class="main-header-menu">
        <div v-if="isShowCollapse" class="main-header-menu-collapse-btn" @click="toggleCollapse()">
          <el-icon><component :is="collapseButton"></component></el-icon>
        </div>
        <div
          :class="{
            'main-header-menu-item-padding-left': !isShowCollapse,
            'main-header-menu-item': true
          }"
        >
          <el-breadcrumb v-if="config.header === 'breadcrumb' && breadcrumbMenu">
            <MainBreadcrumbItem
              v-for="item in breadcrumbMenu.parent"
              :key="item.index"
              :item="item"
            />
            <MainBreadcrumbItem :item="breadcrumbMenu" disable-router />
          </el-breadcrumb>
          <el-breadcrumb v-if="config.header === 'title' && breadcrumbMenu">
            <MainBreadcrumbItem :item="breadcrumbMenu" disable-router />
          </el-breadcrumb>
          <MainTabs v-if="config.header === 'tabs'" :convert-tab="convertTabName" />
        </div>
      </div>
      <div class="main-header-toolbar">
        <el-dropdown v-if="user">
          <div class="main-header-toolbar-item">
            <el-avatar
              v-if="!user.avater"
              style="margin-right: 10px"
              :icon="UserFilled"
              :size="30"
            />
            <el-avatar v-else style="margin-right: 10px" :src="user.avater" :size="30" />
            <span>{{ user.nick }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="logout()">
                <el-icon><i-ep-switch-button /></el-icon>
                退出系统
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <div class="main-header-toolbar-item" @click="toggleSettingPanel()">
          <el-icon><i-ep-setting /></el-icon>
        </div>
      </div>
    </el-row>
    <el-row class="main-header-tabs" v-if="isShowTabs">
      <MainTabs :convert-tab="convertTabName" />
    </el-row>
  </div>
</template>

<style>
.main-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 45px;
  font-size: 1.1rem;
  flex-wrap: nowrap;
}
.main-header-menu {
  height: 100%;
  max-width: calc(100% - 240px);
  display: flex;
  justify-content: start;
  align-items: center;
}
.main-header-menu-collapse-btn {
  height: 100%;
  cursor: pointer;
  padding: 0 10px;
  display: flex;
  align-items: center;
  color: var(--el-text-color-primary);
  outline: none;
}
.main-header-menu-collapse-btn:hover {
  background-color: var(--main-header-btn-hover-bg-color);
}
.main-header-menu-item {
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
}
.main-header-menu-item-padding-left {
  padding-left: 10px;
}
.main-header-toolbar {
  display: inline-flex;
  justify-content: end;
  align-items: center;
  height: 45px;
  width: 240px;
}
.main-header-toolbar .el-dropdown {
  height: 100%;
}
.main-header-toolbar-item {
  height: 100%;
  cursor: pointer;
  padding: 0 10px;
  display: flex;
  align-items: center;
  color: var(--el-text-color-primary);
  outline: none;
}
.main-header-toolbar-item:hover {
  background-color: var(--main-header-btn-hover-bg-color);
}
</style>
