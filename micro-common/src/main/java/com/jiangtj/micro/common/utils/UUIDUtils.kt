package com.jiangtj.micro.common.utils

import java.nio.ByteBuffer
import java.util.*

object UUIDUtils {
    // 使用Base64进一步压缩UUID - 将UUID转换为16字节的二进制形式后进行编码
    @JvmStatic
    fun compressToBase64(uuid: UUID): String {
        val byteBuffer = ByteBuffer.allocate(16)
        byteBuffer.putLong(uuid.mostSignificantBits)
        byteBuffer.putLong(uuid.leastSignificantBits)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(byteBuffer.array())
    }

    // 生成Base64压缩的UUID - 直接使用随机UUID的二进制形式
    @JvmStatic
    fun generateBase64Compressed(): String {
        val randomUUID = UUID.randomUUID()
        return compressToBase64(randomUUID)
    }
}
