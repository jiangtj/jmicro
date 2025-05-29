package com.jiangtj.micro.pic.upload;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

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
    private String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "webp"};

    /**
     * 默认最大文件大小 10mb
     */
    private DataSize maxFileSize = DataSize.ofMegabytes(10);

    private Map<String, Dir> dirs = new HashMap<>();

    @Data
    public static class Dir {
        private String path;
        private String provider;
        private DataSize maxFileSize;
        private String[] allowedExtensions;

        public String resolve(String fileName) {
            if (path.endsWith("/")) {
                return path + fileName;
            }
            return path + "/" + fileName;
        }
    }
}