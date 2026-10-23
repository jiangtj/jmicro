package com.jiangtj.micro.business.pic.upload.minio

import com.jiangtj.micro.business.config.SystemConfigService

/**
 * MinIO 运行时配置，由系统配置项 [MinIOConfigItem] 解析而来
 */
data class MinIOSettings(
    val endpoint: String,
    val accessKey: String,
    val secretKey: String,
    val bucket: String,
    val isAllowGetObject: Boolean,
) {

    companion object {

        fun from(systemConfigService: SystemConfigService): MinIOSettings = MinIOSettings(
            endpoint = systemConfigService.getValue(MinIOConfigItem.ENDPOINT.key),
            accessKey = systemConfigService.getValue(MinIOConfigItem.ACCESS_KEY.key),
            secretKey = systemConfigService.getValue(MinIOConfigItem.SECRET_KEY.key),
            bucket = systemConfigService.getValue(MinIOConfigItem.BUCKET.key),
            isAllowGetObject = systemConfigService.isTrue(MinIOConfigItem.IS_ALLOW_GET_OBJECT.key),
        )
    }
}
