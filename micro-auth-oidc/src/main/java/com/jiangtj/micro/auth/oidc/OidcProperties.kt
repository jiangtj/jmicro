package com.jiangtj.micro.auth.oidc

import org.springframework.boot.context.properties.ConfigurationProperties

data class OidcProperties(
    /**
     * 匹配 kid，默认匹配所有路径
     */
    val pattern: String = "*",
    /**
     * 存储 OpenID Connect 配置信息的 URL。
     * 该 URL 通常指向 OpenID Provider 的 .well-known/openid-configuration 端点，用于获取 OpenID Connect 的相关配置。
     */
    val openidConfiguration: String? = null,
)
