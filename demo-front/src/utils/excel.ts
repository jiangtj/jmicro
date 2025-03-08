import type { TableColumn } from '@/core/table'
import ExcelJS from 'exceljs'
import { saveAs } from 'file-saver'
import { pick, at, cloneDeep } from 'lodash'
import type { VNode } from 'vue'

// export const flatList = (list, ...keys) => {
//   return list.map((item) => {
//     keys.forEach((k1) => {
//       const o = item[k1]
//       Object.keys(o).forEach((k2) => {
//         item[`${k1}.${k2}`] = o[k2]
//       })
//     })
//     return item
//   })
// }

export interface SheetColumn {
  header: string
  key: string
  width?: number
  render?: (cell: any, row: any, column: any, index: number) => VNode | string
}

export interface SheetColumnChangeConfig {
  ignore?: string[]
  filterTableColumn?: (c: TableColumn) => boolean
}

export const toSheetColumn = (column: TableColumn[], config?: SheetColumnChangeConfig): SheetColumn[] => {
  return column
    .filter((c) => {
      if (!config?.filterTableColumn) return true
      return config.filterTableColumn(c)
    })
    .map((c, i) => {
      const { label, prop, width, render } = c
      // let w: number | undefined
      // if (typeof width === 'string') {
      //   if (width.endsWith('px')) {
      //     w = parseInt(width.substring(0, width.length - 2))
      //   } else {
      //     w = parseInt(width)
      //   }
      // } else {
      //   w = width
      // }
      return {
        header: label ?? `header${i}`,
        key: prop ?? `key${i}`,
        render
      }
    })
    .filter((c) => !config?.ignore?.includes(c.key))
}

export const exportExcel = async (data: any[], name: string, columns: SheetColumn[]) => {
  const workbook = new ExcelJS.Workbook()
  workbook.creator = 'Me'
  workbook.created = new Date()
  const sheet = workbook.addWorksheet('My Sheet')
  sheet.columns = columns

  const render = new Map<string, (cell: any, row: any, column: any, index: number) => VNode | string>()
  columns.forEach((c) => {
    if (c.render) {
      render.set(c.key, c.render)
    }
  })

  data.forEach((row, i) => {
    const r: any = {}
    columns.forEach((c) => {
      const cell = at(row, c.key)[0]
      r[c.key] = cell

      const renderF = render.get(c.key)
      if (renderF) {
        const result = renderF(cell, row, undefined, i)
        if (typeof result === 'string') {
          r[c.key] = result
        }
      }
    })

    sheet.addRow(r)
  })

  const buffer = await workbook.xlsx.writeBuffer()
  const fileType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  const fileExtension = '.xlsx'
  const blob = new Blob([buffer], { type: fileType })
  saveAs(blob, name + fileExtension)
}
