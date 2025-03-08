<script setup lang="ts">
import { computed, ref } from 'vue'
import { createRefWithDefaultValue, type SelectOption, type SelectOptions } from '@/core/form'
import type { Arrayable } from '@/core/types'
import { request } from '@/core/http-client'
import { useOptions } from '@/core/form-select'

interface SelectFormItem {
  options?: SelectOptions
  number?: boolean
  defaultValue?: Arrayable<number | string>
  multiple?: boolean
  api?: string
}

const props = defineProps<SelectFormItem>()
const model = defineModel<Arrayable<number | string>>()

const innerModel = createRefWithDefaultValue(model, props.defaultValue, {
  undefinedValue: props.multiple ? [] : undefined,
  isUndefined: (v) => v === undefined || v === '' || (Array.isArray(v) && v.length === 0)
})

const remoteApi = computed(() => props.api)
const remoteOptions = ref<SelectOption[]>([])
const loading = ref(false)

const remoteMethod = async (query: string) => {
  if (query && remoteApi.value) {
    loading.value = true
    remoteOptions.value = await request<SelectOption[]>('get', remoteApi.value, { query })
    loading.value = false
  } else {
    remoteOptions.value = []
  }
}

const innerOptions = useOptions(() => {
  if (remoteApi.value) {
    return remoteOptions.value
  }
  return props.options || []
}, props.number)
</script>

<template>
  <el-select
    v-model="innerModel"
    :multiple="props.multiple"
    :remote="remoteApi !== undefined"
    :remote-method="remoteMethod"
    :loading="loading"
  >
    <slot>
      <el-option
        v-for="item in innerOptions"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      />
    </slot>
  </el-select>
</template>
