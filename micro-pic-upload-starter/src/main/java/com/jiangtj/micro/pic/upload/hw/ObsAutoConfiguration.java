package com.jiangtj.micro.pic.upload.hw;

import com.obs.services.ObsClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(ObsClient.class)
@EnableConfigurationProperties(ObsProperties.class)
public class ObsAutoConfiguration {

    /**
     * 注册 hwcloud obs服务
     */
    @Bean
    public ObsService obsService(ObsProperties properties) {
        return new ObsService(properties);
    }

}