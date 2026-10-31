package com.jiangtj.micro.demobackend.auth.user

import com.jiangtj.micro.demobackend.dao.SysUserRepository
import com.jiangtj.micro.demobackend.jooq.tables.daos.SysRoleDao
import com.jiangtj.micro.test.JMicroTest
import com.jiangtj.micro.test.WithMockUser
import com.jiangtj.micro.web.BaseException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

/**
 * 用户 Service 集成测试（真实 Spring 上下文 + H2，不经过 Controller）。
 *
 * 用户管理仅 admin 可用，用 [WithMockUser] 表达调用者身份。
 * 写库操作由 Spring Test 的 [Transactional] 在用例结束后自动回滚，无需手工恢复数据。
 */
@JMicroTest
@Transactional
@WithMockUser(roles = ["admin"])
class UserServiceTest {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userRepository: SysUserRepository

    @Autowired
    lateinit var roleDao: SysRoleDao

    @Autowired
    lateinit var userStore: DemoUserStore

    private fun roleId(code: String): Long =
        requireNotNull(roleDao.fetchOneByCode(code)?.id) { "角色不存在：$code" }

    @Test
    fun `seeded users carry their roles`() {
        val users = userService.page(PageRequest.of(0, 100)).content.associateBy { it.username }
        assertThat(users.keys).contains("admin", "reader")
        assertThat(users.getValue("admin").roleIds).containsExactly(roleId("admin"))
        assertThat(users.getValue("reader").roleIds).containsExactly(roleId("reader"))
        assertThat(users.getValue("admin").nickname).isEqualTo("管理员")
    }

    @Test
    fun `page filters by username nickname or enabled`() {
        val byUsername = userService.page(PageRequest.of(0, 10), UserQuery(username = "admin"))
        assertThat(byUsername.content.map { it.username }).containsExactly("admin")
        assertThat(byUsername.totalElements).isEqualTo(1)

        val byNickname = userService.page(PageRequest.of(0, 10), UserQuery(nickname = "管理"))
        assertThat(byNickname.content.map { it.username }).containsExactly("admin")

        assertThat(userService.page(PageRequest.of(0, 10), UserQuery(enabled = true)).totalElements)
            .isGreaterThanOrEqualTo(2)
        assertThat(userService.page(PageRequest.of(0, 10), UserQuery(enabled = false)).totalElements)
            .isZero()
    }

    @Test
    fun `create update delete`() {
        val readerId = roleId("reader")
        val adminId = roleId("admin")

        val created = userService.create(UserForm("tester", "tester123", "测试用户", true, listOf(readerId)))
        val id = created.id
        assertThat(created.username).isEqualTo("tester")
        assertThat(created.nickname).isEqualTo("测试用户")
        assertThat(created.roleIds).containsExactly(readerId)
        assertThat(userService.get(id)).isEqualTo(created)
        // 新建用户可以直接登录，密码以 bcrypt 存储
        assertThat(userStore.login("tester", "tester123").roles).containsExactly("reader")

        // 密码留空表示不修改，角色绑定整体覆盖
        val updated = userService.update(id, UserForm("tester2", null, "测试用户（改）", true, listOf(adminId)))
        assertThat(updated.username).isEqualTo("tester2")
        assertThat(updated.nickname).isEqualTo("测试用户（改）")
        assertThat(updated.roleIds).containsExactly(adminId)
        assertThat(userStore.login("tester2", "tester123").roles).containsExactly("admin")

        // 传入密码则重置密码，旧密码立即失效
        userService.update(id, UserForm("tester2", "newpass123", null, true, listOf(adminId)))
        assertThat(userStore.login("tester2", "newpass123").username).isEqualTo("tester2")
        val loginEx = assertThrows<BaseException> { userStore.login("tester2", "tester123") }
        assertThat(loginEx.statusCode.value()).isEqualTo(401)

        // 昵称留空时回退为用户名
        assertThat(userService.update(id, UserForm("tester2", null, "  ", true, listOf(adminId))).nickname)
            .isEqualTo("tester2")

        userService.delete(id)
        assertThat(userService.page(PageRequest.of(0, 100)).content.map { it.username }).doesNotContain("tester2")
        // 关联数据由应用层维护，删除用户时需一并清理
        assertThat(userRepository.findRoleIdsByUserId(id)).isEmpty()

        val ex = assertThrows<BaseException> { userService.get(id) }
        assertThat(ex.statusCode.value()).isEqualTo(404)
        assertThat(ex.body.detail).isEqualTo("用户不存在：$id")
    }

    @Test
    fun `disabled user cannot login`() {
        userService.create(UserForm("blocked", "blocked123", null, false, emptyList()))
        assertThat(userService.page(PageRequest.of(0, 10), UserQuery(username = "blocked")).content.single().enabled)
            .isFalse()

        val ex = assertThrows<BaseException> { userStore.login("blocked", "blocked123") }
        assertThat(ex.statusCode.value()).isEqualTo(401)
    }

    @Test
    fun `duplicate username is rejected`() {
        val ex = assertThrows<BaseException> {
            userService.create(UserForm("admin", "admin123"))
        }
        assertThat(ex.statusCode.value()).isEqualTo(400)
        assertThat(ex.body.detail).isEqualTo("用户名已存在：admin")
    }

    @Test
    fun `password is required on create`() {
        val ex = assertThrows<BaseException> {
            userService.create(UserForm("tester-nopass", null))
        }
        assertThat(ex.statusCode.value()).isEqualTo(400)
        assertThat(ex.body.detail).isEqualTo("密码不能为空")
    }

    @Test
    fun `unknown role id is rejected`() {
        val ex = assertThrows<BaseException> {
            userService.create(UserForm("tester-unknown", "tester123", null, true, listOf(-1L)))
        }
        assertThat(ex.statusCode.value()).isEqualTo(400)
        assertThat(ex.body.detail).isEqualTo("未知的角色 id：-1")
    }

    @Test
    fun `missing user throws not found`() {
        val getEx = assertThrows<BaseException> { userService.get(-1) }
        assertThat(getEx.statusCode.value()).isEqualTo(404)
        assertThat(getEx.body.detail).isEqualTo("用户不存在：-1")

        val deleteEx = assertThrows<BaseException> { userService.delete(-1) }
        assertThat(deleteEx.statusCode.value()).isEqualTo(404)
    }
}
