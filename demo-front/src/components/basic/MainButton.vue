<script setup lang="ts">
import { btnPlain } from '@/core/state'
</script>

<template>
  <el-button :plain="btnPlain"><slot /></el-button>
</template>
