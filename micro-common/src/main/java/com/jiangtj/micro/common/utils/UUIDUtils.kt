package com.jiangtj.micro.common.utils;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class UUIDUtils {

    // 使用Base64进一步压缩UUID - 将UUID转换为16字节的二进制形式后进行编码
    public static String compressToBase64(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(byteBuffer.array());
    }

    // 生成Base64压缩的UUID - 直接使用随机UUID的二进制形式
    public static String generateBase64Compressed() {
        UUID randomUUID = UUID.randomUUID();
        return compressToBase64(randomUUID);
    }
}
