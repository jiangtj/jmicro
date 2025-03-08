<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { isSubMenu, isMenuGroup, menuKey } from '@/core/menu'
import type { MenuItem, SubMenu, MenuGroup } from '@/core/menu'

const props = defineProps<{
  item: MenuItem | SubMenu
  disableRouter?: boolean
}>()

const router = useRouter()
const jumpTo = (m: MenuItem) => {
  if (props.disableRouter) {
    return
  }
  router.push(m.route ?? m.index)
}

let dropdowns: Array<MenuItem | MenuGroup> = []
if (isSubMenu(props.item)) {
  dropdowns = props.item.children
    .map((m) => {
      if (isSubMenu(m)) {
        return []
      }
      if (isMenuGroup(m)) {
        return [m, ...m.menu]
      }
      return [m]
    })
    .flat()
}

// const dropdownsC = computed(() => {
//   return dropdowns.filter((i) => isMenuGroup(i) || !activeMenuIndexs.value.includes(i.index))
// })
</script>

<template>
  <el-breadcrumb-item v-if="isSubMenu(props.item)">
    <el-dropdown v-if="dropdowns.length > 0">
      <div class="main-breadcrumb-dropdown">
        <MainMenuIconText :icon="props.item.icon" :text="props.item.name" />
        <el-icon class="el-icon--right">
          <i-ep-arrow-down />
        </el-icon>
      </div>
      <template #dropdown>
        <el-dropdown-menu class="main-breadcrumb-dropdown-item">
          <template v-for="x in dropdowns" :key="menuKey(x)">
            <el-dropdown-item class="main-breadcrumb-no-disabled" v-if="isMenuGroup(x)" disabled>
              <el-divider>{{ x.name }}</el-divider>
            </el-dropdown-item>
            <el-dropdown-item v-else @click="jumpTo(x)">
              {{ x.name }}
            </el-dropdown-item>
          </template>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <span v-else>
      <div class="main-breadcrumb-item">
        <el-icon v-if="props.item.icon"><component :is="props.item.icon"></component></el-icon>
        <span>{{ props.item.name }}</span>
      </div>
    </span>
  </el-breadcrumb-item>
  <el-breadcrumb-item v-else>
    <div
      :class="[props.disableRouter ? 'main-breadcrumb-item' : 'main-breadcrumb-item-router']"
      @click="jumpTo(props.item)"
    >
      <MainMenuIconText :icon="props.item.icon" :text="props.item.name" />
    </div>
  </el-breadcrumb-item>
</template>

<style>
.main-breadcrumb-item {
  display: inline-flex;
  align-items: center;
  outline: none;
  font-weight: normal;
}
.main-breadcrumb-item-router {
  display: inline-flex;
  align-items: center;
  outline: none;
  cursor: pointer;

  & > span {
    font-weight: 600;
  }
}
.main-breadcrumb-dropdown {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  outline: none;
  font-weight: bold;
}
.main-breadcrumb-dropdown-item .el-divider {
  margin: 0 2px;
}
.main-breadcrumb-dropdown-item .el-divider__text {
  font-size: 0.5rem;
  line-height: 0.5rem;
  color: var(--el-text-color-secondary);
  padding: 0;
}
.main-breadcrumb-no-disabled:hover {
  cursor: default;
}
</style>
