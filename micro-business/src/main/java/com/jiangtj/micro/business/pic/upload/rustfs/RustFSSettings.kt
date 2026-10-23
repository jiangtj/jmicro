package com.jiangtj.micro.business.pic.upload.rustfs

import com.jiangtj.micro.business.config.SystemConfigService

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

        fun from(systemConfigService: SystemConfigService): RustFSSettings = RustFSSettings(
            enable = systemConfigService.isTrue(RustFSConfigItem.ENABLE.key),
            endpoint = systemConfigService.getValue(RustFSConfigItem.ENDPOINT.key),
            accessKey = systemConfigService.getValue(RustFSConfigItem.ACCESS_KEY.key),
            secretKey = systemConfigService.getValue(RustFSConfigItem.SECRET_KEY.key),
            bucket = systemConfigService.getValue(RustFSConfigItem.BUCKET.key),
            isAllowGetObject = systemConfigService.isTrue(RustFSConfigItem.IS_ALLOW_GET_OBJECT.key),
        )
    }
}
