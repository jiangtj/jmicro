package com.jiangtj.micro.business.pic.upload

import com.fasterxml.jackson.annotation.JsonInclude
import com.jiangtj.micro.business.config.jackson.DataSizeDeserializer
import com.jiangtj.micro.business.config.jackson.DataSizeSerializer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.util.unit.DataSize
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

/**
 * 图片上传配置属性
 *
 * 只保留功能开关，上传服务商、允许的文件类型、文件大小与目录映射等统一由系统配置项提供，
 * 见 [PicUploadConfigLoader]。
 */
@ConfigurationProperties(prefix = "jmicro.pic.upload")
class PicUploadProperties {

    /**
     * 是否启用图片上传
     */
    var enable: Boolean = true

    /**
     * 上传目录定义，作为系统配置项 `jmicro.pic.upload.dirs`（JSON）的值类型
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    class Dir {

        var path: String = ""

        var provider: String? = null

        @get:JsonSerialize(using = DataSizeSerializer::class)
        @set:JsonDeserialize(using = DataSizeDeserializer::class)
        var maxFileSize: DataSize? = null

        var allowedExtensions: Array<String>? = null

        fun resolve(fileName: String): String =
            if (path.endsWith("/")) {
                path + fileName
            } else {
                "$path/$fileName"
            }
    }
}
