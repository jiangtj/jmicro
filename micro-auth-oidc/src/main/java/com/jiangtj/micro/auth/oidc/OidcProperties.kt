package com.jiangtj.micro.auth.oidc

data class OidcProperties(
    /**
     * 匹配 kid，默认匹配所有路径
     */
    val pattern: String = "*",

    /**
     * 匹配样式，默认 ANT 模式
     */
    var matcherStyle: MatcherStyle = MatcherStyle.ANT,

    /**
     * 匹配路径分隔符，默认为 "/"。仅适用于 ANT 模式
     */
    var pathSeparator: String = "/",

    /**
     * 存储 OpenID Connect 配置信息的 URL。
     * 该 URL 通常指向 OpenID Provider 的 .well-known/openid-configuration 端点，用于获取 OpenID Connect 的相关配置。
     */
    val openidConfiguration: String? = null,
)
