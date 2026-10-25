package __PKG__.auth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * 应用自定义 JWT 配置（`jmicro.app.jwt.*`）。
 */
@Component
@ConfigurationProperties("jmicro.app.jwt")
class JwtProperties {

    /**
     * 签发者，解析后写入 `Subject.issuer`
     */
    var issuer: String = "__PROJECT_NAME__"

    /**
     * HS256 签名密钥，长度至少 32 字节
     */
    var secret: String = "change-me-__PROJECT_NAME__-0123456789abcdef0123456789abcdef"

    /**
     * token 有效期
     */
    var ttl: Duration = Duration.ofHours(2)
}
