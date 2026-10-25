package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.business.config.JMicroConfigService
import org.springframework.util.StringUtils
import org.springframework.util.unit.DataSize

/**
 * 图片上传通用运行时配置，由系统配置项 [PicUploadBaseConfigItem] 解析而来
 */
data class PicUploadSettings(
    /**
     * 默认的文件上传服务商
     */
    val provider: String,
    /**
     * 默认允许的文件类型
     */
    val allowedExtensions: List<String>,
    /**
     * 默认最大文件大小
     */
    val maxFileSize: DataSize,
) {

    companion object {

        /**
         * 从系统配置读取，未配置时回退到配置项默认值
         */
        fun from(jmicroConfigService: JMicroConfigService): PicUploadSettings = PicUploadSettings(
            provider = jmicroConfigService.getValue(PicUploadBaseConfigItem.PROVIDER.key)
                .ifBlank { PicUploadBaseConfigItem.PROVIDER.defaultValue },
            allowedExtensions = parseExtensions(
                jmicroConfigService.getValue(PicUploadBaseConfigItem.ALLOWED_EXTENSIONS.key)
            ),
            maxFileSize = parseMaxFileSize(
                jmicroConfigService.getValue(PicUploadBaseConfigItem.MAX_FILE_SIZE.key)
            ),
        )

        private fun parseExtensions(value: String): List<String> {
            val extensions = value.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            return extensions.ifEmpty { PicUploadBaseConfigItem.ALLOWED_EXTENSIONS.defaultValue.split(",") }
        }

        private fun parseMaxFileSize(value: String): DataSize =
            if (StringUtils.hasText(value)) {
                runCatching { DataSize.parse(value) }
                    .getOrElse { DataSize.parse(PicUploadBaseConfigItem.MAX_FILE_SIZE.defaultValue) }
            } else {
                DataSize.parse(PicUploadBaseConfigItem.MAX_FILE_SIZE.defaultValue)
            }
    }
}
