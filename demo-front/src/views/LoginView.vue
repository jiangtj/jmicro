<script setup lang="ts">
import { onMounted } from 'vue'

async function login() {
  const r = await fetch(
    `http://localhost:17001/toLogin?redirectUrl=${encodeURIComponent('http://localhost:5173/callback')}`,
    {
      method: 'GET'
    }
  )
  window.location.href = await r.text()
}

onMounted(() => {
  login()
})
</script>

<template>
  <div class="main-login-page">
    <button @click="login">Casdoor 登录</button>
  </div>
  <MainSettingPanelBtn />
</template>

<style>
.main-login-page {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
.main-form {
  width: 400px;
  opacity: 0.9;
}

.main-form .el-card__header {
  padding: 0;
}

.main-form .title-banner {
  width: 400px;
}

.main-form .title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  padding: 10px 0;
}
</style>
