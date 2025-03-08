<script setup lang="ts">
import { computed, inject, ref } from 'vue'
import { columnInjectionKey, type TableColumnCtx } from '@/core/table'
import { isGt600 } from '@/core/state'

const columnInjecter = inject(columnInjectionKey)
const columns = columnInjecter?.columns ?? ref<TableColumnCtx[]>([])

const expandColumns = computed(() => {
  return columns.value.filter((cell) => cell.expand !== undefined && cell.expand !== false)
})

const isExpand = computed(() => {
  return !isGt600.value && expandColumns.value.length > 0
})

const ExpandCellValue = (props2: { row: any; $index: number; cl: TableColumnCtx }) => {
  const { cl, row, $index } = props2
  return cl.renderCell({ row, $index })
}
</script>

<template>
  <el-table-column type="expand" v-if="isExpand">
    <template #default="scope">
      <el-descriptions :column="1" border>
        <el-descriptions-item v-for="ec in expandColumns" :label="ec.label">
          <ExpandCellValue v-bind="scope" :cl="ec" />
        </el-descriptions-item>
      </el-descriptions>
    </template>
  </el-table-column>
</template>
