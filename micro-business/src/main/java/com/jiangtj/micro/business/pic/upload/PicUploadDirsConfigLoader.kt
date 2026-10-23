package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.business.config.SystemConfigLoader
import com.jiangtj.micro.business.config.SystemGroup
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.config.SystemItemType
import com.jiangtj.micro.common.JsonUtils

/**
 * 图片上传目录映射分组
 */
val picUploadGroup = SystemGroup("图片上传", 120)

/**
 * 图片上传目录映射标签
 */
const val PIC_UPLOAD_TAG = "pic-upload"

/**
 * 将 `jmicro.pic.upload.dirs` 注册为系统配置项（JSON 类型）。
 *
 * 配置值形如：
 * ```json
 * {"avatar":{"path":"/avatar","provider":"local","maxFileSize":10485760}}
 * ```
 * 运行期读取见 [PicUploadService]，未配置时回退到 `jmicro.pic.upload.dirs.*` 的启动绑定值。
 */
class PicUploadDirsConfigLoader(
    private val properties: PicUploadProperties,
) : SystemConfigLoader {

    private val dirsType = JsonUtils.jsonMapper.typeFactory
        .constructMapType(LinkedHashMap::class.java, String::class.java, PicUploadProperties.Dir::class.java)

    override fun load(): List<SystemItemInfo> = listOf(
        SystemItemInfo(
            key = KEY,
            name = "上传目录映射",
            description = "按上传目标（target）配置路径、服务商与上传限制",
            value = JsonUtils.toJson(properties.dirs) ?: "{}",
            type = SystemItemType.JSON.name,
            jsonType = dirsType,
            group = picUploadGroup,
            order = 0,
            tag = listOf(PIC_UPLOAD_TAG),
        )
    )

    companion object {
        const val KEY = "jmicro.pic.upload.dirs"
    }
}
