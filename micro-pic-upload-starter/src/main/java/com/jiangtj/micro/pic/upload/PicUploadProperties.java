package com.jiangtj.micro.pic.upload;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 图片上传配置属性
 */
@Data
@ConfigurationProperties(prefix = "micro.pic.upload")
public class PicUploadProperties {

    /**
     * 上传路径，默认为系统临时目录
     */
    private String uploadPath = System.getProperty("java.io.tmpdir");

    /**
     * 允许的文件类型
     */
    private String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "webp"};

    /**
     * 最大文件大小（字节）
     */
    private long maxFileSize = 10 * 1024 * 1024; // 默认5MB

    /**
     * 是否生成缩略图
     */
    private boolean generateThumbnail = false;

    /**
     * 缩略图宽度
     */
    private int thumbnailWidth = 200;

    /**
     * 缩略图高度
     */
    private int thumbnailHeight = 200;
}