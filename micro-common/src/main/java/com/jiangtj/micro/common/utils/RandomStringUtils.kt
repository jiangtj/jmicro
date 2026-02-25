package com.jiangtj.micro.common.utils

import java.text.SimpleDateFormat
import java.util.Date

/**
 * 生成随机字符串工具类
 *
 * 提供多种生成随机字符串的方法，包括：
 * - 数字和大写字母组合的随机字符串
 * - 纯数字随机字符串
 * - 十六进制随机字符串
 * - 基于格式的随机字符串（使用占位符）
 * - 基于时间戳的唯一码生成
 * - 字符串分割工具
 *
 * 所有方法都是线程安全的，可在多线程环境下使用。
 */
object RandomStringUtils {

    private const val SRC_NUMBER = "0123456789"
    private const val SRC_LOWER = "abcdefghijklmnopqrstuvwxyz"
    private val SRC_UPPER = SRC_LOWER.uppercase()
    private const val SRC_HEX_LOWER = "0123456789abcdef"
    private val SRC_HEX_UPPER = SRC_HEX_LOWER.uppercase()
    private const val ESC_CHAR = '?'

    private val locker = Object()

    /**
     * 生成指定长度的随机字符串（数字和大写字母组合）
     *
     * @param size 生成的字符串长度
     * @return 指定长度的随机字符串，包含数字和大写字母
     */
    @JvmStatic
    fun get(size: Int): String {
        val src = SRC_NUMBER + SRC_UPPER
        return buildString(size) {
            repeat(size) {
                append(getRandomChar(src))
            }
        }
    }

    /**
     * 根据格式生成随机字符串，使用默认占位符 '?'
     *
     * 格式字符串中的 '?' 会被替换为随机字符（数字或大写字母），其他字符保持不变。
     * 例如："TEST-???-XXX" 可能生成 "TEST-9A7-XXX"
     *
     * @param format 格式字符串，其中 '?' 作为占位符
     * @return 根据格式生成的随机字符串
     */
    @JvmStatic
    fun get(format: String): String {
        return get(format, ESC_CHAR)
    }

    /**
     * 根据格式和自定义占位符生成随机字符串（数字和大写字母组合）
     *
     * 例如：使用 '#' 作为占位符，格式 "TEST-###" 可能生成 "TEST-9A7"
     *
     * @param format 格式字符串
     * @param esc 占位符字符
     * @return 根据格式生成的随机字符串
     */
    @JvmStatic
    fun get(format: String, esc: Char): String {
        return get(SRC_NUMBER + SRC_UPPER, format, esc)
    }

    /**
     * 根据自定义字符源、格式和占位符生成随机字符串
     *
     * 提供最灵活的随机字符串生成方式，允许指定：
     * - 字符源：用于生成随机字符的字符集合
     * - 格式：输出字符串的格式模板
     * - 占位符：在格式中被替换为随机字符的标记
     *
     * @param source 字符源，用于生成随机字符
     * @param format 格式字符串
     * @param esc 占位符字符
     * @return 根据格式生成的随机字符串
     */
    @JvmStatic
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
     * @param size 生成的字符串长度
     * @return 指定长度的纯数字随机字符串
     */
    @JvmStatic
    fun getNum(size: Int): String {
        return buildString(size) {
            repeat(size) {
                append(getRandomChar(SRC_NUMBER))
            }
        }
    }

    /**
     * 根据格式生成随机数字字符串，使用默认占位符 '?'
     *
     * 格式字符串中的 '?' 会被替换为随机数字，其他字符保持不变。
     * 例如："SN-???" 可能生成 "SN-987"
     *
     * @param format 格式字符串，其中 '?' 作为占位符
     * @return 根据格式生成的随机数字字符串
     */
    @JvmStatic
    fun getNum(format: String): String {
        return getNum(format, ESC_CHAR)
    }

    /**
     * 根据格式和自定义占位符生成随机数字字符串
     *
     * 例如：使用 '#' 作为占位符，格式 "SN-###" 可能生成 "SN-987"
     *
     * @param format 格式字符串
     * @param esc 占位符字符
     * @return 根据格式生成的随机数字字符串
     */
    @JvmStatic
    fun getNum(format: String, esc: Char): String {
        return get(SRC_NUMBER, format, esc)
    }

    /**
     * 生成指定长度的随机十六进制字符串（大写）
     *
     * @param size 生成的字符串长度
     * @return 指定长度的大写十六进制随机字符串
     */
    @JvmStatic
    fun getHex(size: Int): String {
        return buildString(size) {
            repeat(size) {
                append(getRandomChar(SRC_HEX_UPPER))
            }
        }
    }

    /**
     * 根据格式生成随机十六进制字符串，使用默认占位符 '?'
     *
     * 格式字符串中的 '?' 会被替换为随机十六进制字符（0-9, A-F），其他字符保持不变。
     * 例如："0x????" 可能生成 "0x9A7F"
     *
     * @param format 格式字符串，其中 '?' 作为占位符
     * @return 根据格式生成的随机十六进制字符串
     */
    @JvmStatic
    fun getHex(format: String): String {
        return getHex(format, ESC_CHAR)
    }

    /**
     * 根据格式和自定义占位符生成随机十六进制字符串
     *
     * 例如：使用 '#' 作为占位符，格式 "0x####" 可能生成 "0x9A7F"
     *
     * @param format 格式字符串
     * @param esc 占位符字符
     * @return 根据格式生成的随机十六进制字符串
     */
    @JvmStatic
    fun getHex(format: String, esc: Char): String {
        return get(SRC_HEX_UPPER, format, esc)
    }

    /**
     * 从字符源中获取一个随机字符
     *
     * @param src 字符源
     * @return 随机字符，如果字符源为空则返回空字符串
     */
    private fun getRandomChar(src: String): String {
        if (src.isEmpty()) {
            return ""
        }
        return src[(Math.random() * src.length).toInt()].toString()
    }

    /**
     * 生成唯一码（线程安全）
     *
     * 返回值格式：26位数字字符串
     * - 前17位：时间戳（yyyyMMddHHmmssSSS）
     * - 后9位：随机数字（100000000-999999999）
     *
     * 该方法是线程安全的，适合在高并发环境下生成唯一标识。
     * 例如："202312251230450009123456789"
     *
     * @return 26位数字唯一码
     */
    @JvmStatic
    fun getNextVal(): String {
        synchronized(locker) {
            val formatDate = SimpleDateFormat("yyyyMMddHHmmssSSS")
            return formatDate.format(Date()) +
                Math.round(Math.random() * 899999999 + 100000000)
        }
    }

    /**
     * 将原始字符串分割成指定长度的字符串列表
     *
     * 如果字符串长度不能被指定长度整除，最后一个元素可能较短。
     * 例如："123456789" 按长度 3 分割得到 ["123", "456", "789"]
     * 例如："1234567890" 按长度 3 分割得到 ["123", "456", "789", "0"]
     *
     * @param inputString 要分割的原始字符串
     * @param length 每个子字符串的长度
     * @return 分割后的字符串列表
     */
    @JvmStatic
    fun subStringByLength(inputString: String, length: Int): List<String> {
        var size = inputString.length / length
        if (inputString.length % length != 0) {
            size += 1
        }
        return subStringByLengthAndSize(inputString, length, size).filterNotNull()
    }

    /**
     * 将原始字符串分割成指定长度和大小的字符串列表
     *
     * 如果指定的 [size] 大于实际需要的大小，超出部分将填充 null。
     * 例如："123456" 按长度 3、大小 3 分割得到 ["123", "456", null]
     *
     * @param inputString 要分割的原始字符串
     * @param length 每个子字符串的长度
     * @param size 返回列表的大小
     * @return 分割后的字符串列表，可能包含 null 值
     */
    @JvmStatic
    fun subStringByLengthAndSize(inputString: String, length: Int, size: Int): List<String?> {
        return List(size) { index ->
            substring(inputString, index * length, (index + 1) * length)
        }
    }

    /**
     * 截取字符串的子串
     *
     * - 如果起始位置大于等于字符串长度，返回 null
     * - 如果结束位置超出字符串长度，返回到字符串末尾的子串
     * - 正常情况下返回 [f, t) 区间的子串
     *
     * @param str 原始字符串
     * @param f 起始位置（包含）
     * @param t 结束位置（不包含）
     * @return 截取的子串，如果起始位置无效则返回 null
     */
    @JvmStatic
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
}
