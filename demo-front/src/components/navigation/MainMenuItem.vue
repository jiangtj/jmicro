<script setup lang="ts">
import type { MenuItem } from '@/core/menu'

const props = defineProps<MenuItem>()
</script>

<template>
  <el-menu-item :index="props.index" :route="props.route" v-if="props.auth ? props.auth() : true">
    <!-- <el-icon v-if="props.icon"><component :is="props.icon"></component></el-icon>
    <template #title>{{ props.name }}</template> -->
    <MainMenuIconText :icon="props.icon" :text="props.name" />
  </el-menu-item>
</template>
