package com.jiangtj.micro.business.pic.upload.hw

import com.jiangtj.micro.business.config.SystemConfigService

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

        fun from(systemConfigService: SystemConfigService): ObsSettings = ObsSettings(
            key = systemConfigService.getValue(ObsConfigItem.KEY.key),
            secret = systemConfigService.getValue(ObsConfigItem.SECRET.key),
            endPoint = systemConfigService.getValue(ObsConfigItem.END_POINT.key),
            bucketName = systemConfigService.getValue(ObsConfigItem.BUCKET_NAME.key),
        )
    }
}
