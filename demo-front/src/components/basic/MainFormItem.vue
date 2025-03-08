<script setup lang="ts">
import { useValidator } from '@/core/form'
import type { FormItem } from '@/core/form'

const props = defineProps<FormItem>()
const { rules } = useValidator(props)
</script>

<template>
  <el-form-item :label="props.label" :rules="rules">
    <template #label v-if="props.tip">
      <div class="main-form-item-tip-label">
        <span>{{ props.label }}</span>
        <el-tooltip effect="dark" :content="props.tip" placement="top">
          <el-icon><i-ep-info-filled /></el-icon>
        </el-tooltip>
      </div>
    </template>
    <template #default>
      <slot />
    </template>
  </el-form-item>
</template>

<style lang="scss">
.main-form-item-tip-label {
  display: flex;
  align-items: center;
}
</style>
