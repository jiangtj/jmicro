<script setup lang="ts">
import { config } from '@/core/state'
</script>

<template>
  <el-scrollbar>
    <div class="main-container">
      <router-view v-if="config.isKeepAlive" v-slot="{ Component }">
        <keep-alive>
          <Suspense>
            <component :is="Component" />
          </Suspense>
        </keep-alive>
      </router-view>
      <router-view v-else v-slot="{ Component }">
        <Suspense>
          <component :is="Component" />
        </Suspense>
      </router-view>
    </div>
  </el-scrollbar>
</template>

<style>
.main-container {
  padding: 10px 20px;
}
</style>
