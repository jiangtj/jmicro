package com.jiangtj.micro.auth.oidc

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "micro.oidc.server")
data class OidcServerProperties(

    /**
     * 开启 OpenID Connect 服务器
     */
    val enabled: Boolean = false,

    /**
     * KID 前缀，默认 不添加
     */
    var kidPrefix: String? =null,

)
