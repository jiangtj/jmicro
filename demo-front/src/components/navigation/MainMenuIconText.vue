<script setup lang="ts">
import type { Component, SVGAttributes } from 'vue'
const props = defineProps<{ icon?: Component<SVGAttributes>; text: string }>()
</script>

<template>
  <el-icon v-if="props.icon"><component :is="props.icon"></component></el-icon>
  <span>{{ props.text }}</span>
</template>
