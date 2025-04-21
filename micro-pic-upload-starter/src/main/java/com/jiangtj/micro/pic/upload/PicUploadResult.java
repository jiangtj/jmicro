package com.jiangtj.micro.pic.upload;

import lombok.Data;

/**
 * 图片上传结果
 */
@Data
public class PicUploadResult {

    /**
     * 原始文件名
     */
    private String originalFileName;

    /**
     * 新文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 缩略图路径（如果生成）
     */
    private String thumbnailPath;
}