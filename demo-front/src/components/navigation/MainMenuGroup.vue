<script setup lang="ts">
import type { MenuGroup } from '@/core/menu'
import MainMenuItem from '@/components/navigation/MainMenuItem.vue'

const props = defineProps<MenuGroup>()
</script>

<template>
  <el-menu-item-group v-if="props.auth ? props.auth() : true">
    <template #title>
      <el-icon v-if="props.icon"><component :is="props.icon"></component></el-icon>
      <span>{{ props.name }}</span>
    </template>
    <main-menu-item v-for="m in props.menu" v-bind="m" />
  </el-menu-item-group>
</template>
