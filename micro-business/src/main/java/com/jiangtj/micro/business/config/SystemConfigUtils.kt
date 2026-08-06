package com.jiangtj.micro.business.config

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
