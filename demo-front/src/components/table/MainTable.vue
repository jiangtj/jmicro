<script setup lang="ts">
import { provide, ref, useTemplateRef, type VNode } from 'vue'
import type { TableInstance } from 'element-plus'
import {
  type MainTableInstance,
  type TableColumn,
  type Sort,
  columnInjectionKey,
  type TableColumnCtx
} from '@/core/table'

const props = defineProps<{
  data: any[]
  defaultSort?: Sort
}>()

const emit = defineEmits<{
  sortChange: [sort: Sort]
}>()

const slots = defineSlots<{
  default?(): any
}>()

const inst = useTemplateRef<TableInstance>('main-table')

const columns = ref<TableColumnCtx[]>([])
const registerColumn = (c: TableColumnCtx) => {
  columns.value.push(c)
}
const getColumns = (): TableColumnCtx[] => {
  return columns.value
}
provide(columnInjectionKey, {
  columns,
  getColumns,
  registerColumn
})

defineExpose<MainTableInstance>({ inst, getColumns })
</script>

<template>
  <el-table
    class="main-table"
    v-bind="$attrs"
    ref="main-table"
    stripe
    border
    style="width: 100%"
    :data="data"
    @sort-change="(s: any) => emit('sortChange', s)"
    :default-sort="props.defaultSort as any"
  >
    <slot />
  </el-table>
</template>

<style lang="scss"></style>
