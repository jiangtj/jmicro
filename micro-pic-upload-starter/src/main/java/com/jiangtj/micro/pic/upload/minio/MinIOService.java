package com.jiangtj.micro.pic.upload.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinIOService {
    private final MinioClient minioClient;
    private final MinIOProperties properties;

    public MinIOService(MinioClient minioClient, MinIOProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    public void uploadObject() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String bucket = properties.getBucket();
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            } else {
                System.out.println("Bucket '" + bucket + "' already exists.");
            }

            minioClient.uploadObject(
                UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object("asiaphotos-2015.zip")
                    .filename("/home/user/Photos/asiaphotos.zip")
                    .build());
            System.out.println(
                "'/home/user/Photos/asiaphotos.zip' is successfully uploaded as "
                    + "object 'asiaphotos-2015.zip' to bucket 'asiatrip'.");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }
    }
}
