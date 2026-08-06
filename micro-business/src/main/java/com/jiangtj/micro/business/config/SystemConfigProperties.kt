package com.jiangtj.micro.business.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "system.config")
data class SystemConfigProperties(
    var enabled: Boolean = false,
    var filePath: String = "upload",
    var kv: MutableMap<String, String> = mutableMapOf(),
)
