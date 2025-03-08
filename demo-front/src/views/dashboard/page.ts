import { defineSubMenu, defineMenuRoute, hasPermissions } from '@/core'
import MenuIcon from '~icons/ep/menu'

export default {
  install: () => {
    defineSubMenu({
      order: 1,
      icon: MenuIcon,
      index: 'dashboard',
      name: 'Dashboard',
      auth: () => hasPermissions('analysis') || hasPermissions('workbench')
    })
    defineMenuRoute({
      name: '分析页',
      parent: ['dashboard'],
      auth: () => hasPermissions('analysis'),
      route: {
        name: 'dashboard-analysis',
        path: '/dashboard/analysis',
        component: () => import('./AnalysisView.vue')
      }
    })
    defineMenuRoute({
      name: '工作台',
      parent: ['dashboard'],
      auth: () => hasPermissions('workbench'),
      route: {
        name: 'dashboard-workbench',
        path: '/dashboard/workbench',
        component: () => import('./WorkbenchView.vue')
      }
    })
  }
}
