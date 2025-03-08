<script setup lang="ts">
const props = defineProps<{
  type?: string
  text?: string
}>()
</script>

<template>
  <MainDropdown class="main-table-cell-item">
    <slot>
      <MainButton :type="props.type" size="small">
        {{ props.text ?? '操作' }}<el-icon class="el-icon--right"><i-ep-arrow-down /></el-icon>
      </MainButton>
    </slot>
    <template #dropdown>
      <slot name="dropdown"></slot>
    </template>
  </MainDropdown>
</template>
