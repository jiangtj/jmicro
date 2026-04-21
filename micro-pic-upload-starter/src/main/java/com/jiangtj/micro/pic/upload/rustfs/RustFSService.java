package com.jiangtj.micro.pic.upload.rustfs;

import com.jiangtj.micro.pic.upload.*;
import com.jiangtj.micro.pic.upload.ex.PicUploadInternalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Slf4j
@PicUploadType("rustfs")
public class RustFSService implements PicUploadProvider {
    private final S3Client rustFSClient;
    private final RustFSProperties properties;

    public RustFSService(S3Client rustFSClient, RustFSProperties properties) {
        this.rustFSClient = rustFSClient;
        this.properties = properties;
    }

    @Override
    public PicUploadResult upload(PicUploadProperties.Dir dir, MultipartFile file) {
        String bucket = properties.getBucket();
        try (InputStream inputStream = file.getInputStream()) {

            try {
                rustFSClient.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
                System.out.println("Bucket created: " + bucket);
            } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException e) {
                System.out.println("Bucket already exists.");
            }

            // 上传对象
            String name = PicUploadUtils.generateNewFileName(file);
            String path = dir.resolve(name);
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            PutObjectRequest.Builder builder = PutObjectRequest.builder()
                .bucket(bucket)
                .key(path);

            // 设置允许策略
            if (properties.getIsAllowGetObject()) {
                builder.acl(ObjectCannedACL.PUBLIC_READ);
            }

            rustFSClient.putObject(
                builder.build(),
                RequestBody.fromInputStream(inputStream, file.getSize())
            );

            // 生成 url
            String fileUrl = URI.create(properties.getEndpoint())
                .resolve(bucket + "/" + path)
                .toString();

            return PicUploadResult.builder()
                .fileName(name)
                .fileUrl(fileUrl)
                .build();

        } catch (S3Exception | IOException e) {
            log.error("Error occurred: {}", String.valueOf(e));
            throw new PicUploadInternalException("RustFS 上传失败！", e);
        }
    }
}
