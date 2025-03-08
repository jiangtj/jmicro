import { createRouter as createRouterAction, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import LayoutView from '@/views/LayoutView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import callbackPage from '@/views/callbackPage.vue'
import { isLogin } from '@/core/auth'

interface OrderedRoute {
  order?: number
  route: RouteRecordRaw
}

const orderedRoutes: OrderedRoute[] = []

export const defineRoute = (r: RouteRecordRaw | OrderedRoute) => {
  const orderedRoute = r as OrderedRoute
  if (orderedRoute.route) {
    orderedRoutes.push(orderedRoute)
  } else {
    orderedRoutes.push({
      route: r as RouteRecordRaw
    })
  }
}

export const createRouter = () => {
  const routes = orderedRoutes
    .sort((a, b) => (a.order ?? 999) - (b.order ?? 999))
    .map((r) => r.route)
  const router = createRouterAction({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
      { path: '/callback', name: 'callback', component: callbackPage },
      {
        path: '/login',
        name: 'login',
        component: LoginView
      },
      {
        path: '/',
        name: 'layout',
        component: LayoutView,
        children: [
          ...routes,
          { path: '/:pathMatch(.*)*', name: 'notFound', component: NotFoundView }
        ]
      }
    ]
  })
  router.beforeEach((to, from, next) => {
    if (to.name === 'callback') next()
    else if (to.name !== 'login' && !isLogin()) next({ name: 'login' })
    else next()
  })
  return router
}
