package com.jiangtj.micro.business.pic.upload.minio

import com.jiangtj.micro.business.config.SystemConfigService
import com.jiangtj.micro.business.pic.upload.PicUploadProperties
import com.jiangtj.micro.business.pic.upload.PicUploadProvider
import com.jiangtj.micro.business.pic.upload.PicUploadResult
import com.jiangtj.micro.business.pic.upload.PicUploadType
import com.jiangtj.micro.business.pic.upload.PicUploadUtils
import com.jiangtj.micro.business.pic.upload.ex.PicUploadInternalException
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.SetBucketPolicyArgs
import io.minio.errors.MinioException
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URI

@PicUploadType("minio")
class MinIOService(
    private val systemConfigService: SystemConfigService,
) : PicUploadProvider {

    private var isSetBucketPolicy = true

    override fun upload(dir: PicUploadProperties.Dir, file: MultipartFile): PicUploadResult {
        val settings = MinIOSettings.from(systemConfigService)
        try {
            file.inputStream.use { inputStream ->
                val name = PicUploadUtils.generateNewFileName(file)
                var path = dir.resolve(name)
                if (path.startsWith("/")) {
                    path = path.substring(1)
                }

                MinioClient.builder()
                    .endpoint(settings.endpoint)
                    .credentials(settings.accessKey, settings.secretKey)
                    .build()
                    .use { minioClient ->
                        ensureBucket(minioClient, settings)
                        applyAllowGetObjectPolicy(minioClient, settings)

                        // 上传对象
                        minioClient.putObject(
                            PutObjectArgs.builder()
                                .bucket(settings.bucket)
                                .`object`(path)
                                .stream(inputStream, file.size, -1L)
                                .contentType(file.contentType)
                                .build()
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
        } catch (e: MinioException) {
            log.error("Error occurred: {}", e.toString())
            log.error("HTTP trace: {}", e.httpTrace())
            throw PicUploadInternalException("MinIO 上传失败！", e)
        } catch (e: IOException) {
            log.error("获取图片失败", e)
            throw PicUploadInternalException("获取图片失败！", e)
        }
    }

    private fun ensureBucket(minioClient: MinioClient, settings: MinIOSettings) {
        val found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(settings.bucket).build())
        if (!found) {
            log.info("Bucket '{}' not exists, create it.", settings.bucket)
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(settings.bucket).build())
        } else {
            log.debug("Bucket '{}' already exists.", settings.bucket)
        }
    }

    private fun applyAllowGetObjectPolicy(minioClient: MinioClient, settings: MinIOSettings) {
        if (!settings.isAllowGetObject || !isSetBucketPolicy) {
            return
        }
        val policy = """
            {"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":"*","Action":"s3:GetObject","Resource":"arn:aws:s3:::%s/*"}]}
        """.trimIndent().format(settings.bucket)
        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(settings.bucket).config(policy).build())
        isSetBucketPolicy = false
    }

    companion object {
        private val log = LoggerFactory.getLogger(MinIOService::class.java)
    }
}
