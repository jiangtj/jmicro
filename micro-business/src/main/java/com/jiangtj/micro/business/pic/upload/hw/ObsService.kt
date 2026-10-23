package com.jiangtj.micro.business.pic.upload.hw

import com.jiangtj.micro.business.config.SystemConfigService
import com.jiangtj.micro.business.pic.upload.PicUploadProperties
import com.jiangtj.micro.business.pic.upload.PicUploadProvider
import com.jiangtj.micro.business.pic.upload.PicUploadResult
import com.jiangtj.micro.business.pic.upload.PicUploadType
import com.jiangtj.micro.business.pic.upload.PicUploadUtils
import com.jiangtj.micro.business.pic.upload.ex.PicUploadInternalException
import com.obs.services.ObsClient
import com.obs.services.exception.ObsException
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.io.InputStream

@PicUploadType("hw")
class ObsService(
    private val systemConfigService: SystemConfigService,
) : PicUploadProvider {

    override fun upload(dir: PicUploadProperties.Dir, file: MultipartFile): PicUploadResult {
        val name = PicUploadUtils.generateNewFileName(file)
        val url = try {
            file.inputStream.use { input ->
                putObject(ObsSettings.from(systemConfigService), dir.resolve(name), input)
            }
        } catch (e: IOException) {
            log.error("获取图片失败", e)
            throw PicUploadInternalException("获取图片失败！", e)
        }
        return PicUploadResult.builder()
            .fileName(name)
            .fileUrl(url)
            .build()
    }

    fun putObject(settings: ObsSettings, key: String, input: InputStream): String {
        // 使用永久 AK/SK 初始化客户端
        try {
            ObsClient(settings.key, settings.secret, settings.endPoint).use { obsClient ->
                val result = obsClient.putObject(settings.bucketName, key, input)
                return result.objectUrl
            }
        } catch (e: ObsException) {
            log.error("putObject failed", e)
            log.error("HTTP Code:{}", e.responseCode)
            log.error("Error Code:{}", e.errorCode)
            log.error("Error Message:{}", e.errorMessage)
            log.error("Request ID:{}", e.errorRequestId)
            log.error("Host ID:{}", e.errorHostId)
            throw PicUploadInternalException("华为云上传错误!", e)
        } catch (e: Exception) {
            throw PicUploadInternalException("华为云客户端异常!", e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ObsService::class.java)
    }
}
