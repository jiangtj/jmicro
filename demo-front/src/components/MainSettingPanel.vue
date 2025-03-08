<script setup lang="ts">
import { reactive } from 'vue'
import { env as httpEnv, options as httpOptions } from '@/core/http-client'
import { isShowSettingPanel, config, btnPlain } from '@/core/state'
import { pageBackground, pagePosition, PagePositionLabel } from '@/core/table'
import { isAlert, confirmTypes, confirmType } from '@/core/confirm-box'
import { useColorMode } from '@vueuse/core'
import IconThemeAuto from '@/components/icons/IconThemeAuto.vue'
import IconThemeLight from '@/components/icons/IconThemeLight.vue'
import IconThemeDark from '@/components/icons/IconThemeDark.vue'

const { store: colorStore } = useColorMode()
const modeOptions = ['auto', 'light', 'dark']
const modeConfig: any = {
  auto: {
    icon: IconThemeAuto
  },
  light: {
    icon: IconThemeLight
  },
  dark: {
    icon: IconThemeDark
  }
}

const isModifyHttpEnv = import.meta.env.VITE_HTTP_ENV_MODIFY
</script>

<template>
  <el-drawer class="main-setting-panel" v-model="isShowSettingPanel" title="项目配置" size="100%">
    <el-form label-position="left" label-width="70%">
      <el-divider>主题</el-divider>
      <el-form-item label="主题">
        <el-segmented v-model="colorStore" :options="modeOptions">
          <template #default="{ item }">
            <div v-if="modeConfig[String(item)].icon" class="main-theme-icon">
              <component :is="modeConfig[String(item)].icon" />
            </div>
            <div v-if="modeConfig[String(item)].text">{{ modeConfig[String(item)].text }}</div>
          </template>
        </el-segmented>
      </el-form-item>
      <el-form-item v-if="colorStore !== 'dark'" label="亮">
        <el-select v-model="config.themeLight">
          <el-option label="默认" value="default" />
          <el-option label="灵动" value="smart" />
          <el-option label="绿(测试)" value="green" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="colorStore !== 'light'" label="暗">
        <el-select v-model="config.themeDark">
          <el-option label="默认" value="default" />
        </el-select>
      </el-form-item>
      <el-divider>功能区</el-divider>
      <el-form-item label="环境" v-if="isModifyHttpEnv">
        <el-select v-model="httpEnv">
          <el-option
            v-for="item in httpOptions"
            :key="item.name"
            :label="item.name"
            :value="item.name"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="标签页缓存">
        <el-switch v-model="config.isKeepAlive" />
      </el-form-item>
      <el-form-item>
        <template #label>
          <div class="main-setting-panel-label">
            <span>弹出框提醒</span>
            <el-tooltip
              effect="dark"
              content="如关闭,则不会提醒一些关键操作,例如:表格中的删除与切换状态"
              placement="top"
            >
              <el-icon><i-ep-info-filled /></el-icon>
            </el-tooltip>
          </div>
        </template>
        <template #default>
          <el-switch v-model="isAlert" />
        </template>
      </el-form-item>
      <el-divider>界面配置</el-divider>
      <el-form-item label="顶部菜单">
        <el-select v-model="config.header">
          <el-option label="面包屑" value="breadcrumb" />
          <el-option label="标题" value="title" />
          <el-option label="标签页" value="tabs" />
          <el-option label="无" value="none" />
        </el-select>
      </el-form-item>
      <el-form-item label="标签页" v-if="config.header !== 'tabs'">
        <el-switch v-model="config.isShowTabs" />
      </el-form-item>
      <el-form-item label="标签页关闭按钮" v-if="config.header === 'tabs' || config.isShowTabs">
        <el-switch v-model="config.tabsCloseable" />
      </el-form-item>
      <el-form-item label="按钮 Plain 样式">
        <MainSwitch v-model="btnPlain" />
      </el-form-item>
      <el-form-item label="菜单折叠">
        <el-switch v-model="config.foldingMenu" />
      </el-form-item>
      <el-form-item label="菜单折叠按钮">
        <el-select v-model="config.foldingButton">
          <el-option label="底部" value="bottom" />
          <el-option label="顶部" value="header" />
          <el-option label="不显示" value="hide" />
        </el-select>
      </el-form-item>
      <MainFormItem label="表格分页位置">
        <MainSelect v-model="pagePosition" :options="PagePositionLabel" />
      </MainFormItem>
      <MainFormItem label="表格分页背景色">
        <MainSwitch v-model="pageBackground" />
      </MainFormItem>
      <MainFormItem label="弹出框样式(功能区)">
        <MainSelect v-model="confirmType" :options="confirmTypes" />
      </MainFormItem>
    </el-form>
  </el-drawer>
</template>

<style>
.main-setting-panel {
  max-width: 400px;
}
.main-setting-panel .el-drawer__body {
  padding-top: 0;
  padding-bottom: 0;
}
.main-setting-panel .el-form-item__content {
  justify-content: end;
}

.main-setting-panel .el-segmented {
  --el-border-radius-base: 16px;
}
.main-theme-icon {
  display: flex;
  align-items: center;
}
</style>
