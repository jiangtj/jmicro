package com.jiangtj.micro.demobackend.config

import com.jiangtj.micro.business.config.PUBLIC_TAG
import com.jiangtj.micro.business.config.JMicroConfigLoader
import com.jiangtj.micro.business.config.SystemGroup
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.config.SystemItemType
import com.jiangtj.micro.common.JsonUtils
import org.springframework.stereotype.Component
import java.util.function.Function

/**
 * 演示用系统配置项，覆盖 TEXT / TEXTAREA / SWITCH / SELECT / JSON 五种前端表单类型。
 *
 * 这里的 [SystemItemInfo.value] 是默认值，运行期通过接口修改后的值持久化在 `sys_config` 表。
 */
@Component
class DemoConfigLoader : JMicroConfigLoader {

    private val socialLinksType = JsonUtils.jsonMapper.typeFactory
        .constructMapType(LinkedHashMap::class.java, String::class.java, String::class.java)

    private val themes = listOf("light" to "浅色", "dark" to "深色")

    override fun load(): List<SystemItemInfo> = listOf(
        SystemItemInfo(
            key = "demo.site.name",
            name = "站点名称",
            description = "展示在页面标题与导航栏上的名称",
            value = "JMicro Demo",
            group = demoSiteGroup,
            type = SystemItemType.TEXT.name,
            tag = listOf(PUBLIC_TAG),
            order = 0,
            valueFormatter = Function { it.trim().ifEmpty { throw IllegalArgumentException("站点名称不能为空") } },
        ),
        SystemItemInfo(
            key = "demo.site.description",
            name = "站点描述",
            description = "展示在首页的站点介绍",
            value = "基于 JMicro 的演示站点",
            group = demoSiteGroup,
            type = SystemItemType.TEXTAREA.name,
            tag = listOf(PUBLIC_TAG),
            order = 1,
        ),
        SystemItemInfo(
            key = "demo.site.maintenance",
            name = "维护模式",
            description = "开启后站点进入维护状态",
            value = "false",
            group = demoSiteGroup,
            type = SystemItemType.SWITCH.name,
            order = 2,
            valueFormatter = Function {
                val v = it.trim().lowercase()
                if (v != "true" && v != "false") throw IllegalArgumentException("仅支持 true / false")
                v
            },
        ),
        SystemItemInfo(
            key = "demo.site.theme",
            name = "站点主题",
            description = "前端配色方案",
            value = "light",
            group = demoSiteGroup,
            type = SystemItemType.SELECT.name,
            tag = listOf(PUBLIC_TAG),
            order = 3,
            provider = themes,
            valueFormatter = Function {
                val v = it.trim().lowercase()
                if (themes.none { theme -> theme.first == v }) {
                    throw IllegalArgumentException("可选值: ${themes.joinToString(",") { theme -> theme.first }}")
                }
                v
            },
        ),
        SystemItemInfo(
            key = "demo.site.social-links",
            name = "社交链接",
            description = "JSON 对象，key 为平台名称，value 为链接地址",
            value = """{"github":"https://github.com/jiangtj/jmicro"}""",
            group = demoSiteGroup,
            type = SystemItemType.JSON.name,
            jsonType = socialLinksType,
            tag = listOf(PUBLIC_TAG),
            order = 4,
        ),
    )

    companion object {
        val demoSiteGroup = SystemGroup("演示站点", 10)
    }
}
