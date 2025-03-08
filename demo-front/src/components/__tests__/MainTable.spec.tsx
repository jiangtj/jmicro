import { describe, it, expect } from 'vitest'

import { mount } from '@vue/test-utils'
import MainTable from '../table/MainTable.vue'
import { doubleWait, getTestData } from './el/table-test-common'
import MainTableColumn from '../table/MainTableColumn.vue'
import { useMainTable } from '@/core/table-c'
import { ref } from 'vue'
import type { MainTableInstance } from '@/core/table'

describe('MainTable', () => {
  describe('base', () => {
    const data = getTestData()
    const wrapper = mount(() => (
      <MainTable data={data}>
        <MainTableColumn prop="id" />
        <MainTableColumn prop="name" label="片名" />
        <MainTableColumn prop="release" label="发行日期" />
        <MainTableColumn prop="director" label="导演" />
        <MainTableColumn prop="runtime" label="时长（分）" />
      </MainTable>
    ))
    it('head', async () => {
      await doubleWait()
      const ths = wrapper.findAll('thead th')
      expect(ths.map((node) => node.text()).filter((o) => o)).toEqual(['片名', '发行日期', '导演', '时长（分）'])
    })
    it('row length', () => {
      expect(wrapper.findAll('.el-table__body-wrapper tbody tr').length).toEqual(getTestData().length)
    })
    it('row data', () => {
      const cells = wrapper.findAll('td .cell').map((node) => node.text())
      const testDataArr = getTestData().flatMap((cur) => {
        return Object.values(cur).map(String)
      })
      expect(cells).toEqual(testDataArr)
      wrapper.unmount()
    })
  })

  describe('render', () => {
    const data = getTestData()
    const wrapper = mount(() => (
      <MainTable data={data}>
        <MainTableColumn prop="id" />
        <MainTableColumn prop="name" label="片名" />
        <MainTableColumn prop="release" label="发行日期" render={(cell: any, row: any, column: any, $index: number) => `${cell}:${row.release}:${$index}`} />
        <MainTableColumn prop="director" label="导演" renderHeader={() => '导演2'} />
        <MainTableColumn prop="runtime" label="时长（分）" />
      </MainTable>
    ))
    it('head', async () => {
      await doubleWait()
      const ths = wrapper.findAll('thead th')
      expect(ths.map((node) => node.text()).filter((o) => o)).toEqual(['片名', '发行日期', '导演2', '时长（分）'])
    })
    it('row length', () => {
      expect(wrapper.findAll('.el-table__body-wrapper tbody tr').length).toEqual(getTestData().length)
    })
    it('row data', () => {
      const cells = wrapper.findAll('td .cell').map((node) => node.text())
      const testDataArr = getTestData().flatMap((cur, index) => {
        cur.release = `${cur.release}:${cur.release}:${index}`
        return Object.values(cur).map(String)
      })
      expect(cells).toEqual(testDataArr)
      wrapper.unmount()
    })
  })

  describe('slot', () => {
    const data = getTestData()
    const wrapper = mount(() => (
      <MainTable data={data}>
        <MainTableColumn prop="id" />
        <MainTableColumn prop="name" label="片名"></MainTableColumn>
        <MainTableColumn prop="release" label="发行日期">
          {({ row, cell, $index }: any) => <div>{`${row.release}:${cell}:${$index}`}</div>}
        </MainTableColumn>
        <MainTableColumn prop="director" label="导演">
          {{
            header: () => <div>导演2</div>
          }}
        </MainTableColumn>
        <MainTableColumn prop="runtime" label="时长（分）">
          {{
            default: () => undefined,
            header: () => <div>时长</div>
          }}
        </MainTableColumn>
      </MainTable>
    ))
    it('head', async () => {
      await doubleWait()
      const ths = wrapper.findAll('thead th')
      expect(ths.map((node) => node.text()).filter((o) => o)).toEqual(['片名', '发行日期', '导演2', '时长'])
    })
    it('row length', () => {
      expect(wrapper.findAll('.el-table__body-wrapper tbody tr').length).toEqual(getTestData().length)
    })
    it('row data', () => {
      const cells = wrapper.findAll('td .cell').map((node) => node.text())
      const testDataArr = getTestData().flatMap((cur, index) => {
        cur.release = `${cur.release}:${cur.release}:${index}`
        return Object.values(cur).map(String)
      })
      expect(cells).toEqual(testDataArr)
      wrapper.unmount()
    })
  })

  describe('useMainTable', () => {
    // const data = getTestData()
    // const { tableData, createTableComponent } = useMainTable<any>()
    // tableData.value = data
    const data = ref(getTestData())
    const { createTableComponent } = useMainTable(data)
    const [DefineTable, DefineTableCloumn] = createTableComponent()
    const wrapper = mount(() => (
      <DefineTable>
        <DefineTableCloumn prop="id" />
        <DefineTableCloumn prop="name" label="片名"></DefineTableCloumn>
        <DefineTableCloumn prop="release" label="发行日期">
          {({ row, cell, $index }: any) => <div>{`${row.release}:${cell}:${$index}`}</div>}
        </DefineTableCloumn>
        <DefineTableCloumn prop="director" label="导演">
          {{
            header: () => <div>导演2</div>
          }}
        </DefineTableCloumn>
        <DefineTableCloumn prop="runtime" label="时长（分）">
          {{
            default: () => undefined,
            header: () => <div>时长</div>
          }}
        </DefineTableCloumn>
      </DefineTable>
    ))
    it('head', async () => {
      await doubleWait()
      const ths = wrapper.findAll('thead th')
      expect(ths.map((node) => node.text()).filter((o) => o)).toEqual(['片名', '发行日期', '导演2', '时长'])
    })
    it('row length', () => {
      expect(wrapper.findAll('.el-table__body-wrapper tbody tr').length).toEqual(getTestData().length)
    })
    it('row data', () => {
      const cells = wrapper.findAll('td .cell').map((node) => node.text())
      const testDataArr = getTestData().flatMap((cur, index) => {
        cur.release = `${cur.release}:${cur.release}:${index}`
        return Object.values(cur).map(String)
      })
      expect(cells).toEqual(testDataArr)
      wrapper.unmount()
    })
  })

  describe('cloumns ctx', () => {
    const createTable = function () {
      return mount({
        components: {
          MainTable,
          MainTableColumn
        },
        template: `
          <MainTable :data="testData" ref="table" >
            <MainTableColumn prop="id" />
            <MainTableColumn prop="name" label="片名" />
            <MainTableColumn prop="release" label="发行日期" :not-export="false"/>
            <MainTableColumn prop="director" label="导演" expand />
            <MainTableColumn prop="runtime" label="时长（分）" not-export/>
          </MainTable>
        `,
        data() {
          return {
            testData: getTestData()
          }
        }
      })
    }
    it('expose', async () => {
      const wrapper = createTable()
      await doubleWait()
      const table = wrapper.vm.$refs.table as MainTableInstance

      expect(table.getColumns().map((item) => item.prop)).toEqual(['id', 'name', 'release', 'director', 'runtime'])
      expect(table.getColumns().map((item) => item.notExport)).toEqual([false, false, false, false, true])
      expect(table.getColumns().map((item) => item.expand)).toEqual([false, false, false, true, false])
      wrapper.unmount()
    })
    it('nested', async () => {
      const wrapper = mount({
        components: {
          MainTable,
          MainTableColumn
        },
        template: `
          <MainTable :data="testData" ref="table">
            <MainTableColumn prop="id" />
            <MainTableColumn prop="name" label="片名">
              <MainTable :data="testData" ref="table2">
                <MainTableColumn prop="id" />
                <MainTableColumn prop="name2" label="片名"/>
                <MainTableColumn prop="release2" label="发行日期"/>
                <MainTableColumn prop="director2" label="导演" />
                <MainTableColumn prop="runtime2" label="时长（分）"/>
              </MainTable>
            </MainTableColumn>
            <MainTableColumn prop="release" label="发行日期"/>
            <MainTableColumn prop="director" label="导演" />
            <MainTableColumn prop="runtime" label="时长（分）"/>
          </MainTable>
        `,
        data() {
          return {
            testData: getTestData()
          }
        }
      })
      await doubleWait()
      const table = wrapper.vm.$refs.table as MainTableInstance
      expect(table.getColumns().map((item) => item.prop)).toEqual(['id', 'name', 'release', 'director', 'runtime'])
      const table2 = wrapper.vm.$refs.table2 as MainTableInstance
      expect(table2.getColumns().map((item) => item.prop)).toEqual(['id', 'name2', 'release2', 'director2', 'runtime2'])
      wrapper.unmount()
    })
  })
})
