package com.jiangtj.micro.pic.upload.ali;

import com.aliyun.oss.OSS;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(OSS.class)
@EnableConfigurationProperties(AliOSSProperties.class)
public class AliOSSAutoConfiguration {

    /**
     * 注册阿里oss服务
     */
    @Bean
    public AliOSSService aliOSSService(AliOSSProperties properties) {
        return new AliOSSService(properties);
    }

}