package com.jiangtj.micro.demobackend.controller

import com.jiangtj.micro.auth.annotations.HasRole
import com.jiangtj.micro.business.config.PUBLIC_TAG
import com.jiangtj.micro.business.config.JMicroConfigService
import com.jiangtj.micro.business.config.SystemItem
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.config.trimKey
import com.jiangtj.micro.web.BaseExceptionUtils
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 系统配置管理接口（仅 admin 角色可访问）
 *
 * - `GET /system/configs`：配置项列表，可按 `tag` 过滤，`secret` 项已脱敏
 * - `GET /system/configs/{key}`：单个配置项（含前端渲染用的 `schema`）
 * - `PUT /system/configs/{key}`：写入覆盖值并持久化到 `sys_config`
 * - `DELETE /system/configs/{key}`：删除覆盖值，回退到默认值
 * - `GET /system/configs/{key}/properties`：生成可粘贴到 `application.properties` 的配置行
 * - `POST /system/configs/refresh`：清空缓存并发布配置刷新事件
 */
@HasRole("admin")
@RestController
@RequestMapping("/system/configs")
class JMicroConfigController(
    private val jmicroConfigService: JMicroConfigService,
) {

    @GetMapping
    fun list(@RequestParam(required = false) tag: String?): List<SystemItemInfo> {
        val configs = jmicroConfigService.getAllConfig()
        if (tag.isNullOrBlank()) {
            return configs
        }
        val target = tag.trim().lowercase()
        return configs.filter { config -> config.tag.any { it.trim().lowercase() == target } }
    }

    @GetMapping("/{key}")
    fun get(@PathVariable key: String): SystemItemInfo = findItem(key)

    @PutMapping("/{key}")
    fun update(@PathVariable key: String, @RequestBody form: JMicroConfigForm): SystemItemInfo {
        jmicroConfigService.updateConfig(key, form.value)
        return findItem(key)
    }

    @DeleteMapping("/{key}")
    fun reset(@PathVariable key: String): SystemItemInfo {
        jmicroConfigService.deleteConfig(key)
        return findItem(key)
    }

    /**
     * 生成 `jmicro.config.kv.*` 形式的配置行，方便前端提供「复制配置」能力
     */
    @GetMapping("/{key}/properties")
    fun properties(@PathVariable key: String): String =
        jmicroConfigService.generateConfig(key, jmicroConfigService.getValue(key))

    @PostMapping("/refresh")
    fun refresh() {
        jmicroConfigService.refreshConfig()
    }

    private fun findItem(key: String): SystemItemInfo =
        jmicroConfigService.getAllConfig()
            .firstOrNull { it.key.trimKey() == key.trimKey() }
            ?: throw BaseExceptionUtils.notFound("配置项不存在：$key")
}

/**
 * 公开的系统配置接口（未标注鉴权注解，无需登录与权限）
 *
 * - `GET /system/configs/public`：返回 `tag` 含 [PUBLIC_TAG] 的配置项，供前端读取站点名称等展示信息
 *
 * 配置项是否公开由其 `tag` 决定（见 `DemoJMicroConfigLoader`），敏感项不会标记为 `public`。
 */
@RestController
@RequestMapping("/system/configs/public")
class PublicJMicroConfigController(
    private val jmicroConfigService: JMicroConfigService,
) {

    @GetMapping
    fun list(): List<SystemItem> = jmicroConfigService.getConfigByTag(PUBLIC_TAG)
}

/**
 * 配置项写入表单
 */
data class JMicroConfigForm(
    var value: String = "",
)
