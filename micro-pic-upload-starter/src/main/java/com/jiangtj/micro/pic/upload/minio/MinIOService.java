package com.jiangtj.micro.pic.upload.minio;

import com.jiangtj.micro.pic.upload.*;
import com.jiangtj.micro.pic.upload.ex.PicUploadInternalException;
import io.minio.*;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@PicUploadType("minio")
public class MinIOService implements PicUploadProvider {
    private final MinioClient minioClient;
    private final MinIOProperties properties;

    public MinIOService(MinioClient minioClient, MinIOProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @Override
    public PicUploadResult upload(PicUploadProperties.Dir dir, MultipartFile file) {
        String bucket = properties.getBucket();
        try (InputStream inputStream = file.getInputStream()) {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                log.info("Bucket '{}' not exists, create it.", bucket);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                if (properties.getIsAllowGetObject()) {
                    String policy = """
                        {"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":"*","Action":"s3:GetObject","Resource":"arn:aws:s3:::%s/*"}]}
                        """.formatted(bucket);
                    minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
                }
            } else {
                log.debug("Bucket '{}' already exists.", bucket);
            }

            // 上传对象
            String name = PicUploadUtils.generateNewFileName(file);
            String path = dir.resolve(name);
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            // 生成 url
            String fileUrl = URI.create(properties.getEndpoint())
                .resolve(bucket + "/" + path)
                .toString();

            return PicUploadResult.builder()
                .fileName(name)
                .fileUrl(fileUrl)
                .build();

        } catch (MinioException e) {
            log.error("Error occurred: {}", String.valueOf(e));
            log.error("HTTP trace: {}", e.httpTrace());
            throw new PicUploadInternalException("MinIO 上传失败！", e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("获取图片失败", e);
            throw new PicUploadInternalException("获取图片失败！", e);
        }
    }
}
