<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  title?: string
}>()

const model = defineModel<boolean>()

const fullscreen = ref(false)

const toggleFullscreen = () => {
  fullscreen.value = !fullscreen.value
}

const close = () => {
  model.value = false
}
</script>

<template>
  <el-dialog v-model="model" :fullscreen="fullscreen" :show-close="false">
    <template #header>
      <slot name="header">
        <el-row justify="space-between">
          <div>
            <span class="el-dialog__title">{{ props.title }}</span>
          </div>
          <div>
            <el-space :size="0">
              <el-button text @click="toggleFullscreen">
                <el-icon><i-ep-full-screen /></el-icon>
              </el-button>
              <el-button text @click="close">
                <el-icon><i-ep-close /></el-icon>
              </el-button>
            </el-space>
          </div>
        </el-row>
      </slot>
    </template>
    <slot></slot>
    <template #footer><slot name="footer"></slot></template>
  </el-dialog>
</template>
