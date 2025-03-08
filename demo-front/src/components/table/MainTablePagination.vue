<script setup lang="tsx">
import { watch, computed } from 'vue'
import { pageSizes, pageBackground, pagePosition } from '@/core/table'

const props = defineProps<{
  total: number
}>()
const emit = defineEmits<{
  (e: 'change', page: number, size: number): void
}>()
const page = defineModel('page', { default: 1, type: Number })
const size = defineModel('size', { default: pageSizes.value[0], type: Number })

watch(page, () => {
  emit('change', page.value, size.value)
})
watch(size, () => {
  emit('change', page.value, size.value)
})

const layout = computed(() => {
  if (pageSizes.value.length > 1) {
    return 'total, sizes, prev, pager, next, jumper'
  }
  return 'total, prev, pager, next, jumper'
})
</script>

<template>
  <el-row class="main-table-pagination" :justify="pagePosition">
    <el-pagination
      v-bind="$attrs"
      v-model:current-page="page"
      v-model:page-size="size"
      :page-sizes="pageSizes"
      small
      :background="pageBackground"
      :layout="layout"
      :total="props.total"
    />
  </el-row>
</template>

<style lang="scss">
.main-table-pagination {
  margin-top: 5px;
}
</style>
