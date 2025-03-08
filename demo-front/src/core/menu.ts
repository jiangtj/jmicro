import { computed, markRaw, ref } from 'vue'
import type { Component, SVGAttributes, Ref } from 'vue'
import type { RouteLocationRaw } from 'vue-router'
import Document from '~icons/ep/document'
import InfoFilled from '~icons/ep/info-filled'

export interface SubMenu {
  order?: number
  index: string
  icon?: Component<SVGAttributes>
  name: string
  children: Array<SubMenu | MenuGroup | MenuItem>
  auth?: () => boolean
}

export interface MenuGroup {
  order?: number
  icon?: Component<SVGAttributes>
  name: string
  menu: Array<MenuItem>
  auth?: () => boolean
}

export interface MenuItem {
  order?: number
  index: string
  icon?: Component<SVGAttributes>
  name: string
  route?: RouteLocationRaw
  auth?: () => boolean
}

export interface MenuTreeNode {
  items: Menu[]
  tree: Map<string, MenuTreeNode>
}

export type Menu = SubMenu | MenuGroup | MenuItem

export function isSubMenu(item: Menu): item is SubMenu {
  return (item as SubMenu).children !== undefined
}

export function isMenuGroup(item: Menu): item is MenuGroup {
  return (item as MenuGroup).menu !== undefined
}

export function isMenuItem(item: Menu): item is MenuItem {
  return !isSubMenu(item) && !isMenuGroup(item)
}

export function menuGroupKey(item: MenuGroup) {
  return item.name + ':' + item.menu.map((i) => i.index).join(',')
}

export function menuKey(item: Menu) {
  return isMenuGroup(item) ? menuGroupKey(item) : item.index
}

const menuTree: MenuTreeNode = {
  items: [],
  tree: new Map()
}

export function getMenuTreeNode(...parent: string[]) {
  if (!parent) {
    return menuTree
  }
  let node: MenuTreeNode = menuTree
  for (const p of parent) {
    let newNode = node.tree.get(p)
    if (!newNode) {
      newNode = { items: [], tree: new Map() }
      node.tree.set(p, newNode)
    }
    node = newNode
  }
  return node
}

interface SubMenuDefine {
  order?: number
  index: string
  icon?: Component<SVGAttributes>
  name: string
  auth?: () => boolean
}

export function defineSubMenu(item: SubMenuDefine, ...parent: string[]) {
  const { order, index, icon, name, auth } = item
  defineMenu({ order, index, icon, name, auth, children: [] }, ...parent)
}

export function defineMenuGroup(item: MenuGroup, ...parent: string[]) {
  defineMenu(item, ...parent)
}

export function defineMenuItem(item: MenuItem, ...parent: string[]) {
  defineMenu(item, ...parent)
}

export function defineMenu(item: Menu, ...parent: string[]) {
  const node = getMenuTreeNode(...parent)
  if (item.icon) {
    item.icon = markRaw(item.icon)
  }
  node.items.push(item)
  if (isSubMenu(item)) {
    item.children.forEach((child) => {
      defineMenu(child, ...parent, item.index)
    })
  }
  if (isMenuGroup(item)) {
    item.menu.forEach((child) => {
      defineMenu(child, ...parent, `g:${item.name}`)
    })
  }
}

export function defineDefaultMenu(item: MenuItem) {
  isCustomDefaultMenu.value = true
  defaultMenu.value = item
}

export const menu: Ref<Menu[]> = ref([])
export const defaultMenu: Ref<MenuItem | undefined> = ref()
export const isCustomDefaultMenu = ref(false)

export function handleMenu() {
  menu.value = handleMenuTree(menuTree)
  if (!isCustomDefaultMenu.value) {
    defaultMenu.value = findFirstMenuItem(menu.value)
  }
}
function handleMenuTree(node: MenuTreeNode | undefined): Menu[] {
  if (node === undefined) {
    return []
  }
  return node.items
    .map((item) => {
      if (isSubMenu(item)) {
        item.children = handleMenuTree(node.tree.get(item.index))
      }
      if (isMenuGroup(item)) {
        item.menu = handleMenuTree(node.tree.get(`g:${item.name}`)) as MenuItem[]
      }
      return item
    })
    .sort((a, b) => (a.order ?? 999) - (b.order ?? 999))
}

const findFirstMenuItem = (menus: Array<Menu>): MenuItem => {
  const m = menus[0]
  if (isSubMenu(m)) {
    return findFirstMenuItem(m.children)
  }
  if (isMenuGroup(m)) {
    return findFirstMenuItem(m.menu)
  }
  return m
}

// 下面是测试代码 todo 删除
defineMenu({
  icon: Document,
  index: '/s-1',
  name: '二级菜单',
  children: [
    {
      index: '/home2',
      name: '首页2'
    },
    {
      icon: InfoFilled,
      name: '组1',
      menu: [
        {
          index: '/about1',
          name: '关于1'
        },
        {
          index: '/about2',
          name: '关于2'
        }
      ]
    },
    {
      icon: Document,
      index: '/s-s-1',
      name: '三级菜单',
      children: [
        {
          index: '/s-home2',
          name: 'san1'
        },
        {
          icon: InfoFilled,
          name: '组1',
          menu: [
            {
              index: '/s-about1',
              name: 'san4'
            },
            {
              index: '/s-about2',
              name: 'san2'
            }
          ]
        },
        {
          index: '/s-home3',
          name: 'san211'
        }
      ]
    },
    {
      icon: Document,
      index: 's-s-2',
      name: '三级菜单(单)',
      children: [
        {
          index: '/s-home2f',
          name: 'ffff'
        }
      ]
    },
    {
      index: '/home3',
      name: '首页3'
    }
  ]
})

const moreM = Array.from({ length: 33 }, (v, i) => i).map((i) => ({
  index: '/more' + i,
  name: '很多菜单' + i
}))

defineMenu({
  icon: Document,
  index: '/more-sub',
  name: '测试很多菜单',
  children: []
})

moreM.forEach((x) => defineMenu(x, '/more-sub'))

export type MenuSubOrItem = MenuItem | SubMenu
export type MenuSubOrItemWithParent = (MenuItem | SubMenu) & {
  parent: Array<MenuSubOrItemWithParent>
}

const handleMenuToMap = (
  map: Map<String, MenuSubOrItemWithParent>,
  m: Menu,
  parent: MenuSubOrItemWithParent[]
) => {
  if (isMenuGroup(m)) {
    m.menu.forEach((x) => handleMenuToMap(map, x, parent))
    return
  }
  const item = Object.assign({}, m, { parent })
  map.set(m.index, item)
  if (isSubMenu(m)) {
    const p = [...parent, item]
    m.children.forEach((x) => handleMenuToMap(map, x, p))
  }
}
export const indexToMenu = computed(() => {
  const indexToMenu: Map<String, MenuSubOrItemWithParent> = new Map()
  menu.value.forEach((m) => handleMenuToMap(indexToMenu, m, []))
  return indexToMenu
})
