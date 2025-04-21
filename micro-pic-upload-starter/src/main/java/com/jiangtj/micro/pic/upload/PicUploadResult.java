package com.jiangtj.micro.pic.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片上传结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
     * 文件Url
     */
    private String fileUrl;

    /**
     * 缩略图Url（如果生成）
     */
    private String thumbnailUrl;
}