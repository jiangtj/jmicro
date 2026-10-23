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
 * `provider` / `allowedExtensions` / `maxFileSize` / `dirs` 的值既可通过
 * `jmicro.pic.upload.*` 启动绑定，也会由 [PicUploadConfigLoader] 注册为系统配置项
 * （键名分别为 [PicUploadBaseConfigItem.PROVIDER]、[PicUploadBaseConfigItem.ALLOWED_EXTENSIONS]、
 * [PicUploadBaseConfigItem.MAX_FILE_SIZE] 与 [PicUploadConfigLoader.KEY]），开启系统配置后可在运行期覆盖。
 */
@ConfigurationProperties(prefix = "jmicro.pic.upload")
class PicUploadProperties {

    /**
     * 默认的文件上传提供者
     */
    var provider: String = PicUploadBaseConfigItem.PROVIDER.defaultValue

    /**
     * 默认允许的文件类型
     */
    var allowedExtensions: Array<String> =
        PicUploadBaseConfigItem.ALLOWED_EXTENSIONS.defaultValue.split(",").toTypedArray()

    /**
     * 默认最大文件大小 10mb
     */
    var maxFileSize: DataSize = DataSize.parse(PicUploadBaseConfigItem.MAX_FILE_SIZE.defaultValue)

    var dirs: MutableMap<String, Dir> = HashMap()

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
