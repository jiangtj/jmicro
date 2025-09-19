package com.jiangtj.micro.auth.oidc

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "micro.jwt")
data class JwtProperties(
    var oidc: List<OidcProperties> = emptyList(),
    var matcherStyle: MatcherStyle = MatcherStyle.ANT
)

enum class MatcherStyle {
    // todo 支持正则表达式
    REGEX,
    ANT
}
