package com.jiangtj.micro.pic.upload.local;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 图片上传配置属性
 */
@Data
@ConfigurationProperties(prefix = "micro.pic.upload.local")
public class LocalPicUploadProperties {

    /**
     * 上传路径，默认为系统临时目录
     */
    private String uploadPath = System.getProperty("java.io.tmpdir");

    /**
     * 上传路径，默认为系统临时目录
     */
    private String url = "http://localhost:8080";
}