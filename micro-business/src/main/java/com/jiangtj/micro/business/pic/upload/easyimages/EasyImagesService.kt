package com.jiangtj.micro.business.pic.upload.easyimages

import com.jiangtj.micro.business.config.SystemConfigService
import com.jiangtj.micro.business.pic.upload.PicUploadProperties
import com.jiangtj.micro.business.pic.upload.PicUploadProvider
import com.jiangtj.micro.business.pic.upload.PicUploadResult
import com.jiangtj.micro.business.pic.upload.PicUploadType
import com.jiangtj.micro.business.pic.upload.ex.PicUploadInternalException
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.multipart.MultipartFile

@PicUploadType("easyimages")
class EasyImagesService(
    private val systemConfigService: SystemConfigService,
) : PicUploadProvider {

    fun uploadImage(file: MultipartFile): EasyImagesResponse {
        val settings = EasyImagesSettings.from(systemConfigService)
        val parts = LinkedMultiValueMap<String, Any>()
        parts.add("token", settings.token)
        parts.add("image", file.resource)

        try {
            val body = RestClient.builder()
                .baseUrl(settings.url)
                .build()
                .post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(parts)
                .retrieve()
                .body(EasyImagesResponse::class.java)
            if (body != null && body.code != 200) {
                throw PicUploadInternalException("EasyImages 错误码" + body.code, null)
            }
            return body ?: throw PicUploadInternalException("EasyImages 返回内容为空", null)
        } catch (e: Exception) {
            log.error("图片上传失败", e)
            throw PicUploadInternalException("EasyImages 异常", e)
        }
    }

    override fun upload(dir: PicUploadProperties.Dir, file: MultipartFile): PicUploadResult {
        val response = uploadImage(file)
        val url = response.url ?: throw PicUploadInternalException("EasyImages 未返回文件链接", null)
        val split = url.split("/")
        return PicUploadResult.builder()
            .fileName(split[split.size - 1])
            .fileUrl(url)
            .apply { response.thumb?.let { thumbnailUrl(it) } }
            .build()
    }

    companion object {
        private val log = LoggerFactory.getLogger(EasyImagesService::class.java)
    }
}
