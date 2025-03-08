import { ref } from 'vue'
import type { Ref } from 'vue'
import { login, User } from '@/core/auth'
import { pushDefaultPage } from '@/core'
import avaterUrl from '@/assets/avater.jpg'

export const loginHandler = ref((form: { username: string; password: string }) => {
  login(new User({ nick: 'Super Admin', subject: 1, avater: avaterUrl }))
  pushDefaultPage()
})
