package com.jiangtj.micro.business.pic.upload.rustfs

import com.jiangtj.micro.business.config.JMicroConfigService

/**
 * RustFS 运行时配置，由系统配置项 [RustFSConfigItem] 解析而来
 */
data class RustFSSettings(
    val enable: Boolean,
    val endpoint: String,
    val accessKey: String,
    val secretKey: String,
    val bucket: String,
    val isAllowGetObject: Boolean,
) {

    companion object {

        fun from(jmicroConfigService: JMicroConfigService): RustFSSettings = RustFSSettings(
            enable = jmicroConfigService.isTrue(RustFSConfigItem.ENABLE.key),
            endpoint = jmicroConfigService.getValue(RustFSConfigItem.ENDPOINT.key),
            accessKey = jmicroConfigService.getValue(RustFSConfigItem.ACCESS_KEY.key),
            secretKey = jmicroConfigService.getValue(RustFSConfigItem.SECRET_KEY.key),
            bucket = jmicroConfigService.getValue(RustFSConfigItem.BUCKET.key),
            isAllowGetObject = jmicroConfigService.isTrue(RustFSConfigItem.IS_ALLOW_GET_OBJECT.key),
        )
    }
}
