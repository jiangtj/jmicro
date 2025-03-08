<script setup lang="ts">
import { btnPlain } from '@/core/state'
</script>

<template>
  <el-dropdown :button-props="{ plain: btnPlain } as any">
    <slot></slot>
    <template #dropdown>
      <slot name="dropdown"></slot>
    </template>
  </el-dropdown>
</template>
