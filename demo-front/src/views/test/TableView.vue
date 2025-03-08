<script setup lang="ts">
import { formatter } from 'element-plus'
import { reactive } from 'vue'

const formInline = reactive({
  user: '',
  region: '',
  date: ''
})

const onSubmit = () => {
  console.log('submit!')
}

const options = [
  { label: 'Zone one 2', value: 'shanghai' },
  { label: 'Zone two 3', value: 'beijing' }
]

const tableData = [
  {
    date: '2016-05-03',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles'
  },
  {
    date: '2016-05-02',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles'
  },
  {
    date: '2016-05-04',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles'
  },
  {
    date: '2016-05-01',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles'
  }
]

const columns = [
  { prop: 'date', label: 'Date', width: '180' },
  { prop: 'name', label: 'Name', width: '180' },
  {
    prop: 'address',
    label: 'Address',
    minWidth: '300',
    render: (cell: any, row: any, column: any, index: number) =>
      `cell: ${cell}, index: ${index}, column: ${JSON.stringify(column)}, name in this row: ${row.name}`,
    renderHeader: (column: any) => `${column.prop}: ${column.label}`
  }
]

const pageAndSizeChange = (a: number, b: number) => {
  console.log(a)
  console.log(b)
}
</script>

<template>
  <MainQueryForm>
    <MainQueryInput label="Approved by" v-model="formInline.user" />
    <MainQuerySelect label="Activity zone" v-model="formInline.region" :options="options" />
  </MainQueryForm>
  <el-divider />
  <MainTable :data="tableData" :columns="columns" />
  <MainTablePagination :total="110" @change="pageAndSizeChange" />
  <el-divider />
  <MainTable :data="tableData">
    <MainTableColumn prop="date" label="Date" width="180" />
    <MainTableColumn prop="name" label="Name" width="180" />
    <MainTableColumn
      prop="address"
      label="Address"
      min-width="300"
      :render="
        (cell: any, row: any, column: any, index: number) =>
          `cell: ${cell}, index: ${index}, column: ${JSON.stringify(column)}, name in this row: ${row.name}`
      "
    />
  </MainTable>
  <el-divider />
  <MainTable :data="tableData">
    <el-table-column prop="date" label="Date" width="180" />
    <el-table-column prop="name" label="Name" width="180" />
    <el-table-column
      prop="address"
      label="Address"
      min-width="300"
      :formatter="
        (row: any, column: any, cell: any, index: number) =>
          `cell: ${cell}, index: ${index}, column: ${JSON.stringify(column)}, name in this row: ${row.name}`
      "
    />
  </MainTable>
</template>
