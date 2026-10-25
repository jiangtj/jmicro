package com.jiangtj.micro.business.pic.upload.minio

import com.jiangtj.micro.business.config.JMicroConfigLoader
import com.jiangtj.micro.business.config.SystemGroup
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.config.SystemItemType
import com.jiangtj.micro.business.pic.upload.PicUploadConfigItem
import com.jiangtj.micro.business.pic.upload.toJMicroConfigItems

/**
 * MinIO 配置项定义
 */
enum class MinIOConfigItem(
    override val key: String,
    override val label: String,
    override val defaultValue: String,
    override val secret: Boolean = false,
    override val type: SystemItemType = SystemItemType.TEXT,
) : PicUploadConfigItem {
    ENDPOINT("minio.endpoint", "Endpoint", "http://localhost:9000"),
    ACCESS_KEY("minio.access-key", "AccessKey", "", true),
    SECRET_KEY("minio.secret-key", "SecretKey", "", true),
    BUCKET("minio.bucket", "Bucket", "bucket"),
    IS_ALLOW_GET_OBJECT(
        "minio.is-allow-get-object",
        "允许匿名读取",
        "false",
        type = SystemItemType.SWITCH,
    ),
}

/**
 * MinIO 配置项分组
 */
val minioGroup = SystemGroup("MinIO", 140)

/**
 * MinIO 配置项标签
 */
const val MINIO_TAG = "minio"

/**
 * 将 MinIO 配置注册为系统配置项默认值
 */
class MinIOConfigLoader : JMicroConfigLoader {

    override fun load(): List<SystemItemInfo> = MinIOConfigItem.entries.toJMicroConfigItems(minioGroup, MINIO_TAG)
}
