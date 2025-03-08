<script setup lang="ts">
import { ref } from 'vue'
import type { Ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { RouteLocationNormalizedGeneric } from 'vue-router'
import type { TabsPaneContext, TabPaneName } from 'element-plus'
import { defaultMenu } from '@/core/menu'
import { type MenuTab } from '@/core/menu-tabs'
import { config } from '@/core/state'
import { pushDefaultPage } from '@/core'

const router = useRouter()
const route = useRoute()

const props = defineProps<{
  convertTab: (index: string) => string | undefined
}>()

const activeTab: Ref<string> = ref('_temporary')
const tabs = ref<MenuTab[]>([])

const getPosition = (index: string): number => {
  for (let i = 0; i < tabs.value.length; i++) {
    const element = tabs.value[i]
    if (element.index === index) {
      return i
    }
  }
  return -1
}

const initRecord = () => {
  tabs.value = []
}

const pushRecord = (r: RouteLocationNormalizedGeneric) => {
  const index = r.meta.tabIndex ?? r.meta.menuIndex
  if (!index) {
    return
  }
  const tabName = props.convertTab(index) ?? r.meta.tabName
  if (!tabName) {
    return
  }
  const { name, params, query, hash } = r
  const tab: MenuTab = {
    name: tabName,
    index,
    route: { name, params, query, hash }
  }

  activeTab.value = index

  const pos = getPosition(index)
  if (pos === -1) {
    tabs.value.push(tab)
  } else {
    tabs.value[pos] = tab
  }
}

if (route.name !== 'login') {
  pushRecord(route)
}

router.beforeEach((to, from, next) => {
  if (to.name === 'login') {
    initRecord()
  } else {
    pushRecord(to)
  }
  next()
})

const tabClick = (ctx: TabsPaneContext) => {
  if (!ctx.index) {
    return
  }
  const tab = tabs.value[Number(ctx.index)]
  if (tab.route) {
    router.push(tab.route)
  }
}

const tabRemove = (targetName: TabPaneName) => {
  if (typeof targetName === 'string') {
    let index = getPosition(targetName)
    let lastIndex = tabs.value.length - 1
    if (index === lastIndex) {
      router.push({ name: tabs.value[lastIndex - 1].index })
    } else {
      router.push({ name: tabs.value[index + 1].index })
    }
    tabs.value.splice(index, 1)
  }
}

const tooltipPaneName = ref()
const buttonRef = ref()
const tooltipRef = ref()
const visible = ref(false)
const handleContextMenu = (e: any) => {
  let paneName = e.target.id
  if (paneName) {
    console.log(e)
    buttonRef.value = e.target
    visible.value = true
    tooltipPaneName.value = paneName.replace('tab-', '')
    console.log(tooltipPaneName.value)
    e.preventDefault()
  }
}
const closeCurrent = () => {
  let index = getPosition(tooltipPaneName.value)
  tabs.value.splice(index, 1)
  if (tooltipPaneName.value === activeTab.value) {
    index = Math.min(index, tabs.value.length - 1)
    router.push(tabs.value[index].route)
  }
}
const closeLeft = () => {
  let index = getPosition(tooltipPaneName.value)
  router.push(tabs.value[index].route)
  tabs.value = tabs.value.slice(index, tabs.value.length)
}
const closeRight = () => {
  let index = getPosition(tooltipPaneName.value)
  router.push(tabs.value[index].route)
  tabs.value = tabs.value.slice(0, index + 1)
}
const closeOther = () => {
  const m = tabs.value[getPosition(tooltipPaneName.value)]
  router.push(m.route)
  tabs.value = [m]
}
const closeAll = () => {
  if (!defaultMenu.value) {
    return
  }
  if (defaultMenu.value.index === activeTab.value) {
    closeOther()
    return
  }
  initRecord()
  pushDefaultPage()
}

const isShowClosable = () => {
  return config.value.tabsCloseable
}
</script>

<template>
  <div class="main-tabs">
    <el-tabs
      :model-value="activeTab"
      type="card"
      class="main-router-tabs"
      @tab-click="tabClick"
      @tab-remove="tabRemove"
      @contextmenu="handleContextMenu($event)"
      :closable="isShowClosable()"
    >
      <el-tab-pane v-for="item in tabs" :key="item.index" :label="item.name" :name="item.index" />
    </el-tabs>

    <el-tooltip
      ref="tooltipRef"
      :visible="visible"
      effect="light"
      :popper-options="{
        modifiers: [
          {
            name: 'computeStyles',
            options: {
              adaptive: false,
              enabled: false
            }
          }
        ]
      }"
      :virtual-ref="buttonRef"
      virtual-triggering
      popper-class="singleton-tooltip"
    >
      <template #content>
        <div @mouseleave="visible = false">
          <el-row @click="closeCurrent()">
            <el-text>
              <el-icon><i-ep-close /></el-icon>关闭
            </el-text>
          </el-row>
          <el-row @click="closeLeft()">
            <el-text>
              <el-icon><i-ep-back /></el-icon>关闭左侧
            </el-text>
          </el-row>
          <el-row @click="closeRight()">
            <el-text>
              <el-icon><i-ep-right /></el-icon>关闭右侧
            </el-text>
          </el-row>
          <el-row @click="closeOther()">
            <el-text>
              <el-icon><i-ep-close-bold /></el-icon>关闭其他
            </el-text>
          </el-row>
          <el-row @click="closeAll()">
            <el-text>
              <el-icon><i-ep-circle-close-filled /></el-icon>关闭全部
            </el-text>
          </el-row>
        </div>
      </template>
    </el-tooltip>
  </div>
</template>

<style lang="scss">
.main-tabs {
  width: 100%;
  display: inline-flex;
  justify-content: space-between;
  align-items: center;
  height: var(--el-tabs-header-height);

  .main-router-tabs {
    width: 100%;
  }

  .el-tabs {
    --el-tabs-header-height: var(--main-tabs-height);
  }

  .el-tabs__header {
    margin: 0;
  }

  .el-tabs--card > .el-tabs__header {
    border-top: 1px solid var(--el-border-color-light);
  }

  .el-tabs--card > .el-tabs__header .el-tabs__nav {
    border-radius: 0 !important;
  }

  .el-tabs--card > .el-tabs__header .el-tabs__nav {
    border-radius: 0 !important;
    border-top: 0 !important;
  }
  .el-tabs__nav-prev,
  .el-tabs__nav-next {
    height: 100%;
  }
}

.singleton-tooltip {
  padding: 5px 0 !important;
}

.singleton-tooltip .el-row {
  padding: 5px 10px;
  cursor: pointer;
}

.singleton-tooltip .el-row:hover {
  background-color: #f5f7fa;
}
</style>
