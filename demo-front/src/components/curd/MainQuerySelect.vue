<script setup lang="ts">
import type { SelectOptions } from '@/core/form'

const props = withDefaults(
  defineProps<{
    label: string
    clearable?: boolean
    placeholder?: string
    options?: SelectOptions
  }>(),
  {
    clearable: true,
    options: () => []
  }
)

const p = props.placeholder ?? '请选择' + props.label
</script>

<template>
  <MainFormItem :label="props.label">
    <MainSelect v-bind="$attrs" :clearable="clearable" :placeholder="p" :options="options" />
  </MainFormItem>
</template>
