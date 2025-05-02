package com.jiangtj.micro.pic.upload.easyimages;

import lombok.Data;

@Data
public class EasyImagesResponse {
    private String result;      // 返回状态
    private Integer code;       // 返回状态编号
    private String url;         // 文件链接
    private String srcName;     // 原始名称
    private String thumb;       // 缩略图
    private String del;         // 文件删除链接
}