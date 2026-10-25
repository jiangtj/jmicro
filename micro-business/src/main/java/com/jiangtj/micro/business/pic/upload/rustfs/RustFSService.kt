package com.jiangtj.micro.business.pic.upload.rustfs

import com.jiangtj.micro.business.config.JMicroConfigService
import com.jiangtj.micro.business.pic.upload.PicUploadProperties
import com.jiangtj.micro.business.pic.upload.PicUploadProvider
import com.jiangtj.micro.business.pic.upload.PicUploadResult
import com.jiangtj.micro.business.pic.upload.PicUploadType
import com.jiangtj.micro.business.pic.upload.PicUploadUtils
import com.jiangtj.micro.business.pic.upload.ex.PicUploadInternalException
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import java.io.IOException
import java.net.URI

@PicUploadType("rustfs")
class RustFSService(
    private val jmicroConfigService: JMicroConfigService,
) : PicUploadProvider {

    override fun upload(dir: PicUploadProperties.Dir, file: MultipartFile): PicUploadResult {
        val settings = RustFSSettings.from(jmicroConfigService)
        if (!settings.enable) {
            throw PicUploadInternalException(
                "RustFS 未启用，请在系统配置中开启 " + RustFSConfigItem.ENABLE.key,
                null,
            )
        }

        try {
            file.inputStream.use { inputStream ->
                // 上传对象
                val name = PicUploadUtils.generateNewFileName(file)
                var path = dir.resolve(name)
                if (path.startsWith("/")) {
                    path = path.substring(1)
                }

                buildClient(settings).use { rustFSClient ->
                    ensureBucket(rustFSClient, settings)

                    val builder = PutObjectRequest.builder()
                        .bucket(settings.bucket)
                        .key(path)

                    // 设置允许策略
                    if (settings.isAllowGetObject) {
                        builder.acl(ObjectCannedACL.PUBLIC_READ)
                    }

                    rustFSClient.putObject(
                        builder.build(),
                        RequestBody.fromInputStream(inputStream, file.size),
                    )
                }

                // 生成 url
                val fileUrl = URI.create(settings.endpoint)
                    .resolve(settings.bucket + "/" + path)
                    .toString()

                return PicUploadResult.builder()
                    .fileName(name)
                    .fileUrl(fileUrl)
                    .build()
            }
        } catch (e: S3Exception) {
            log.error("Error occurred: {}", e.toString())
            throw PicUploadInternalException("RustFS 上传失败！", e)
        } catch (e: IOException) {
            log.error("Error occurred: {}", e.toString())
            throw PicUploadInternalException("RustFS 上传失败！", e)
        }
    }

    private fun buildClient(settings: RustFSSettings): S3Client {
        return S3Client.builder()
            .endpointOverride(URI.create(settings.endpoint)) // RustFS 地址
            .region(Region.US_EAST_1) // 可写死，RustFS 不校验 region
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(settings.accessKey, settings.secretKey)
                )
            )
            .forcePathStyle(true) // 关键配置！RustFS 需启用 Path-Style
            .build()
    }

    private fun ensureBucket(rustFSClient: S3Client, settings: RustFSSettings) {
        try {
            rustFSClient.createBucket(CreateBucketRequest.builder().bucket(settings.bucket).build())
            log.info("Bucket created: {}", settings.bucket)
        } catch (e: BucketAlreadyExistsException) {
            log.info("Bucket already exists.")
        } catch (e: BucketAlreadyOwnedByYouException) {
            log.info("Bucket already exists.")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(RustFSService::class.java)
    }
}
