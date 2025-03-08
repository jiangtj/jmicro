<script setup lang="ts">
import { isAlert, confirmTxt, cancelTxt, confirmType } from '@/core/confirm-box'

const props = withDefaults(
  defineProps<{
    title?: string
    width?: number | string
    type?: 'success' | 'info' | 'warning' | 'error'
  }>(),
  {
    title: '是否确认进行该操作?',
    width: 150
  }
)

const emit = defineEmits<{
  click: []
  cancel: []
}>()

const click = () => {
  if (confirmType.value === 'box' && isAlert.value) {
    ElMessageBox.confirm(props.title, '提示', {
      confirmButtonText: confirmTxt.value,
      cancelButtonText: cancelTxt.value,
      type: props.type,
      customClass: 'main-confirm-box'
    })
      .then(() => {
        emit('click')
      })
      .catch(() => {
        emit('cancel')
      })
    return
  }
  emit('click')
}
</script>

<template>
  <el-popconfirm
    v-if="confirmType === 'pop' && isAlert"
    :title="props.title"
    :width="props.width"
    :confirm-button-text="confirmTxt"
    :cancel-button-text="cancelTxt"
    @confirm="click"
    @cancel="emit('cancel')"
  >
    <template #reference>
      <slot></slot>
    </template>
  </el-popconfirm>
  <div class="main-confirm-content" v-else @click="click">
    <slot></slot>
  </div>
</template>

<style lang="scss">
.main-confirm-content {
  line-height: 1rem;
}
</style>
