package com.jiangtj.micro.business.config

/**
 * 敏感配置的脱敏占位符
 */
const val MASKED_VALUE = "******"

fun String.trimKey() = this.replace("-", "")
    .replace("_", "")
    .replace(" ", "")
    .replace(".", "")
    .lowercase()

fun multiLineToString(value: String) = value
    .replace("，", ",")
    .replace("\r\n", ",")
    .replace("\n", ",")
    .replace("\r", ",")
    .split(",")
    .joinToString(",") { it.trim() }

fun multiLineToView(value: String) = value.split(",").joinToString("\n")
