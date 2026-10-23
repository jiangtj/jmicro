package com.jiangtj.micro.demobackend.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * demo 的 JWT 配置（{@code jmicro.demo.jwt.*}）
 */
@Data
@Component
@ConfigurationProperties("jmicro.demo.jwt")
public class DemoJwtProperties {

    /**
     * 签发者，解析后写入 {@code Subject.issuer}
     */
    private String issuer = "jmicro-demo";

    /**
     * HS256 签名密钥，长度至少 32 字节
     */
    private String secret = "jmicro-demo-secret-key-change-me-0123456789";

    /**
     * token 有效期
     */
    private Duration ttl = Duration.ofHours(2);
}
