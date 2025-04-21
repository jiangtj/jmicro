package com.jiangtj.micro.common.utils;

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

}
