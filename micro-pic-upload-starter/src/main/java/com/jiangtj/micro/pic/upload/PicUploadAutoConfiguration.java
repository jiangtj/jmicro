package com.jiangtj.micro.pic.upload;

import com.jiangtj.micro.pic.upload.local.LocalPicUploadProperties;
import com.jiangtj.micro.pic.upload.local.LocalPicUploadService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 图片上传自动配置类
 */
@AutoConfiguration
@EnableConfigurationProperties({
    PicUploadProperties.class,
    LocalPicUploadProperties.class
})
public class PicUploadAutoConfiguration {

    /**
     * 注册图片上传服务
     */
    @Bean
    public PicUploadService picUploadService(PicUploadProperties properties, List<PicUploadProvider> providers) {
        return new PicUploadService(properties, providers);
    }

    /**
     * 注册本地图片上传供应商
     */
    @Bean
    public LocalPicUploadService localPicUploadService(LocalPicUploadProperties properties) {
        return new LocalPicUploadService(properties);
    }
}