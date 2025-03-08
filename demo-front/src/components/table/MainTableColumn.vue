<script setup lang="ts">
import { ElTableColumn } from 'element-plus'
import { at } from 'lodash'
import { columnInjectionKey, type TableColumn } from '@/core/table'
import { inject, type Ref, renderSlot } from 'vue'
import { isGt600 } from '@/core/state'

const props = defineProps<TableColumn>()

const slots = defineSlots<{
  default?(props: { cell: any; row: any; $index: number }): any
  header?(): any
}>()

const cellValue = (row: any) => (props.prop ? at(row, props.prop)[0] : undefined)

const renderCellToString = (scope: { row: any; $index: number }) => {
  const cell = cellValue(scope.row)
  if (props.render) {
    return props.render(cell, scope.row, props, scope.$index)
  }
  if (typeof cell === 'string') {
    return cell
  }
  return JSON.stringify(cell)
}

const renderCell = (scope: { row: any; $index: number }) => {
  return renderSlot(
    slots,
    'default',
    { cell: cellValue(scope.row), row: scope.row, $index: scope.$index },
    () => [renderCellToString(scope)]
  )
}

const columnInjecter = inject(columnInjectionKey)
if (columnInjecter) {
  const c = Object.assign({ renderCellToString, renderCell }, props)
  columnInjecter.registerColumn(c)
}
</script>

<template>
  <ElTableColumn
    v-if="!props.expand || isGt600"
    :prop="props.prop"
    :label="props.label"
    :width="props.width"
    :min-width="props.minWidth"
    :fixed="props.fixed"
    align="center"
  >
    <template #default="scope">
      <div class="main-table-cell-wrap">
        <renderCell v-bind="scope" />
      </div>
    </template>
    <template #header>
      <slot name="header">
        {{ props.renderHeader ? props.renderHeader(props) : props.label }}
        <el-tooltip v-if="props.tip" effect="dark" :content="props.tip" placement="top" raw-content>
          <el-icon><i-ep-info-filled /></el-icon>
        </el-tooltip>
      </slot>
    </template>
  </ElTableColumn>
</template>

<style lang="scss">
.main-table-cell-wrap {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 5px;

  .el-button + .el-button {
    margin-left: 0;
  }
}
</style>
