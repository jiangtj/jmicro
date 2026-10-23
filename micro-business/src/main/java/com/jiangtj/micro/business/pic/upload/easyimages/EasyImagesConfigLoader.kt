package com.jiangtj.micro.business.pic.upload.easyimages

import com.jiangtj.micro.business.config.SystemConfigLoader
import com.jiangtj.micro.business.config.SystemGroup
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.pic.upload.PicUploadConfigItem
import com.jiangtj.micro.business.pic.upload.toSystemConfigItems

/**
 * EasyImages 配置项定义
 */
enum class EasyImagesConfigItem(
    override val key: String,
    override val label: String,
    override val defaultValue: String,
    override val secret: Boolean = false,
) : PicUploadConfigItem {
    URL("easyimages.api.url", "API 地址", ""),
    TOKEN("easyimages.api.token", "API Token", "", true),
}

/**
 * EasyImages 配置项分组
 */
val easyImagesGroup = SystemGroup("EasyImages", 110)

/**
 * EasyImages 配置项标签
 */
const val EASY_IMAGES_TAG = "easyimages"

/**
 * 将 EasyImages 配置注册为系统配置项默认值
 */
class EasyImagesConfigLoader : SystemConfigLoader {

    override fun load(): List<SystemItemInfo> =
        EasyImagesConfigItem.entries.toSystemConfigItems(easyImagesGroup, EASY_IMAGES_TAG)
}
