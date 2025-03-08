import './assets/main.css'

import { createApp, watchEffect } from 'vue'
import App from './App.vue'
import core, { defineBaseUrlOptions, definePageSizes } from './core'
import { mock } from './core/http-client'
import './style'
import { useRegisterSW } from 'virtual:pwa-register/vue'
import token from './core/token'

defineBaseUrlOptions({
  name: '17001',
  baseUrl: 'http://localhost:17001'
})

const app = createApp(App)

app.use(core).use(token)

definePageSizes(10, 20, 50, 100)

app.mount('#app')

const intervalMS = 60 * 60 * 1000

const { needRefresh, updateServiceWorker } = useRegisterSW({
  onRegistered(r) {
    r &&
      setInterval(() => {
        r.update()
      }, intervalMS)
  }
})

watchEffect(() => {
  if (needRefresh.value) {
    const n = ElNotification({
      title: '更新提醒',
      message: '系统升级，点击该消息启用新版本',
      type: 'info',
      showClose: false,
      customClass: 'main-upgrade-notification',
      onClick() {
        updateServiceWorker()
        n.close()
      },
      duration: 0
    })
  }
})
