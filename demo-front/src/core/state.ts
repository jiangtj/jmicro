import { ref, reactive, computed } from 'vue'
import isMobile from 'ismobilejs'
import { useToggle, useLocalStorage, useWindowSize } from '@vueuse/core'
import Expand from '~icons/ep/expand'
import Fold from '~icons/ep/fold'
import { useLocalSwitch } from './utils'

export const check = isMobile(window.navigator)
// export const isCollapse = ref(check.phone || check.tablet)
// export const toggleCollapse = useToggle(isCollapse)

export const isShowSettingPanel = ref(false)
export const toggleSettingPanel = useToggle(isShowSettingPanel)

export const config = useLocalStorage(
  'config',
  {
    themeLight: 'default', // 'green'
    themeDark: 'default', //
    foldingMenu: check.phone || check.tablet,
    foldingButton: 'bottom', // 'bottom' | 'header' | 'hide'
    isKeepAlive: false,
    isShowTabs: true,
    tabsCloseable: true,
    header: 'breadcrumb' // 'none' | 'breadcrumb' | 'tabs' | 'title'
  },
  { mergeDefaults: true }
)

export const toggleCollapse = () => {
  config.value.foldingMenu = !config.value.foldingMenu
}
export const collapseButton = computed(() => (config.value.foldingMenu ? Expand : Fold))

export const btnPlain = useLocalSwitch('table-btn-plain', false)

const { width } = useWindowSize()
export const isGt600 = computed(() => width.value > 600)

// 侧边栏是否一直显示
export const isShowAsideWhenMenuFold = ref(true)

if (check.phone) {
  config.value.foldingButton = 'header'
  isShowAsideWhenMenuFold.value = false
} else {
  isShowAsideWhenMenuFold.value = true
}
