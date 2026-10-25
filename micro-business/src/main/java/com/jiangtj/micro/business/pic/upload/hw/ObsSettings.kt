package com.jiangtj.micro.business.pic.upload.hw

import com.jiangtj.micro.business.config.JMicroConfigService

/**
 * 华为云 OBS 运行时配置，由系统配置项 [ObsConfigItem] 解析而来
 */
data class ObsSettings(
    val key: String,
    val secret: String,
    val endPoint: String,
    val bucketName: String,
) {

    companion object {

        fun from(jmicroConfigService: JMicroConfigService): ObsSettings = ObsSettings(
            key = jmicroConfigService.getValue(ObsConfigItem.KEY.key),
            secret = jmicroConfigService.getValue(ObsConfigItem.SECRET.key),
            endPoint = jmicroConfigService.getValue(ObsConfigItem.END_POINT.key),
            bucketName = jmicroConfigService.getValue(ObsConfigItem.BUCKET_NAME.key),
        )
    }
}
