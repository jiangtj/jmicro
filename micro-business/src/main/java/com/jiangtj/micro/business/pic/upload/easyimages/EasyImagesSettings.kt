package com.jiangtj.micro.business.pic.upload.easyimages

import com.jiangtj.micro.business.config.JMicroConfigService

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

        fun from(jmicroConfigService: JMicroConfigService): EasyImagesSettings = EasyImagesSettings(
            url = jmicroConfigService.getValue(EasyImagesConfigItem.URL.key),
            token = jmicroConfigService.getValue(EasyImagesConfigItem.TOKEN.key),
        )
    }
}
