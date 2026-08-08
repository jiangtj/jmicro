@file:JvmName("RandomStringUtils")
package com.jiangtj.micro.common.utils

import java.text.SimpleDateFormat
import java.util.Date

/**
 * 随机字符串生成工具类
 *
 * 提供多种生成随机字符串的方法，支持：
 * - 数字与大写字母混合
 * - 纯数字
 * - 十六进制字符
 * - 基于格式的自定义生成
 * - 唯一码生成（时间戳+随机数）
 * - 字符串分割工具
 *
 * 所有方法均为线程安全，可在多线程环境下使用。
 *
 * @author jiangtj
 * @since 1.0.0
 */

private const val NUMERIC_CHARS = "0123456789"
private const val LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz"
private val UPPERCASE_LETTERS = LOWERCASE_LETTERS.uppercase()
private const val HEX_LOWERCASE_CHARS = "0123456789abcdef"
private val HEX_UPPERCASE_CHARS = HEX_LOWERCASE_CHARS.uppercase()
private const val PLACEHOLDER_CHAR = '?'
private val LOCK = Any()

/**
 * 生成指定长度的随机字符串（包含数字和大写字母）
 *
 * @param size 生成字符串的长度，必须大于等于 0
 * @return 随机字符串，长度为指定的 size
 *
 * @sample
 * ```kotlin
 * RandomStringUtils.get(10) // 返回类似 "A3B7K9P2M1" 的字符串
 * RandomStringUtils.get(0)  // 返回空字符串 ""
 * ```
 */
fun get(size: Int): String {
    val src = NUMERIC_CHARS + UPPERCASE_LETTERS
    return buildString(size) {
        repeat(size) {
            append(getRandomChar(src))
        }
    }
}

/**
 * 根据格式字符串生成随机字符串
 *
 * 格式字符串中的 `?` 字符会被替换为随机字符（数字或大写字母），
 * 其他字符保持不变。
 *
 * @param format 格式字符串，`?` 表示随机字符占位符
 * @return 按格式生成的随机字符串
 *
 * @sample
 * ```kotlin
 * RandomStringUtils.get("test-???-test") // 返回类似 "test-A3B-test" 的字符串
 * RandomStringUtils.get("")              // 返回空字符串 ""
 * ```
 */
fun get(format: String): String {
    return get(format, PLACEHOLDER_CHAR)
}

/**
 * 根据格式字符串和自定义占位符生成随机字符串
 *
 * @param format 格式字符串
 * @param esc 占位符字符，该字符在格式字符串中的位置会被替换为随机字符
 * @return 按格式生成的随机字符串
 *
 * @sample
 * ```kotlin
 * RandomStringUtils.get("test-###-test", '#') // 返回类似 "test-A3B-test" 的字符串
 * ```
 */
fun get(format: String, esc: Char): String {
    return get(NUMERIC_CHARS + UPPERCASE_LETTERS, format, esc)
}

/**
 * 根据自定义字符源、格式字符串和占位符生成随机字符串
 *
 * @param source 自定义字符源，随机字符将从此字符串中选取
 * @param format 格式字符串
 * @param esc 占位符字符
 * @return 按格式生成的随机字符串
 *
 * @sample
 * ```kotlin
 * RandomStringUtils.get("abc", "a?b?c", '?') // 返回类似 "aXbYc" 的字符串，X和Y从"abc"中选取
 * ```
 */
fun get(source: String, format: String, esc: Char): String {
    return buildString(format.length) {
        for (char in format) {
            if (char.equals(esc, ignoreCase = true)) {
                append(getRandomChar(source))
            } else {
                append(char)
            }
        }
    }
}

/**
 * 生成指定长度的随机数字字符串
 *
 * @param size 生成字符串的长度，必须大于等于 0
 * @return 随机数字字符串
 *
 * @sample
 * ```kotlin
 * RandomStringUtils.getNum(8) // 返回类似 "38472915" 的字符串
 * RandomStringUtils.getNum(0) // 返回空字符串 ""
 * ```
 */
fun getNum(size: Int): String {
    return buildString(size) {
        repeat(size) {
            append(getRandomChar(NUMERIC_CHARS))
        }
    }
}

/**
 * 根据格式字符串生成随机数字字符串
 *
 * @param format 格式字符串，`?` 表示随机数字占位符
 * @return 按格式生成的随机数字字符串
 *
 * @sample
 * ```kotlin
 * RandomStringUtils.getNum("SN-??-??") // 返回类似 "SN-38-47" 的字符串
 * ```
 */
fun getNum(format: String): String {
    return getNum(format, PLACEHOLDER_CHAR)
}

/**
 * 根据格式字符串和自定义占位符生成随机数字字符串
 *
 * @param format 格式字符串
 * @param esc 占位符字符
 * @return 按格式生成的随机数字字符串
 *
 * @sample
 * ```kotlin
 * RandomStringUtils.getNum("SN-##-##", '#') // 返回类似 "SN-38-47" 的字符串
 * ```
 */
fun getNum(format: String, esc: Char): String {
    return get(NUMERIC_CHARS, format, esc)
}

/**
 * 生成指定长度的随机十六进制字符串（大写）
 *
 * @param size 生成字符串的长度，必须大于等于 0
 * @return 随机十六进制字符串（大写）
 *
 * @sample
 * ```kotlin
 * RandomStringUtils.getHex(6) // 返回类似 "A3F7B2" 的字符串
 * RandomStringUtils.getHex(0) // 返回空字符串 ""
 * ```
 */
fun getHex(size: Int): String {
    return buildString(size) {
        repeat(size) {
            append(getRandomChar(HEX_UPPERCASE_CHARS))
        }
    }
}

/**
 * 根据格式字符串生成随机十六进制字符串
 *
 * @param format 格式字符串，`?` 表示随机十六进制字符占位符
 * @return 按格式生成的随机十六进制字符串
 *
 * @sample
 * ```kotlin
 * RandomStringUtils.getHex("0x????") // 返回类似 "0xA3F7" 的字符串
 * ```
 */
fun getHex(format: String): String {
    return getHex(format, PLACEHOLDER_CHAR)
}

/**
 * 根据格式字符串和自定义占位符生成随机十六进制字符串
 *
 * @param format 格式字符串
 * @param esc 占位符字符
 * @return 按格式生成的随机十六进制字符串
 *
 * @sample
 * ```kotlin
 * RandomStringUtils.getHex("0x####", '#') // 返回类似 "0xA3F7" 的字符串
 * ```
 */
fun getHex(format: String, esc: Char): String {
    return get(HEX_UPPERCASE_CHARS, format, esc)
}

/**
 * 从指定字符源中获取一个随机字符
 *
 * @param src 字符源字符串
 * @return 随机选取的单个字符，如果 src 为空则返回空字符串
 */
private fun getRandomChar(src: String): String {
    if (src.isEmpty()) {
        return ""
    }
    return src[(Math.random() * src.length).toInt()].toString()
}

/**
 * 生成唯一码
 *
 * 生成一个 26 位的数字字符串，格式为：yyyyMMddHHmmssSSS + 11位随机数
 * 此方法线程安全，适用于高并发场景下生成唯一标识。
 *
 * @return 26 位数字字符串，格式：17位时间戳 + 9位随机数
 *
 * @sample
 * ```kotlin
 * RandomStringUtils.getNextVal() // 返回类似 "20240115123045123123456789" 的字符串
 * ```
 */
fun getNextVal(): String {
    synchronized(LOCK) {
        val formatDate = SimpleDateFormat("yyyyMMddHHmmssSSS")
        return formatDate.format(Date()) +
                Math.round(Math.random() * 899999999 + 100000000)
    }
}

/**
 * 将字符串按指定长度分割成列表
 *
 * 如果字符串长度不能被指定长度整除，最后一个元素的长度会小于指定长度。
 *
 * @param inputString 要分割的原始字符串
 * @param length 每个子字符串的长度
 * @return 分割后的字符串列表，如果输入为空字符串则返回空列表
 *
 * @sample
 * ```kotlin
 * RandomStringUtils.subStringByLength("123456789", 3)   // 返回 ["123", "456", "789"]
 * RandomStringUtils.subStringByLength("1234567890", 3) // 返回 ["123", "456", "789", "0"]
 * RandomStringUtils.subStringByLength("", 3)           // 返回 []
 * ```
 */
fun subStringByLength(inputString: String, length: Int): List<String> {
    if (inputString.isEmpty()) {
        return emptyList()
    }
    var size = inputString.length / length
    if (inputString.length % length != 0) {
        size++
    }
    return subStringByLengthAndSize(inputString, length, size).filterNotNull()
}

/**
 * 将字符串按指定长度和数量分割成列表
 *
 * 如果指定的 size 大于实际需要的大小，多余的元素将为 null。
 *
 * @param inputString 要分割的原始字符串
 * @param length 每个子字符串的长度
 * @param size 期望返回的列表大小
 * @return 分割后的字符串列表，如果起始位置超出字符串长度则对应元素为 null
 *
 * @sample
 * ```kotlin
 * RandomStringUtils.subStringByLengthAndSize("123456789", 3, 3) // 返回 ["123", "456", "789"]
 * RandomStringUtils.subStringByLengthAndSize("123456", 3, 3)    // 返回 ["123", "456", null]
 * ```
 */
fun subStringByLengthAndSize(inputString: String, length: Int, size: Int): List<String?> {
    return List(size) { index ->
        substring(inputString, index * length, (index + 1) * length)
    }
}

/**
 * 截取子字符串
 *
 * 安全的字符串截取方法，当结束位置超出字符串长度时自动调整。
 *
 * @param str 原始字符串
 * @param f 起始位置（包含）
 * @param t 结束位置（不包含）
 * @return 截取的子字符串，如果起始位置大于等于字符串长度则返回 null
 *
 * @sample
 * ```kotlin
 * RandomStringUtils.substring("12345", 1, 4)  // 返回 "234"
 * RandomStringUtils.substring("12345", 6, 8)  // 返回 null
 * RandomStringUtils.substring("12345", 3, 8)  // 返回 "45"
 * ```
 */
fun substring(str: String, f: Int, t: Int): String? {
    if (f >= str.length) {
        return null
    }
    return if (t > str.length) {
        str.substring(f)
    } else {
        str.substring(f, t)
    }
}
