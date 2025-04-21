package com.jiangtj.micro.pic.upload;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 图片上传配置属性
 */
@Data
@ConfigurationProperties(prefix = "micro.pic.upload")
public class PicUploadProperties {

    private String provider = "local";

    private Map<String, Dir> dirs;

    @Data
    public static class Dir {
        private String path;
        private long maxFileSize = 10 * 1024 * 1024; // 默认10MB
        private String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "webp"};

        public String resolve(String fileName) {
            if (path.endsWith("/")) {
                return path + fileName;
            }
            return path + "/" + fileName;
        }
    }
}