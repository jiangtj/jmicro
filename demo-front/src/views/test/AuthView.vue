<script setup lang="ts">
import { login, User } from '@/core/auth'
import { menu } from '@/core/menu'
import avaterUrl from '@/assets/avater.jpg'

const loadUser = (u: User) => {
  login(u)
  console.log(u)
  console.log(menu)
}
</script>

<template>
  <el-row>
    <el-button
      type="primary"
      @click="loadUser(new User({ nick: 'Super Admin', subject: 1, avater: avaterUrl }))"
    >
      Load Super Admin
    </el-button>
    <el-button type="primary" @click="loadUser(new User(2, ['test'], ['analysis', 'workbench']))">
      Load all 权限
    </el-button>
    <el-button type="primary" @click="loadUser(new User(3, ['test'], ['analysis']))">
      Load analysis 权限
    </el-button>
    <el-button type="primary" @click="loadUser(new User(4, ['test'], ['workbench']))">
      Load workbench 权限
    </el-button>
    <el-button type="primary" @click="loadUser(new User(5, ['test']))">Load none 权限</el-button>
  </el-row>
</template>
