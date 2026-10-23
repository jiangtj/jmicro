package com.jiangtj.micro.business.pic.upload.easyimages

import com.jiangtj.micro.business.config.SystemConfigService

/**
 * EasyImages 运行时配置，由系统配置项 [EasyImagesConfigItem] 解析而来
 */
data class EasyImagesSettings(
    /**
     * API 服务器地址
     */
    val url: String,
    /**
     * API Token
     */
    val token: String,
) {

    companion object {

        fun from(systemConfigService: SystemConfigService): EasyImagesSettings = EasyImagesSettings(
            url = systemConfigService.getValue(EasyImagesConfigItem.URL.key),
            token = systemConfigService.getValue(EasyImagesConfigItem.TOKEN.key),
        )
    }
}
