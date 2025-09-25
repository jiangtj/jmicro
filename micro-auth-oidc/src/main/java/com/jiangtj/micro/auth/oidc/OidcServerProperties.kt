package com.jiangtj.micro.auth.oidc

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jmicro.oidc.server")
data class OidcServerProperties(

    /**
     * 开启 OpenID Connect 服务器
     */
    val enabled: Boolean = false,

    /**
     * KID 前缀，默认 不添加
     */
    var kidPrefix: String? =null,

    var baseUrl: String? = null,

    var wellKnown: String = "/oidc/.well-known/openid-configuration",

    var jwksUri: String = "/oidc/jwks",

    var authorizationEndpoint: String = "/oidc/auth",

    var tokenEndpoint: String = "/oidc/token",

)
