package com.jiangtj.micro.business.pic.upload.ali

import com.jiangtj.micro.business.config.SystemConfigLoader
import com.jiangtj.micro.business.config.SystemGroup
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.pic.upload.PicUploadConfigItem
import com.jiangtj.micro.business.pic.upload.toSystemConfigItems

/**
 * 阿里云 OSS 配置项定义
 *
 * [key] 为系统配置项的键（即原 `ali.oss.*` 属性名），取值需使用 `systemConfigService.getValue(key.key)`。
 */
enum class AliOSSConfigItem(
    override val key: String,
    override val label: String,
    override val defaultValue: String,
    override val secret: Boolean = false,
) : PicUploadConfigItem {
    ENDPOINT("ali.oss.endpoint", "Endpoint", "https://oss-cn-hangzhou.aliyuncs.com"),
    REGION("ali.oss.region", "Region", "cn-hangzhou"),
    BUCKET_NAME("ali.oss.bucket-name", "Bucket 名称", "examplebucket"),
    URL("ali.oss.url", "访问地址", "https://www.aliyun.com/"),
    ACCESS_KEY_ID("ali.oss.access-key-id", "AccessKeyId", "", true),
    SECRET_ACCESS_KEY("ali.oss.secret-access-key", "SecretAccessKey", "", true),
}

/**
 * 阿里云 OSS 配置项分组
 */
val aliOSSGroup = SystemGroup("阿里云 OSS", 100)

/**
 * 阿里云 OSS 配置项标签，可通过 `SystemConfigService.getConfigByTag(ALI_OSS_TAG)` 过滤
 */
const val ALI_OSS_TAG = "ali-oss"

/**
 * 将阿里云 OSS 配置注册为系统配置项默认值
 */
class AliOSSConfigLoader : SystemConfigLoader {

    override fun load(): List<SystemItemInfo> = AliOSSConfigItem.entries.toSystemConfigItems(aliOSSGroup, ALI_OSS_TAG)
}
