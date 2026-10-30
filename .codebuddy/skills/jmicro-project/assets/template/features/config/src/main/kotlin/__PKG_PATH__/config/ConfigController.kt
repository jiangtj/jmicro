package __PKG__.config

import com.jiangtj.micro.auth.annotations.HasRole
import com.jiangtj.micro.business.config.JMicroConfigService
import com.jiangtj.micro.business.config.PUBLIC_TAG
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
 * 系统配置管理接口（仅 admin 角色）。
 *
 * - `GET /system/configs`：配置项列表，可按 tag 过滤（secret 项已脱敏）
 * - `GET /system/configs/{key}`：单个配置项（含前端渲染用的 `schema`）
 * - `PUT /system/configs/{key}`：写入覆盖值
 * - `DELETE /system/configs/{key}`：删除覆盖值，回退默认值
 * - `GET /system/configs/{key}/properties`：生成可粘贴到 application.properties 的配置行
 * - `POST /system/configs/refresh`：清缓存并发布刷新事件
 */
@HasRole("admin")
@RestController
@RequestMapping("/system/configs")
class ConfigController(
    private val jmicroConfigService: JMicroConfigService,
) {

    @GetMapping
    fun list(@RequestParam(required = false) tag: String?): List<SystemItemInfo> {
        val configs = jmicroConfigService.getAllConfig()
        if (tag.isNullOrBlank()) {
            return configs
        }
        val target = tag.trim().lowercase()
        return configs.filter { item -> item.tag.any { it.trim().lowercase() == target } }
    }

    @GetMapping("/{key}")
    fun get(@PathVariable key: String): SystemItemInfo = findItem(key)

    @PutMapping("/{key}")
    fun update(@PathVariable key: String, @RequestBody form: ConfigForm): SystemItemInfo {
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
 * 公开配置（未标注鉴权注解，供前端读取站点名称等展示信息）
 */
@RestController
@RequestMapping("/system/configs/public")
class PublicConfigController(
    private val jmicroConfigService: JMicroConfigService,
) {
    @GetMapping
    fun list(): List<SystemItem> = jmicroConfigService.getConfigByTag(PUBLIC_TAG)
}

data class ConfigForm(var value: String = "")
