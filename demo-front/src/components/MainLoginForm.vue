<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { loginHandler } from '@/core/login-form'

const formInline = reactive({
  username: '',
  password: ''
})

const onSubmit = () => {
  loginHandler.value(formInline)
}
</script>

<template>
  <el-card>
    <template #header>
      <div class="clearfix">
        <div class="title">管理系统</div>
      </div>
    </template>
    <el-form :model="formInline" label-width="80px">
      <el-form-item label="用户名">
        <el-input v-model="formInline.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="formInline.password" type="password" placeholder="请输入密码" />
      </el-form-item>
      <el-form-item style="margin-left: 70px">
        <el-button type="primary" @click="onSubmit">登录</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
