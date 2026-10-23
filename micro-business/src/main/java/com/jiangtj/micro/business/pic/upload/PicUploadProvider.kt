package com.jiangtj.micro.business.pic.upload

import org.springframework.web.multipart.MultipartFile

/**
 * 图片上传服务商接口，实现类通过 [PicUploadType] 声明服务商名称。
 */
interface PicUploadProvider {

    fun upload(dir: PicUploadProperties.Dir, file: MultipartFile): PicUploadResult
}
