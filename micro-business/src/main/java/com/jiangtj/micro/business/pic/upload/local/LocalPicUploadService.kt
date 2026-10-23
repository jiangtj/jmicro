package com.jiangtj.micro.business.pic.upload.local

import com.jiangtj.micro.business.config.SystemConfigService
import com.jiangtj.micro.business.pic.upload.PicUploadProperties
import com.jiangtj.micro.business.pic.upload.PicUploadProvider
import com.jiangtj.micro.business.pic.upload.PicUploadResult
import com.jiangtj.micro.business.pic.upload.PicUploadType
import com.jiangtj.micro.business.pic.upload.ex.PicUploadInternalException
import com.jiangtj.micro.common.utils.FileNameUtils
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

/**
 * 本地图片上传服务
 */
@PicUploadType("local")
class LocalPicUploadService(
    private val systemConfigService: SystemConfigService,
) : PicUploadProvider {

    override fun upload(dir: PicUploadProperties.Dir, file: MultipartFile): PicUploadResult {
        try {
            val settings = LocalPicUploadSettings.from(systemConfigService)

            // 创建上传目录
            val uploadDir = Paths.get(settings.uploadPath).resolve(dir.path)
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir)
            }

            // 生成随机文件名
            val extension = FileNameUtils.getFileExtension(file.originalFilename)
            val newFileName = FileNameUtils.getRandomFileNameWithSuffix(extension)
            val targetPath = uploadDir.resolve(newFileName)

            // 保存文件
            file.transferTo(targetPath)

            // 构建结果
            val result = PicUploadResult()
            result.fileName = newFileName
            result.fileUrl = URI.create(settings.url).resolve(newFileName).toString()

            log.info("图片上传成功: {}", result)
            return result
        } catch (e: IOException) {
            log.error("获取图片失败", e)
            throw PicUploadInternalException("获取图片失败！", e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(LocalPicUploadService::class.java)
    }
}
