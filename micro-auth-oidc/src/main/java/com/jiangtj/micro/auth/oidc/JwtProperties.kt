package com.jiangtj.micro.auth.oidc

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "micro.jwt")
data class JwtProperties(
    var oidc: List<OidcProperties> = emptyList(),
)

enum class MatcherStyle {
    PREFIX,
    REGEX,
    ANT
}
