import { ref, computed, type App } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuSubOrItemWithParent, indexToMenu } from '@/core/menu'

export const menu = ref<MenuSubOrItemWithParent>()
export const activeMenuItem = ref<MenuSubOrItemWithParent>()

export const breadcrumbMenu = computed(() => {
  return menu.value ?? activeMenuItem.value
})

export const defineBreadcrumbMenu = (val: MenuSubOrItemWithParent) => {
  menu.value = val
}

export default {
  install: (app: App) => {
    app.runWithContext(() => {
      const router = useRouter()
      router.beforeEach((to, from, next) => {
        menu.value = undefined
        activeMenuItem.value = to.meta.menuIndex
          ? indexToMenu.value.get(to.meta.menuIndex)
          : undefined
        next()
      })
    })
  }
}
