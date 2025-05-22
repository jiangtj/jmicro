package com.jiangtj.micro.pic.upload.minio;

import com.jiangtj.micro.pic.upload.*;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
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
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            } else {
                log.error("Bucket '{}' already exists.", bucket);
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
            throw new PicUploadException("文件上传错误!");
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("上传文件失败", e);
            throw new PicUploadException("文件上传错误!");
        }
    }
}
