package com.jiangtj.micro.demobackend.auth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * demo 的 JWT 配置（`jmicro.demo.jwt.*`）
 */
@Component
@ConfigurationProperties("jmicro.demo.jwt")
class DemoJwtProperties {

    /**
     * 签发者，解析后写入 `Subject.issuer`
     */
    var issuer: String = "jmicro-demo"

    /**
     * HS256 签名密钥，长度至少 32 字节
     */
    var secret: String = "jmicro-demo-secret-key-change-me-0123456789"

    /**
     * token 有效期
     */
    var ttl: Duration = Duration.ofHours(2)
}
