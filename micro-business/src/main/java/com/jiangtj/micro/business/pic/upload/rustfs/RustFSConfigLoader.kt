package com.jiangtj.micro.business.pic.upload.rustfs

import com.jiangtj.micro.business.config.JMicroConfigLoader
import com.jiangtj.micro.business.config.SystemGroup
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.config.SystemItemType
import com.jiangtj.micro.business.pic.upload.PicUploadConfigItem
import com.jiangtj.micro.business.pic.upload.toJMicroConfigItems

/**
 * RustFS 配置项定义
 */
enum class RustFSConfigItem(
    override val key: String,
    override val label: String,
    override val defaultValue: String,
    override val secret: Boolean = false,
    override val type: SystemItemType = SystemItemType.TEXT,
) : PicUploadConfigItem {
    ENABLE("jmicro.rustfs.enable", "是否启用", "false", type = SystemItemType.SWITCH),
    ENDPOINT("jmicro.rustfs.endpoint", "Endpoint", "http://localhost:9000"),
    ACCESS_KEY("jmicro.rustfs.access-key", "AccessKey", "", true),
    SECRET_KEY("jmicro.rustfs.secret-key", "SecretKey", "", true),
    BUCKET("jmicro.rustfs.bucket", "Bucket", "bucket"),
    IS_ALLOW_GET_OBJECT(
        "jmicro.rustfs.is-allow-get-object",
        "允许匿名读取",
        "false",
        type = SystemItemType.SWITCH,
    ),
}

/**
 * RustFS 配置项分组
 */
val rustFSGroup = SystemGroup("RustFS", 150)

/**
 * RustFS 配置项标签
 */
const val RUSTFS_TAG = "rustfs"

/**
 * 将 RustFS 配置注册为系统配置项默认值
 */
class RustFSConfigLoader : JMicroConfigLoader {

    override fun load(): List<SystemItemInfo> = RustFSConfigItem.entries.toJMicroConfigItems(rustFSGroup, RUSTFS_TAG)
}
