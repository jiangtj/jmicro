<!--Copyright 2022 The Casdoor Authors. All Rights Reserved.-->

<!--Licensed under the Apache License, Version 2.0 (the "License");-->
<!--you may not use this file except in compliance with the License.-->
<!--You may obtain a copy of the License at-->

<!--     http://www.apache.org/licenses/LICENSE-2.0 -->

<!--Unless required by applicable law or agreed to in writing, software-->
<!--distributed under the License is distributed on an "AS IS" BASIS,-->
<!--WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.-->
<!--See the License for the specific language governing permissions and-->
<!--limitations under the License.-->

<template>
  <h1>Callback</h1>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { request } from '@/core/http-client'
import { token } from '@/core/token'
import { login, User, pushDefaultPage } from '@/core'

const route = useRoute()
async function loginAction() {
  const code = route.query.code
  const state = route.query.state
  const res = await request<any>('post', `/login?code=${code}&state=${state}`)
  console.log(token)
  token.value = res.data
  request<any>('get', '/user').then((res: any) => {
    login(new User(res.data))
    pushDefaultPage()
  })
}

onMounted(() => {
  loginAction()
})
</script>

<style scoped></style>
