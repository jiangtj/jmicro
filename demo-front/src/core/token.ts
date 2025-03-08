import { ref, type Ref } from 'vue'
import { useSessionStorage } from '@vueuse/core'
import { defineRequestInterceptor, login, logout, User, pushDefaultPage } from '@/core'
import { loginHandler } from '@/core/login-form'
import { request } from '@/core/http-client'
import { jwtDecode } from 'jwt-decode'
import type { UserInfo } from './auth'

export const token = useSessionStorage('casdoor-token', '')

const initToken = () => {
  if (token.value === '') {
    logout()
    return
  }
  const decoded = jwtDecode(token.value)
  console.log(decoded)
  if (!decoded.exp || decoded.exp * 1000 < new Date().getTime()) {
    token.value = ''
    logout()
    return
  }
  login(new User(decoded as UserInfo))
}

export default {
  install: (app: any) => {
    defineRequestInterceptor((config) => {
      if (token.value !== '') {
        config.headers.Authorization = `Bearer ${token.value}`
      }
      return config
    })
    initToken()
  }
}
