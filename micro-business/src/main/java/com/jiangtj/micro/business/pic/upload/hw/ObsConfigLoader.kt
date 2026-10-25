package com.jiangtj.micro.business.pic.upload.hw

import com.jiangtj.micro.business.config.JMicroConfigLoader
import com.jiangtj.micro.business.config.SystemGroup
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.pic.upload.PicUploadConfigItem
import com.jiangtj.micro.business.pic.upload.toJMicroConfigItems

/**
 * 华为云 OBS 配置项定义
 */
enum class ObsConfigItem(
    override val key: String,
    override val label: String,
    override val defaultValue: String,
    override val secret: Boolean = false,
) : PicUploadConfigItem {
    END_POINT("hw.obs.end-point", "Endpoint", "https://obs.cn-north-1.myhuaweicloud.com"),
    BUCKET_NAME("hw.obs.bucket-name", "Bucket 名称", "examplebucket"),
    KEY("hw.obs.key", "AccessKey", "", true),
    SECRET("hw.obs.secret", "SecretKey", "", true),
}

/**
 * 华为云 OBS 配置项分组
 */
val obsGroup = SystemGroup("华为云 OBS", 120)

/**
 * 华为云 OBS 配置项标签
 */
const val OBS_TAG = "hw-obs"

/**
 * 将华为云 OBS 配置注册为系统配置项默认值
 */
class ObsConfigLoader : JMicroConfigLoader {

    override fun load(): List<SystemItemInfo> = ObsConfigItem.entries.toJMicroConfigItems(obsGroup, OBS_TAG)
}
