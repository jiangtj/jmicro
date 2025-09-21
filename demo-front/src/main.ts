import './assets/main.css'

import {createApp, watchEffect} from 'vue'
import App from './App.vue'
import core, {defineBaseUrlOptions, definePageSizes} from './core'
import './style'
import {useRegisterSW} from 'virtual:pwa-register/vue'
import token from './core/token'
import Casdoor from 'casdoor-vue-sdk'
import {createLogto, type LogtoConfig} from '@logto/vue';


defineBaseUrlOptions({
  name: '17001',
  baseUrl: 'http://localhost:17001'
})

const app = createApp(App)

app.use(core).use(token)

const config = {
    serverUrl: "http://192.168.31.10:8000",
    clientId: "a1f9883530433d009fb1",
    organizationName: "built-in",
    appName: "application_he3oml",
    redirectPath: "/callback",
};
app.use(Casdoor, config)

const config2: LogtoConfig = {
    endpoint: 'http://192.168.31.10:8000',
    appId: 'a1f9883530433d009fb1',
};
app.use(createLogto, config2);

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
