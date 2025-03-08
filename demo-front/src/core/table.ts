import { computed, ref, toValue } from 'vue'
import type { MaybeRefOrGetter, Ref, VNode } from 'vue'
import { useLocalSelect, useLocalSwitch } from './utils'
import { type SortParams } from './http-client'
import type { InjectionKey } from 'vue'

export interface MainTableInstance {
  inst: any
  getColumns: () => TableColumn[]
}

export interface TableColumn {
  prop?: string
  label?: string
  width?: string | number
  minWidth?: string | number
  fixed?: 'left' | 'right' | boolean
  renderHeader?: (column: TableColumn) => string
  render?: (cell: any, row: any, column: any, index: number) => string
  notExport?: boolean
  expand?: boolean
  tip?: string
}

export interface TableColumnCtx extends TableColumn {
  renderCellToString: (scope: { row: any; $index: number }) => string
  renderCell: (scope: { row: any; $index: number }) => VNode
}

export const columnInjectionKey = Symbol('table-c-columns') as InjectionKey<{
  getColumns: () => TableColumnCtx[]
  columns: Ref<TableColumnCtx[]>
  registerColumn: (column: TableColumnCtx) => void
}>

export const defaultSize = ref(10)
export const pageSizes = ref([10])

export const definePageSizes = (...sizes: number[]) => {
  pageSizes.value = sizes
  defaultSize.value = sizes[0]
}

export const defineDefaultPageSize = (size: number) => {
  defaultSize.value = size
}

export enum PagePositionLabel {
  start = '行头',
  center = '中间',
  end = '行尾'
}
export const pagePosition = useLocalSelect('page-position', PagePositionLabel, 'end')
export const pageBackground = useLocalSwitch('page-background', false)

export type Sort = { column?: any; prop?: string; order?: any }
export const idDesc: Sort = { prop: 'id', order: 'descending' }
export const useSort = ({
  params,
  callback,
  defaultSort
}: {
  params: MaybeRefOrGetter<SortParams>
  callback?: Function
  defaultSort?: Sort
}) => {
  const innerP = toValue(params)
  const sort = ref<Sort>()
  const selectSort = computed(() => {
    if (sort.value?.prop && sort.value?.order) {
      return `${sort.value.prop},${sort.value.order === 'ascending' ? 'ASC' : 'DESC'}`
    }
    return undefined
  })

  const changeParams = (s: Sort) => {
    sort.value = s
    innerP.sortQ = selectSort.value
  }

  const sortChange = (s: Sort) => {
    changeParams(s)
    if (callback) {
      callback()
    }
  }

  if (defaultSort) {
    changeParams(defaultSort)
  }

  return { selectSort, sortChange }
}
