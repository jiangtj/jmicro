import { defineSubMenu, defineMenuItem, defineRoute, defineMenuRoute } from '@/core'
import Box from '~icons/ep/box'

export default {
  install: () => {
    defineSubMenu({
      icon: Box,
      index: 'test',
      name: '测试'
    })
    defineMenuRoute({
      name: '权限测试',
      parent: ['test'],
      route: {
        name: 'test-auth',
        path: '/test/auth',
        component: () => import('./AuthView.vue')
      }
    })
    defineMenuRoute({
      name: 'Form 校验测试',
      parent: ['test'],
      route: {
        name: 'test-validate',
        path: '/test/validate',
        component: () => import('./FormValidateView.vue')
      }
    })
    defineMenuRoute({
      name: 'Table 测试',
      parent: ['test'],
      route: {
        name: 'test-table',
        path: '/test/table',
        component: () => import('./TableView.vue')
      }
    })
  }
}
