package com.jiangtj.micro.auth.oidc.cas;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenID Connect 服务器应用配置属性
 */
@Data
public class OidcServerClientProperties {
    /**
     * id
     */
    private String clientId;

    /**
     * 密钥
     */
    private String clientSecret;

    /**
     * 回调地址
     */
    private List<String> callbackUri = new ArrayList<>();

}
