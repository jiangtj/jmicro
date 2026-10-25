package com.jiangtj.micro.business.pic.upload.local

import com.jiangtj.micro.business.config.JMicroConfigService

/**
 * 本地上传运行时配置，由系统配置项 [LocalPicUploadConfigItem] 解析而来
 */
data class LocalPicUploadSettings(
    /**
     * 上传路径，默认为系统临时目录
     */
    val uploadPath: String,
    /**
     * 上传文件的可访问 URL 前缀
     */
    val url: String,
) {

    companion object {

        fun from(jmicroConfigService: JMicroConfigService): LocalPicUploadSettings = LocalPicUploadSettings(
            uploadPath = jmicroConfigService.getValue(LocalPicUploadConfigItem.UPLOAD_PATH.key),
            url = jmicroConfigService.getValue(LocalPicUploadConfigItem.URL.key),
        )
    }
}
