<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    formItemWidth?: string
  }>(),
  {
    formItemWidth: '220px'
  }
)
</script>

<template>
  <el-form
    :inline="true"
    class="main-query-form"
    :style="{ '--main-query-input-width': props.formItemWidth }"
  >
    <slot></slot>
    <el-form-item>
      <slot name="action"> </slot>
    </el-form-item>
  </el-form>
</template>

<style>
.main-query-form .el-input {
  --el-input-width: var(--main-query-input-width);
}

.main-query-form .el-select {
  --el-select-width: var(--main-query-input-width);
}
</style>
