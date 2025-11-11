package com.jiangtj.micro.auth.oidc.cas;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenID Connect 服务器配置属性
 */
@Data
@ConfigurationProperties("jmicro.oidc.server")
public class OidcServerProperties {

    /**
     * 开启 OpenID Connect 服务器
     * 默认值: false
     */
    private boolean enabled = false;

    /**
     * KID 前缀，默认不添加
     */
    private String kidPrefix;

    /**
     * 基础URL
     */
    private String baseUrl;

    /**
     * Well-known 配置端点
     * 默认值: /oidc/.well-known/openid-configuration
     */
    private String wellKnown = "/oidc/.well-known/openid-configuration";

    /**
     * JWKS URI 端点
     * 默认值: /oidc/jwks
     */
    private String jwksUri = "/oidc/jwks";

    /**
     * 授权端点
     * 默认值: /oidc/auth
     */
    private String authorizationEndpoint = "/oidc/auth";

    /**
     * 令牌端点
     * 默认值: /oidc/token
     */
    private String tokenEndpoint = "/oidc/token";

    /**
     * 应用配置
     */
    private List<OidcServerClientProperties> clients = new ArrayList<>();

}
