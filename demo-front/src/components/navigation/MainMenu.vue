<script setup lang="ts">
import { isSubMenu, isMenuGroup, isMenuItem } from '@/core/menu'
import type { Menu } from '@/core/menu'
import MainMenuItem from '@/components/navigation/MainMenuItem.vue'
import MainMenuGroup from '@/components/navigation/MainMenuGroup.vue'
import MainSubMenu from '@/components/navigation/MainSubMenu.vue'

const props = defineProps<{
  menu: Menu
}>()
</script>

<template>
  <main-menu-item v-if="isMenuItem(props.menu)" v-bind="props.menu" />
  <main-menu-group v-if="isMenuGroup(props.menu)" v-bind="props.menu" />
  <main-sub-menu v-if="isSubMenu(props.menu)" v-bind="props.menu" />
</template>
