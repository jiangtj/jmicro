package com.jiangtj.micro.business.config

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * bcrypt 哈希工具，用于 `secret` 类配置项（如密码）的安全存储与校验。
 */
object BCryptUtils {

    private val encoder = BCryptPasswordEncoder()

    /** 对明文进行 bcrypt 哈希。 */
    fun encode(raw: String): String = encoder.encode(raw) ?: ""

    /** 校验明文是否与 bcrypt 哈希匹配。 */
    fun matches(raw: String, encoded: String): Boolean = encoder.matches(raw, encoded)

    /** 判断给定字符串是否已是 bcrypt 哈希（以 $2a$/$2b$/$2y$ 开头）。 */
    fun isEncoded(value: String): Boolean = value.startsWith("$2a$")
        || value.startsWith("$2b$")
        || value.startsWith("$2y$")
}
