package com.jiangtj.micro.business.pic.upload;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 图片上传自动配置类
 *
 * 各上传服务商（local / ali / hw / minio / easyimages / rustfs）的配置由系统配置提供，
 * 其自动配置类在开启 `jmicro.system.config.enabled=true` 后生效。
 */
@AutoConfiguration
@EnableConfigurationProperties({
    PicUploadProperties.class
})
public class PicUploadAutoConfiguration {

    /**
     * 注册图片上传服务
     */
    @Bean
    public PicUploadService picUploadService(PicUploadProperties properties, List<PicUploadProvider> providers) {
        return new PicUploadService(properties, providers);
    }
}
