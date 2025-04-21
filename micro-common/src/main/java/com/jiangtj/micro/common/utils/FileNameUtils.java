package com.jiangtj.micro.common.utils;

import org.springframework.util.StringUtils;

/**
 * 文件名工具类
 */
public class FileNameUtils {

    /**
     * 获取文件名字的前缀
     */
    public static String getRandomFileNameWithSuffix(String suffix) {
        if (suffix.startsWith(".")) {
            return UUIDUtils.generateBase64Compressed() + suffix;
        }
        return UUIDUtils.generateBase64Compressed() + "." + suffix;
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }

}
