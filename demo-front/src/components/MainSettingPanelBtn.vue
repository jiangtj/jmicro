<script setup lang="ts">
import { reactive } from 'vue'
import { toggleSettingPanel } from '@/core/state'
</script>

<template>
  <div class="main-setting-panel-btn" @click="toggleSettingPanel()"><i-ep-setting /></div>
</template>

<style lang="scss">
.main-setting-panel-btn {
  position: absolute;
  right: 0;
  top: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--main-setting-panel-btn-color);
  background-color: var(--main-setting-panel-btn-bg-color);
  width: 40px;
  height: 30px;
  border-radius: 4px 0 0 4px;
  cursor: pointer;

  &:hover {
    background-color: var(--main-setting-panel-btn-hover-bg-color);
  }
}
</style>
