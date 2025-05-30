package com.jiangtj.micro.pic.upload;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 图片上传配置属性
 */
@Data
@ConfigurationProperties(prefix = "micro.pic.upload")
public class PicUploadProperties {

    /**
     * 默认的文件上传提供者
     */
    private String provider = "local";

    /**
     * 默认允许的文件类型
     */
    private String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "webp", "bmp", "svg", "avif"};

    /**
     * 默认最大文件大小（字节）
     */
    private long maxFileSize = 10 * 1024 * 1024; // 默认10MB

    private Map<String, Dir> dirs = new HashMap<>();

    @Data
    public static class Dir {
        private String path;
        private String provider;
        private Long maxFileSize;
        private String[] allowedExtensions;

        public String resolve(String fileName) {
            if (path.endsWith("/")) {
                return path + fileName;
            }
            return path + "/" + fileName;
        }
    }
}