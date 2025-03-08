<script setup lang="ts">
import { ref, watch, computed, type Ref } from 'vue'
import MainInput from './MainInput.vue'
import { createRefWithDefaultValue } from '@/core/form'

const props = defineProps<{
  min?: number
  max?: number
  fixed?: number
  formatter?: {
    get: (v: number | string) => number
    set: (v: number | string) => number | string
  }
  defaultValue?: number
  placeholder?: string
}>()

const emit = defineEmits<{
  innerChange: [number | string | undefined]
}>()

const model = defineModel<number>()
const modelValue = createRefWithDefaultValue(model, props.defaultValue, {
  isUndefined: (v) => v === undefined
})
const innerModel: Ref<number | string | undefined> = ref()

const format = (v: number) => {
  let result: string | number = v
  if (props.fixed !== undefined) {
    result = result.toFixed(props.fixed)
  }
  if (props.formatter) {
    result = props.formatter.set(result)
  }
  return result
}

const valueFormat = (result?: number | string) => {
  if (result === undefined || result === '' || isNaN(Number(result))) {
    return undefined
  }
  if (props.formatter) {
    return props.formatter.get(result)
  }
  return Number(result)
}

const submit = () => {
  modelValue.value = valueFormat(innerModel.value)
}

watch(
  modelValue,
  () => {
    if (modelValue.value === undefined) {
      innerModel.value = undefined
      return
    }
    if (props.min !== undefined) {
      if (props.min > modelValue.value) {
        modelValue.value = props.min
      }
    }
    if (props.max !== undefined) {
      if (props.max < modelValue.value) {
        modelValue.value = props.max
      }
    }
    innerModel.value = format(modelValue.value)
  },
  {
    immediate: true
  }
)

const submitInnerChange = (v: number | string | undefined) => {
  emit('innerChange', valueFormat(v))
}

const placeholder = computed(() =>
  props.placeholder
    ? props.placeholder
    : String(props.defaultValue !== undefined ? format(props.defaultValue) : '')
)
</script>

<template>
  <MainInput
    v-model="innerModel"
    @blur="submit"
    @inner-change="submitInnerChange"
    :placeholder="placeholder"
  >
    <template #prepend v-if="$slots.prepend"><slot name="prepend" /></template>
    <template #append v-if="$slots.append"><slot name="append" /></template>
  </MainInput>
</template>
