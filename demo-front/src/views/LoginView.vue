<script setup lang="ts">
import {useCasdoor} from 'casdoor-vue-sdk';
import {useLogto} from '@logto/vue';
import {casdoorSdk} from "@/utils/casdoor.ts";

const { getSigninUrl, getSignupUrl } = useCasdoor();
const { signIn, signOut, isAuthenticated } = useLogto();

async function login() {
  // window.location.href = getSigninUrl()
  // signIn('http://localhost:3000/callback')
  casdoorSdk.signin_redirect()
}

// onMounted(() => {
//   login()
// })
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
