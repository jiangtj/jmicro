package com.jiangtj.micro.business.pic.upload.minio

import com.jiangtj.micro.business.config.JMicroConfigService

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

        fun from(jmicroConfigService: JMicroConfigService): MinIOSettings = MinIOSettings(
            endpoint = jmicroConfigService.getValue(MinIOConfigItem.ENDPOINT.key),
            accessKey = jmicroConfigService.getValue(MinIOConfigItem.ACCESS_KEY.key),
            secretKey = jmicroConfigService.getValue(MinIOConfigItem.SECRET_KEY.key),
            bucket = jmicroConfigService.getValue(MinIOConfigItem.BUCKET.key),
            isAllowGetObject = jmicroConfigService.isTrue(MinIOConfigItem.IS_ALLOW_GET_OBJECT.key),
        )
    }
}
