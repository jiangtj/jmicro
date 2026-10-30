package __PKG__.config

import com.jiangtj.micro.business.config.JMicroConfigLoader
import com.jiangtj.micro.business.config.PUBLIC_TAG
import com.jiangtj.micro.business.config.SystemGroup
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.config.SystemItemType
import com.jiangtj.micro.common.JsonUtils
import org.springframework.stereotype.Component
import java.util.function.Function

/**
 * 默认配置项（与 jmicro demo 的 DemoConfigLoader 同构）。
 *
 * 覆盖 TEXT / TEXTAREA / SWITCH / JSON 四种前端表单类型；
 * 这里的 `value` 是默认值，运行期修改后的覆盖值由 `JMicroConfigSaver` 持久化
 * （有 db 特性时落 `sys_config` 表，否则在内存）。
 *
 * 需要 `jmicro.config.enabled=true` 才生效。
 * 启动期必须确定的配置（端口、数据源、密钥）仍放 application.properties。
 */
@Component
class AppConfigLoader : JMicroConfigLoader {

    private val socialLinksType = JsonUtils.jsonMapper.typeFactory
        .constructMapType(LinkedHashMap::class.java, String::class.java, String::class.java)

    override fun load(): List<SystemItemInfo> = listOf(
        SystemItemInfo(
            key = "app.site.name",
            name = "站点名称",
            description = "展示在页面标题与导航栏上的名称",
            value = "__PROJECT_NAME__",
            group = siteGroup,
            type = SystemItemType.TEXT.name,
            tag = listOf(PUBLIC_TAG),
            order = 0,
            valueFormatter = Function { it.trim().ifEmpty { throw IllegalArgumentException("站点名称不能为空") } },
        ),
        SystemItemInfo(
            key = "app.site.description",
            name = "站点描述",
            description = "展示在首页的站点介绍",
            value = "基于 JMicro 的演示站点",
            group = siteGroup,
            type = SystemItemType.TEXTAREA.name,
            tag = listOf(PUBLIC_TAG),
            order = 1,
        ),
        SystemItemInfo(
            key = "app.site.maintenance",
            name = "维护模式",
            description = "开启后站点进入维护状态",
            value = "false",
            group = siteGroup,
            type = SystemItemType.SWITCH.name,
            order = 2,
            valueFormatter = Function {
                val v = it.trim().lowercase()
                if (v != "true" && v != "false") throw IllegalArgumentException("仅支持 true / false")
                v
            },
        ),
        SystemItemInfo(
            key = "app.site.social-links",
            name = "社交链接",
            description = "JSON 对象，key 为平台名称，value 为链接地址",
            value = """{"github":"https://github.com/jiangtj/jmicro"}""",
            group = siteGroup,
            type = SystemItemType.JSON.name,
            jsonType = socialLinksType,
            tag = listOf(PUBLIC_TAG),
            order = 3,
        ),
    )

    companion object {
        val siteGroup = SystemGroup("站点", 10)
    }
}
