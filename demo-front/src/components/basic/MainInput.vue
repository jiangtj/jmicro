<script setup lang="ts">
import { createRefWithDefaultValue } from '@/core/form'
import { computed, watch } from 'vue'

const props = defineProps<{ defaultValue?: number | string; placeholder?: string }>()

const emit = defineEmits<{
  innerChange: [number | string | undefined]
}>()

const model = defineModel<number | string>()

const dv = computed(() => props.defaultValue)
const innerModel = createRefWithDefaultValue(model, dv, {
  isUndefined: (v) => v === undefined || v === ''
})

const placeholder = computed(() =>
  props.placeholder ? props.placeholder : String(props.defaultValue ?? '')
)

watch(
  innerModel,
  (v) => {
    emit('innerChange', v)
  },
  { immediate: true }
)
</script>

<template>
  <el-input v-model="innerModel" :placeholder="placeholder">
    <template #prepend v-if="$slots.prepend"><slot name="prepend" /></template>
    <template #append v-if="$slots.append"><slot name="append" /></template>
  </el-input>
</template>
