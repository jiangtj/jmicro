<script setup lang="ts">
import { ref, reactive } from 'vue'
import { request, type Pageable, type Page } from '@/core/http-client'
import { nonNull, ValidateError, type FormInstance } from '@/core/form'
import { formFormatter as feeFormatter, columnRender as feeRender } from '@/utils/fee'

export interface Book {
  id: number
  name: string
  category: number
  price: number
  publish: string
}

const selectModel = reactive<Pageable<Book>>({})
selectModel.page = 1

const categories: Record<string, string> = {
  1: '文学',
  2: '流行',
  3: '文化',
  4: '生活',
  5: '经管',
  6: '科技'
}
// const categories = new Map([
//   [1, '文学'],
//   [2, '流行'],
//   [3, '文化'],
//   [4, '生活'],
//   [5, '经管'],
//   [6, '科技']
// ])

const tableData = ref<Book[]>([])
const total = ref(0)

const queryTableData = async () => {
  const result = await request<Page<Book>>('get', '/books', selectModel)
  tableData.value = result.content
  total.value = result.totalElements
}

queryTableData()

const pageChange = async (page: number, size: number) => {
  selectModel.page = page
  selectModel.size = size
  queryTableData()
}

const form = ref<Partial<Book>>({})
const formTitle = ref('')
const formSubmit = ref(() => {})
const formDialogVisible = ref(false)
const showCreate = () => {
  form.value = {}
  formTitle.value = '创建'
  formSubmit.value = submitCreate
  formDialogVisible.value = true
}
const showUpdate = (r: Book) => {
  form.value = Object.assign({}, r)
  formTitle.value = '修改'
  formSubmit.value = submitUpdate
  formDialogVisible.value = true
}
const submitCreate = async () => {
  await request('post', '/books', form.value)
  formDialogVisible.value = false
  ElMessage.success('操作成功!')
  queryTableData()
}
const submitUpdate = async () => {
  await request('put', '/books', form.value)
  formDialogVisible.value = false
  ElMessage.success('操作成功!')
  queryTableData()
}
const submitDelete = async (id: number) => {
  await request('delete', '/books', { id })
  ElMessage.success('delete!')
  queryTableData()
}
const submitCancel = () => {
  ElMessage.info('cancel!')
}
</script>

<template>
  <MainQueryForm>
    <MainQueryInput label="书名" v-model="selectModel.name" />
    <MainQuerySelect label="类别" v-model="selectModel.category" :options="categories" number />
    <template #action>
      <MainQueryBtnCreate @click="showCreate()" />
      <MainQueryBtnRead @click="queryTableData()" />
    </template>
  </MainQueryForm>

  <MainTable :data="tableData">
    <MainTableColumn prop="name" label="书名" min-width="180" />
    <MainTableColumn
      prop="category"
      label="类别"
      min-width="180"
      :render="(cell: number) => categories[cell]"
    />
    <MainTableColumn prop="price" label="价格" min-width="180" :render="feeRender" />
    <MainTableColumn prop="publish" label="发布日期" min-width="180" />
    <MainTableColumn label="操作" min-width="180">
      <template #default="{ row }">
        <MainTableDropdown text="查看" type="primary">
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>Action 1</el-dropdown-item>
              <el-dropdown-item>Action 2</el-dropdown-item>
              <el-dropdown-item>Action 3</el-dropdown-item>
              <el-dropdown-item disabled>Action 4</el-dropdown-item>
              <el-dropdown-item divided>Action 5</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </MainTableDropdown>
        <MainTableBtnEdit @click="showUpdate(row)" />
        <MainTableBtnDel box pop @click="submitDelete(row.id)" @cancel="submitCancel()" />
      </template>
    </MainTableColumn>
  </MainTable>
  <MainTablePagination :total="total" @change="pageChange" />

  <MainFormDialog
    :title="formTitle"
    v-model="formDialogVisible"
    :model="form"
    :width="600"
    :form-props="{ labelWidth: '120px' }"
  >
    <MainFormItem label="书名" prop="name" :rules="nonNull()">
      <MainInput v-model="form.name" />
    </MainFormItem>
    <MainFormItem label="类别" prop="category" :rules="nonNull()">
      <MainSelect v-model="form.category" :options="categories" number />
    </MainFormItem>
    <MainFormItem label="价格" prop="price" :rules="nonNull()">
      <MainInputNumber v-model="form.price" :formatter="feeFormatter" />
    </MainFormItem>
    <MainFormItem label="发布日期" prop="publish" :rules="nonNull()">
      <el-date-picker
        v-model="form.publish"
        type="date"
        placeholder="Pick a Date"
        value-format="YYYY-MM-DD"
      />
    </MainFormItem>
    <template #footer="{ validate }">
      <el-button @click="formDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="validate(formSubmit)">确认</el-button>
    </template>
  </MainFormDialog>
</template>
