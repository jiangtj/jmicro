package com.jiangtj.micro.auth.oidc;

import lombok.Data;
import org.jspecify.annotations.Nullable;

@Data
public class OidcProperties {
    /**
     * 匹配参数，在非 ALWAYS 模式下生效，ANT 模式下为路径，PREFIX 模式下为前缀，REGEX 模式下为正则表达式
     */
    private String pattern = "*";

    /**
     * 匹配样式，默认 ALWAYS 模式
     */
    private MatcherStyle matcherStyle = MatcherStyle.ALWAYS;

    /**
     * 匹配路径分隔符，默认为 "/"。仅适用于 ANT 模式
     */
    private String pathSeparator = "/";

    /**
     * 存储 JSON Web Key Set (JWKS) 配置信息的 URL。
     * 优先级：1. jwksUri > 2. openidConfiguration
     */
    @Nullable
    private String jwksUri;

    /**
     * 存储 OpenID Connect 配置信息的 URL。
     * 该 URL 通常指向 OpenID Provider 的 .well-known/openid-configuration 端点，用于获取 OpenID Connect 的相关配置。
     * 优先级：1. jwksUri > 2. openidConfiguration
     */
    @Nullable
    private String openidConfiguration;
}