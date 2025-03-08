import type { App } from 'vue'
import type { Component, SVGAttributes, Ref } from 'vue'
import { useRouter } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import {
  defineMenu,
  defineSubMenu,
  defineMenuGroup,
  defineMenuItem,
  handleMenu,
  defineDefaultMenu,
  defaultMenu
} from './menu'
import views from './views'
import { defineSuperAdmin, hasRoles, hasPermissions, login, logout, User } from './auth'
import { defineRoute, createRouter } from './router'
import { definePageSizes } from './table'
import {
  defineBaseUrlOptions,
  defineRequestInterceptor,
  defineResponseInterceptor
} from './http-client'
import breadcrumbInstall from './breadcrumb'

let cachedApp: App | null = null

export const pushDefaultPage = () => {
  if (cachedApp) {
    cachedApp.runWithContext(() => {
      const router = useRouter()
      if (!defaultMenu.value) {
        return
      }
      router.push(defaultMenu.value.route ?? defaultMenu.value.index)
    })
  }
}

interface MenuRoute {
  order?: number
  icon?: Component<SVGAttributes>
  name: string
  auth?: () => boolean
  route: RouteRecordRaw
  parent?: string[]
}

export const defineMenuRoute = (mr: MenuRoute) => {
  const { order, icon, name, route, auth } = mr
  const p = mr.parent ?? []
  if (typeof route.name === 'string') {
    route.meta = { menuIndex: route.name }
    defineMenuItem(
      { order, icon, index: route.name, name, auth, route: { name: route.name } },
      ...p
    )
  } else {
    defineMenuItem({ order, icon, index: route.path, name, auth }, ...p)
  }
  defineRoute({ order, route })
}

export {
  defineBaseUrlOptions,
  defineRequestInterceptor,
  defineResponseInterceptor,
  defineMenu,
  defineSubMenu,
  defineMenuGroup,
  defineMenuItem,
  defineDefaultMenu,
  defineRoute,
  defineSuperAdmin,
  definePageSizes,
  hasRoles,
  hasPermissions,
  login,
  logout,
  User
}

export default {
  install: (app: App) => {
    cachedApp = app
    app.use(views)
    app.use(createRouter())
    handleMenu()
    app.use(breadcrumbInstall)
  }
}
