package com.jiangtj.micro.pic.upload.rustfs;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@AutoConfiguration
@ConditionalOnProperty(prefix = "jmicro.rustfs", name = "enable", havingValue = "true")
@EnableConfigurationProperties(RustFSProperties.class)
public class RustFSAutoConfiguration {

    /**
     * 注册 RustFSClient 服务
     */
    @Bean
    public S3Client rustFSClient(RustFSProperties properties) {
        return S3Client.builder()
            .endpointOverride(URI.create(properties.getEndpoint())) // RustFS 地址
            .region(Region.US_EAST_1) // 可写死，RustFS 不校验 region
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())
                )
            )
            .forcePathStyle(true) // 关键配置！RustFS 需启用 Path-Style
            .build();
    }

    /**
     * 注册 MinIOService 服务
     */
    @Bean
    public RustFSService minioService(S3Client rustFSClient, RustFSProperties properties) {
        return new RustFSService(rustFSClient, properties);
    }

}