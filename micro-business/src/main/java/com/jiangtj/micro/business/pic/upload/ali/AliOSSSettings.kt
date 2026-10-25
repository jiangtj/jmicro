package com.jiangtj.micro.business.pic.upload.ali

import com.jiangtj.micro.business.config.JMicroConfigService

/**
 * 阿里云 OSS 运行时配置，由系统配置项 [AliOSSConfigItem] 解析而来
 */
data class AliOSSSettings(
    val endpoint: String,
    val region: String,
    val bucketName: String,
    val url: String,
    val accessKeyId: String?,
    val secretAccessKey: String?,
) {

    companion object {

        /**
         * 从系统配置中读取阿里云 OSS 配置，密钥为空时返回 null（由 SDK 从环境变量读取）
         */
        fun from(jmicroConfigService: JMicroConfigService): AliOSSSettings = AliOSSSettings(
            endpoint = jmicroConfigService.getValue(AliOSSConfigItem.ENDPOINT.key),
            region = jmicroConfigService.getValue(AliOSSConfigItem.REGION.key),
            bucketName = jmicroConfigService.getValue(AliOSSConfigItem.BUCKET_NAME.key),
            url = jmicroConfigService.getValue(AliOSSConfigItem.URL.key),
            accessKeyId = jmicroConfigService.getValue(AliOSSConfigItem.ACCESS_KEY_ID.key).ifBlank { null },
            secretAccessKey = jmicroConfigService.getValue(AliOSSConfigItem.SECRET_ACCESS_KEY.key).ifBlank { null },
        )
    }
}
