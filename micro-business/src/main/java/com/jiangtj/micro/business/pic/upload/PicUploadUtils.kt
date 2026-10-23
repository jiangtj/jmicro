package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.common.utils.FileNameUtils
import org.springframework.web.multipart.MultipartFile

/**
 * 图片上传工具类
 */
object PicUploadUtils {

    /**
     * 按原始文件的扩展名生成随机文件名
     */
    @JvmStatic
    fun generateNewFileName(file: MultipartFile): String {
        val extension = FileNameUtils.getFileExtension(file.originalFilename)
        return FileNameUtils.getRandomFileNameWithSuffix(extension)
    }
}
