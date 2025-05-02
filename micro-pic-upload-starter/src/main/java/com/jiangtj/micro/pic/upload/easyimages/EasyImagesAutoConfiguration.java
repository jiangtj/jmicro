package com.jiangtj.micro.pic.upload.easyimages;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "easyimages.api", value = "url")
@EnableConfigurationProperties(EasyImagesProperties.class)
public class EasyImagesAutoConfiguration {

    /**
     * 注册 EasyImages 服务
     */
    @Bean
    public EasyImagesService easyImagesService(EasyImagesProperties properties) {
        return new EasyImagesService(properties);
    }

}