package __PKG__.config

import com.jiangtj.micro.business.config.JMicroConfigLoader
import com.jiangtj.micro.business.config.PUBLIC_TAG
import com.jiangtj.micro.business.config.SystemGroup
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.config.SystemItemType
import org.springframework.stereotype.Component
import java.util.function.Function

/**
 * 默认配置项（运行期可改，覆盖值由 JMicroConfigSaver 持久化）。
 *
 * 需要 `jmicro.config.enabled=true` 才生效。
 * 启动期必须确定的配置（端口、数据源、密钥）仍放 application.properties。
 */
@Component
class AppConfigLoader : JMicroConfigLoader {

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
            key = "app.site.maintenance",
            name = "维护模式",
            description = "开启后站点进入维护状态",
            value = "false",
            group = siteGroup,
            type = SystemItemType.SWITCH.name,
            order = 1,
            valueFormatter = Function {
                val v = it.trim().lowercase()
                if (v != "true" && v != "false") throw IllegalArgumentException("仅支持 true / false")
                v
            },
        ),
    )

    companion object {
        val siteGroup = SystemGroup("站点", 10)
    }
}
