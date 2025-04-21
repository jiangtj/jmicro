package com.jiangtj.micro.pic.upload;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 图片上传自动配置类
 */
@AutoConfiguration
@EnableConfigurationProperties(PicUploadProperties.class)
public class PicUploadAutoConfiguration {

    /**
     * 注册图片上传服务
     */
    @Bean
    @ConditionalOnMissingBean
    public PicUploadService picUploadService(PicUploadProperties properties) {
        return new PicUploadService(properties);
    }
}