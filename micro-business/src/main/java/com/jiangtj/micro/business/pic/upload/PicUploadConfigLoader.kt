package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.business.config.SystemConfigLoader
import com.jiangtj.micro.business.config.SystemGroup
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.config.SystemItemType
import com.jiangtj.micro.common.JsonUtils

/**
 * 图片上传配置项分组
 */
val picUploadGroup = SystemGroup("图片上传", 120)

/**
 * 图片上传配置项标签
 */
const val PIC_UPLOAD_TAG = "pic-upload"

/**
 * 图片上传通用配置项定义
 *
 * [key] 同时是启动绑定的属性名（`jmicro.pic.upload.*`），开启系统配置后可在运行期覆盖。
 */
enum class PicUploadBaseConfigItem(
    override val key: String,
    override val label: String,
    override val defaultValue: String,
) : PicUploadConfigItem {

    /**
     * 默认的文件上传服务商
     */
    PROVIDER("jmicro.pic.upload.provider", "默认上传服务商", "local"),

    /**
     * 默认允许的文件类型，逗号分隔
     */
    ALLOWED_EXTENSIONS(
        "jmicro.pic.upload.allowed-extensions",
        "默认允许的文件类型",
        "jpg,jpeg,png,gif,webp,bmp,svg,avif",
    ),

    /**
     * 默认最大文件大小，支持 `10MB`、`1KB` 或字节数
     */
    MAX_FILE_SIZE("jmicro.pic.upload.max-file-size", "默认最大文件大小", "10MB"),
}

/**
 * 将 `jmicro.pic.upload.*` 通用配置与目录映射注册为系统配置项。
 *
 * - 通用配置（[PicUploadBaseConfigItem]）为文本类型，默认值取自启动绑定值；
 * - 目录映射 `jmicro.pic.upload.dirs` 为 JSON 类型，配置值形如：
 * ```json
 * {"avatar":{"path":"/avatar","provider":"local","maxFileSize":10485760}}
 * ```
 *
 * 运行期读取见 [PicUploadService]，未配置时回退到 `jmicro.pic.upload.*` 的启动绑定值。
 */
class PicUploadConfigLoader(
    private val properties: PicUploadProperties,
) : SystemConfigLoader {

    private val dirsType = JsonUtils.jsonMapper.typeFactory
        .constructMapType(LinkedHashMap::class.java, String::class.java, PicUploadProperties.Dir::class.java)

    override fun load(): List<SystemItemInfo> = listOf(
        properties.provider.toSystemItem(PicUploadBaseConfigItem.PROVIDER, 0),
        properties.allowedExtensions.joinToString(",")
            .toSystemItem(PicUploadBaseConfigItem.ALLOWED_EXTENSIONS, 1),
        properties.maxFileSize.toString().toSystemItem(PicUploadBaseConfigItem.MAX_FILE_SIZE, 2),
        SystemItemInfo(
            key = KEY,
            name = "上传目录映射",
            description = "按上传目标（target）配置路径、服务商与上传限制",
            value = JsonUtils.toJson(properties.dirs) ?: "{}",
            type = SystemItemType.JSON.name,
            jsonType = dirsType,
            group = picUploadGroup,
            order = 3,
            tag = listOf(PIC_UPLOAD_TAG),
        ),
    )

    private fun String.toSystemItem(item: PicUploadBaseConfigItem, order: Int): SystemItemInfo =
        SystemItemInfo(
            key = item.key,
            name = item.label,
            description = item.label,
            value = this,
            secret = item.secret,
            group = picUploadGroup,
            type = item.type.name,
            order = order,
            tag = listOf(PIC_UPLOAD_TAG),
        )

    companion object {
        const val KEY = "jmicro.pic.upload.dirs"
    }
}
