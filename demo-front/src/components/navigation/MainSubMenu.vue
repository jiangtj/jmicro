<script setup lang="ts">
import { menuKey } from '@/core/menu'
import type { SubMenu } from '@/core/menu'
import MainMenu from '@/components/navigation/MainMenu.vue'

const props = defineProps<SubMenu>()
</script>

<template>
  <el-sub-menu :index="props.index" v-if="props.auth ? props.auth() : true">
    <template #title>
      <MainMenuIconText :icon="props.icon" :text="props.name" />
    </template>
    <main-menu v-for="m in props.children" :key="menuKey(m)" :menu="m" />
  </el-sub-menu>
</template>
