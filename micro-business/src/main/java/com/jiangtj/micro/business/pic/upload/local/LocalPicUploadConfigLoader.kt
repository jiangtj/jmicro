package com.jiangtj.micro.business.pic.upload.local

import com.jiangtj.micro.business.config.JMicroConfigLoader
import com.jiangtj.micro.business.config.SystemGroup
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.pic.upload.PicUploadConfigItem
import com.jiangtj.micro.business.pic.upload.toJMicroConfigItems

/**
 * 本地上传配置项定义
 */
enum class LocalPicUploadConfigItem(
    override val key: String,
    override val label: String,
    override val defaultValue: String,
) : PicUploadConfigItem {
    UPLOAD_PATH("jmicro.pic.upload.local.upload-path", "上传路径", System.getProperty("java.io.tmpdir")),
    URL("jmicro.pic.upload.local.url", "访问地址", "http://localhost:8080"),
}

/**
 * 本地上传配置项分组
 */
val localGroup = SystemGroup("本地存储", 130)

/**
 * 本地上传配置项标签
 */
const val LOCAL_TAG = "local"

/**
 * 将本地上传配置注册为系统配置项默认值
 */
class LocalPicUploadConfigLoader : JMicroConfigLoader {

    override fun load(): List<SystemItemInfo> =
        LocalPicUploadConfigItem.entries.toJMicroConfigItems(localGroup, LOCAL_TAG)
}
