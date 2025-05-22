package com.jiangtj.micro.pic.upload.minio;

import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(MinioClient.class)
@EnableConfigurationProperties(MinIOProperties.class)
public class MinIOAutoConfiguration {

    /**
     * 注册 MinioClient 服务
     */
    @Bean
    public MinioClient minioClient(MinIOProperties properties) {
        return MinioClient.builder()
            .endpoint(properties.getEndpoint())
            .credentials(properties.getAccessKey(), properties.getSecretKey())
            .build();
    }

    /**
     * 注册 MinIOService 服务
     */
    @Bean
    public MinIOService minioService(MinioClient minioClient, MinIOProperties properties) {
        return new MinIOService(minioClient, properties);
    }

}