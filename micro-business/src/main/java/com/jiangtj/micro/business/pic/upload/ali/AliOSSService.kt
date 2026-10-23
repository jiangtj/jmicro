package com.jiangtj.micro.business.pic.upload.ali

import com.aliyun.oss.ClientBuilderConfiguration
import com.aliyun.oss.ClientException
import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.OSSException
import com.aliyun.oss.common.auth.CredentialsProvider
import com.aliyun.oss.common.auth.CredentialsProviderFactory
import com.aliyun.oss.common.comm.SignVersion
import com.aliyun.oss.model.PutObjectRequest
import com.aliyuncs.exceptions.ClientException as CredentialsClientException
import com.jiangtj.micro.business.config.SystemConfigService
import com.jiangtj.micro.business.pic.upload.PicUploadProperties
import com.jiangtj.micro.business.pic.upload.PicUploadProvider
import com.jiangtj.micro.business.pic.upload.PicUploadResult
import com.jiangtj.micro.business.pic.upload.PicUploadType
import com.jiangtj.micro.business.pic.upload.PicUploadUtils
import com.jiangtj.micro.business.pic.upload.ex.PicUploadInternalException
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.io.InputStream

@PicUploadType("ali")
class AliOSSService(
    private val systemConfigService: SystemConfigService,
) : PicUploadProvider {

    override fun upload(dir: PicUploadProperties.Dir, file: MultipartFile): PicUploadResult {
        val name = PicUploadUtils.generateNewFileName(file)
        val url = try {
            file.inputStream.use { input ->
                upload(AliOSSSettings.from(systemConfigService), dir.resolve(name), input)
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

    fun upload(settings: AliOSSSettings, key: String, input: InputStream): String {
        val ossClient = buildClient(settings)
        try {
            val putObjectRequest = PutObjectRequest(settings.bucketName, key, input)
            val result = ossClient.putObject(putObjectRequest)
            log.debug("阿里云 OSS 上传成功: {}", result.requestId)
            return settings.url + "/" + key
        } catch (oe: OSSException) {
            log.error(
                "Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason."
            )
            log.error("Error Message:{}", oe.errorMessage)
            log.error("Error Code:{}", oe.errorCode)
            log.error("Request ID:{}", oe.requestId)
            log.error("Host ID:{}", oe.hostId)
            throw PicUploadInternalException("AliOSS Client call fail!", oe)
        } catch (ce: ClientException) {
            log.error(
                "Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network."
            )
            log.error("Error Message:{}", ce.message)
            throw PicUploadInternalException("AliOSS Client error!", ce)
        } finally {
            ossClient.shutdown()
        }
    }

    private fun buildClient(settings: AliOSSSettings): OSS {
        val credentialsProvider: CredentialsProvider = try {
            if (settings.accessKeyId != null && settings.secretAccessKey != null) {
                CredentialsProviderFactory.newDefaultCredentialProvider(
                    settings.accessKeyId,
                    settings.secretAccessKey,
                )
            } else {
                CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider()
            }
        } catch (e: CredentialsClientException) {
            log.error("初始化阿里云OSS失败 CredentialsProvider 初始化失败", e)
            throw PicUploadInternalException("阿里云OSS CredentialsProvider 初始化失败！", e)
        }

        val clientBuilderConfiguration = ClientBuilderConfiguration().apply {
            setSignatureVersion(SignVersion.V4)
        }

        return OSSClientBuilder.create()
            .endpoint(settings.endpoint)
            .credentialsProvider(credentialsProvider)
            .clientConfiguration(clientBuilderConfiguration)
            .region(settings.region)
            .build()
    }

    companion object {
        private val log = LoggerFactory.getLogger(AliOSSService::class.java)
    }
}
