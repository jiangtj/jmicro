import { defineSubMenu, defineMenuItem, defineRoute, defineMenuRoute } from '@/core'
import Box from '~icons/ep/box'

export default {
  install: () => {
    defineSubMenu({
      order: 2,
      icon: Box,
      index: 'template',
      name: '模板'
    })
    defineMenuItem(
      {
        order: 1,
        index: 'vue-starter',
        name: 'Vue 欢迎页',
        route: {
          name: 'vue-starter'
        }
      },
      'template'
    )
    defineRoute({
      name: 'vue-starter',
      path: '/template/vue-starter',
      component: () => import('./VueStarterView.vue'),
      meta: {
        menuIndex: 'vue-starter'
      }
    })
    defineMenuRoute({
      name: 'Crud 模板',
      parent: ['template'],
      route: {
        name: 'template-crud',
        path: '/template/crud',
        component: () => import('./CrudView.vue')
      }
    })
  }
}
