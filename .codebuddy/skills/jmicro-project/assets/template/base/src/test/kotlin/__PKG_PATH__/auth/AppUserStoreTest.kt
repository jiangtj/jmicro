package __PKG__.auth

import com.jiangtj.micro.test.JMicroTest
import com.jiangtj.micro.web.BaseException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

/**
 * 登录与 JWT 契约的 Service 集成测试（不经过 Controller）：
 * - 登录时按角色解析权限
 * - token 只携带角色，不携带权限
 */
@JMicroTest
class AppUserStoreTest {

    @Autowired
    lateinit var userStore: AppUserStore

    @Autowired
    lateinit var jwtService: JwtService

    @Test
    fun `login resolves roles and permissions`() {
        val admin = userStore.login("admin", "admin123")
        assertThat(admin.nickname).isEqualTo("管理员")
        assertThat(admin.roles).containsExactly("admin")
        assertThat(admin.permissions).containsExactly("book:read", "book:write")

        val reader = userStore.login("reader", "reader123")
        assertThat(reader.roles).containsExactly("reader")
        assertThat(reader.permissions).containsExactly("book:read")
    }

    @Test
    fun `login with wrong password throws unauthorized`() {
        val ex = assertThrows<BaseException> { userStore.login("admin", "wrong") }
        assertThat(ex.statusCode.value()).isEqualTo(401)
        assertThat(ex.body.detail).isEqualTo("用户名或密码错误")
    }

    @Test
    fun `token only carries roles`() {
        val token = jwtService.createToken(userStore.login("admin", "admin123"))
        val claims = checkNotNull(jwtService.parse(token)) { "签发的 token 无法解析" }

        assertThat(claims.subject).isEqualTo("admin")
        assertThat(jwtService.roles(claims)).containsExactly("admin")
        assertThat(claims["permissions"]).isNull()
    }

    @Test
    fun `tampered token is rejected`() {
        val token = jwtService.createToken(userStore.login("reader", "reader123"))
        assertThat(jwtService.parse("$token-x")).isNull()
        assertThat(jwtService.parse("not-a-token")).isNull()
    }
}
