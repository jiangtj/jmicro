package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.business.config.SystemConfigService
import com.jiangtj.micro.business.pic.upload.ex.PicUploadCheckException
import com.jiangtj.micro.common.utils.FileNameUtils
import com.jiangtj.micro.web.AnnotationUtils
import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile

/**
 * 图片上传服务
 *
 * 通用配置（服务商 / 允许的文件类型 / 最大文件大小）与上传目录映射优先读取系统配置项，
 * 未配置时回退到 `jmicro.pic.upload.*` 的启动绑定值。
 */
class PicUploadService @JvmOverloads constructor(
    private val properties: PicUploadProperties,
    providers: List<PicUploadProvider>,
    private val systemConfigService: SystemConfigService? = null,
) {

    private val providers: Map<String, PicUploadProvider> = providers.associateBy { provider ->
        AnnotationUtils.findAnnotation(provider.javaClass, PicUploadType::class.java)
            .map { it.value }
            .orElse(provider.javaClass.name)
    }

    /**
     * 上传图片
     *
     * @param target 上传目标
     * @param file 上传的文件
     * @return 上传结果，包含文件路径等信息
     */
    fun upload(target: String, file: MultipartFile): PicUploadResult {
        val dir = getDir(target)

        // 检查文件是否为空
        if (file.isEmpty) {
            throw PicUploadCheckException("上传的文件不能为空")
        }

        // 检查文件大小
        dir.maxFileSize?.let {
            if (file.size > it.toBytes()) {
                throw PicUploadCheckException("文件大小超过限制")
            }
        }

        // 获取文件扩展名
        val originalFilename = file.originalFilename
        val extension = FileNameUtils.getFileExtension(originalFilename)

        // 检查文件类型
        if (!isAllowedExtension(dir, extension)) {
            throw PicUploadCheckException("不支持的文件类型")
        }

        val provider = dir.provider?.let { providers[it] }
            ?: throw PicUploadCheckException("不存在的图片上传服务")

        val result = provider.upload(dir, file)
        result.originalFileName = originalFilename ?: "未知文件"
        log.info("图片上传成功: {}", result)
        return result
    }

    /**
     * 检查文件类型是否允许
     */
    private fun isAllowedExtension(dir: PicUploadProperties.Dir, extension: String): Boolean {
        if (extension.isBlank()) {
            return false
        }
        val allowed = dir.allowedExtensions ?: return false
        return allowed.any { it.equals(extension, ignoreCase = true) }
    }

    fun getDir(target: String): PicUploadProperties.Dir {
        if (!StringUtils.hasText(target)) {
            throw PicUploadCheckException("请提供上传目标")
        }
        val dir = resolveDirs()[target]
            ?: throw PicUploadCheckException("不存在上传的目标定义，请在配置中添加: jmicro.pic.upload.dirs.$target")
        applyDefaults(dir)
        return dir
    }

    /**
     * 目录映射来源：系统配置优先，未配置时回退到启动绑定值。
     */
    private fun resolveDirs(): Map<String, PicUploadProperties.Dir> {
        systemConfigService?.let { service ->
            val configured = service.getMap(PicUploadConfigLoader.KEY, PicUploadProperties.Dir::class.java)
            if (configured.isNotEmpty()) {
                return configured
            }
        }
        return properties.dirs
    }

    /**
     * 通用配置来源：系统配置优先，未配置（或未开启）时回退到启动绑定值。
     */
    private fun resolveSettings(): PicUploadSettings =
        systemConfigService?.let { PicUploadSettings.from(it) } ?: PicUploadSettings.from(properties)

    private fun applyDefaults(dir: PicUploadProperties.Dir) {
        val settings = resolveSettings()
        if (!StringUtils.hasText(dir.provider)) {
            dir.provider = settings.provider
        }
        if (dir.maxFileSize == null) {
            dir.maxFileSize = settings.maxFileSize
        }
        if (dir.allowedExtensions.isNullOrEmpty()) {
            dir.allowedExtensions = settings.allowedExtensions.toTypedArray()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(PicUploadService::class.java)
    }
}
