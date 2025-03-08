import { defineComponent, ref, type DefineComponent, type Ref } from 'vue'
import { request, type Page, type PageParams, type SortParams } from './http-client'
import { useSort, type Sort, type TableColumn } from './table'
import MainTableColumn from '@/components/table/MainTableColumn.vue'
import MainTable from '@/components/table/MainTable.vue'
import MainTablePagination from '@/components/table/MainTablePagination.vue'
import { makeDestructurable, useManualRefHistory } from '@vueuse/core'

type DeepKeys<T> = T extends object
  ? {
      [K in keyof T]: T[K] extends object ? `${K & string}.${DeepKeys<T[K]> & string}` : K & string
    }[keyof T]
  : never
type RequiredDeepKeys<T> = DeepKeys<Required<T>>

type TC<T> = TableColumn & { prop?: RequiredDeepKeys<T> }
type TCDefaultSlot<T> = { row: T; cell: any; $index: number }

type DefineTableCloumnType<T> = DefineComponent<TC<T>> & {
  new (): { $slots: { default: (_: TCDefaultSlot<T>) => any; header: () => any } }
}

type CreateTableComponentRType<T> = [DefineComponent, DefineTableCloumnType<T>] & {
  table: DefineComponent
  column: DefineTableCloumnType<T>
}

export function useMainTable<T>(tableData = ref<T[]>([])) {
  const total = ref(0)
  const isPage = ref(false)
  const cachedPageChange = ref<(page: number, size: number) => void>()
  const cachedSortChange = ref<(s: Sort) => void>()
  const cachedDefaultSort = ref<Sort>()

  function createPage(fn: (page: number, size: number) => void) {
    isPage.value = true
    cachedPageChange.value = fn
  }

  function createPageRequest<P extends PageParams & SortParams>(url: string, params: Ref<P>) {
    params.value.page = 1
    const qParams = useManualRefHistory(params, { clone: true })

    const queryTableData = async () => {
      qParams.commit()
      const result = await request<Page<T>>('get', url, params.value)
      tableData.value = result.content
      total.value = result.totalElements
      return tableData
    }

    const pageChange = async (page: number, size: number) => {
      params.value.page = page
      params.value.size = size
      await queryTableData()
    }

    createPage(pageChange)

    const createSort = (defaultSort?: Sort) => {
      const { selectSort, sortChange } = useSort({ params, callback: queryTableData, defaultSort })
      cachedDefaultSort.value = defaultSort
      cachedSortChange.value = sortChange
      return { selectSort, sortChange }
    }

    return { tableData, total, queryTableData, pageChange, qParams, createSort }
  }

  const createTableComponent = () => {
    const DefineTable = defineComponent((_props, { slots }) => {
      return () => (
        <>
          <MainTable
            data={tableData.value}
            onSortChange={cachedSortChange.value}
            defaultSort={cachedDefaultSort.value}
          >
            {() => slots.default?.()}
          </MainTable>
          {isPage.value ? (
            <MainTablePagination total={total.value} onChange={cachedPageChange.value} />
          ) : (
            ''
          )}
        </>
      )
    })

    const DefineTableCloumn = defineComponent((props: TC<T>, { slots }) => {
      return () => (
        <MainTableColumn {...props}>
          {{
            default: ({ row, cell, $index }: TCDefaultSlot<T>) =>
              slots.default?.({ row, cell, $index }),
            header: () => slots.header?.()
          }}
        </MainTableColumn>
      )
    })

    return makeDestructurable({ table: DefineTable, column: DefineTableCloumn }, [
      DefineTable,
      DefineTableCloumn
    ]) as CreateTableComponentRType<T>
  }

  return { createTableComponent, createPageRequest, tableData, total, createPage }
}
