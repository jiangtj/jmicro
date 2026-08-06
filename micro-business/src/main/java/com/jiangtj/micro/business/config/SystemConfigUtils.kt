package com.jiangtj.micro.business.config

fun String.trimKey() = this.replace("-", "")
    .replace("_", "")
    .replace(" ", "")
    .replace(".", "")
    .lowercase()
