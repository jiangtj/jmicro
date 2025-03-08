<script setup lang="ts">
import { RouterView } from 'vue-router'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import MainSettingPanel from '@/components/MainSettingPanel.vue'
import { config } from '@/core/state'
</script>

<template>
  <div :class="['theme-light-' + config.themeLight, 'theme-dark-' + config.themeDark]">
    <el-config-provider :locale="zhCn">
      <RouterView />
      <MainSettingPanel />
    </el-config-provider>
  </div>
</template>
