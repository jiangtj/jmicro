package com.jiangtj.micro.business.pic.upload.easyimages

/**
 * EasyImages API 响应
 */
class EasyImagesResponse {

    /**
     * 返回状态
     */
    var result: String? = null

    /**
     * 返回状态编号
     */
    var code: Int? = null

    /**
     * 文件链接
     */
    var url: String? = null

    /**
     * 原始名称
     */
    var srcName: String? = null

    /**
     * 缩略图
     */
    var thumb: String? = null

    /**
     * 文件删除链接
     */
    var del: String? = null
}
