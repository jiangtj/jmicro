package com.jiangtj.micro.common.utils

import com.jiangtj.micro.common.utils.UUIDUtils.generateBase64Compressed

/**
 * 文件名工具类
 */
object FileNameUtils {
    /**
     * 获取文件名字的前缀
     */
    @JvmStatic
    fun getRandomFileNameWithSuffix(suffix: String): String {
        if (suffix.startsWith(".")) {
            return generateBase64Compressed() + suffix
        }
        return generateBase64Compressed() + "." + suffix
    }

    /**
     * 获取文件扩展名
     */
    @JvmStatic
    fun getFileExtension(filename: String?): String {
        if (filename == null) {
            return ""
        }
        val dotIndex = filename.lastIndexOf('.')
        if (dotIndex < 0) {
            return ""
        }
        return filename.substring(dotIndex + 1).lowercase()
    }
}
