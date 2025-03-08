<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    label: string
    clearable?: boolean
    placeholder?: string
  }>(),
  {
    clearable: true
  }
)

const p = props.placeholder ?? '请输入' + props.label
</script>

<template>
  <MainFormItem :label="props.label">
    <MainInput v-bind="$attrs" :placeholder="p" :clearable="props.clearable" />
  </MainFormItem>
</template>
