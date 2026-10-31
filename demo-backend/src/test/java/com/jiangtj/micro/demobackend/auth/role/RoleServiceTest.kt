package com.jiangtj.micro.demobackend.auth.role

import com.jiangtj.micro.demobackend.auth.permission.RolePermissionResolver
import com.jiangtj.micro.demobackend.dao.SysRoleRepository
import com.jiangtj.micro.test.JMicroTest
import com.jiangtj.micro.test.WithMockUser
import com.jiangtj.micro.web.BaseException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

/**
 * 角色 Service 集成测试（真实 Spring 上下文 + H2，不经过 Controller）。
 *
 * 角色管理仅 admin 可用，用 [WithMockUser] 表达调用者身份。
 * 写库操作由 Spring Test 的 [Transactional] 在用例结束后自动回滚，无需手工恢复数据。
 */
@JMicroTest
@Transactional
@WithMockUser(roles = ["admin"])
class RoleServiceTest {

    @Autowired
    lateinit var roleService: RoleService

    @Autowired
    lateinit var rolePermissionResolver: RolePermissionResolver

    @Autowired
    lateinit var roleRepository: SysRoleRepository

    /**
     * 事务回滚只回滚数据库，[RolePermissionResolver] 的内存缓存需要显式清理，避免影响其它用例。
     */
    @AfterEach
    fun clearPermissionCache() {
        listOf("admin", "reader", "tester").forEach(rolePermissionResolver::invalidate)
    }

    @Test
    fun `seeded roles carry their permissions`() {
        val roles = roleService.list().associateBy { it.code }
        assertThat(roles.keys).contains("admin", "reader")
        assertThat(roles.getValue("admin").permissionCodes).containsExactly("book:read", "book:write")
        assertThat(roles.getValue("reader").permissionCodes).containsExactly("book:read")
    }

    @Test
    fun `page returns roles with their permissions`() {
        val first = roleService.page(PageRequest.of(0, 1))
        assertThat(first.content).hasSize(1)
        assertThat(first.totalElements).isGreaterThanOrEqualTo(2)
        assertThat(first.totalPages).isGreaterThanOrEqualTo(2)
        // 默认按主键倒序：第二页的角色 id 更小
        val second = roleService.page(PageRequest.of(1, 1))
        assertThat(first.content.single().id).isGreaterThan(second.content.single().id)

        val all = roleService.page(PageRequest.of(0, 100))
        val byCode = all.content.associateBy { it.code }
        assertThat(byCode.keys).contains("admin", "reader")
        assertThat(byCode.getValue("admin").permissionCodes).containsExactly("book:read", "book:write")
        assertThat(byCode.getValue("reader").permissionCodes).containsExactly("book:read")
    }

    @Test
    fun `page filters by code or name`() {
        val byCode = roleService.page(PageRequest.of(0, 10), RoleQuery(code = "reader"))
        assertThat(byCode.content.map { it.code }).containsExactly("reader")
        assertThat(byCode.totalElements).isEqualTo(1)

        val byName = roleService.page(PageRequest.of(0, 10), RoleQuery(name = "管理"))
        assertThat(byName.content.map { it.code }).containsExactly("admin")

        assertThat(roleService.page(PageRequest.of(0, 10), RoleQuery(name = "  ")).totalElements)
            .isGreaterThanOrEqualTo(2)
    }

    @Test
    fun `create update delete`() {
        val created = roleService.create(RoleForm("tester", "测试角色", listOf("book:read")))
        val id = created.id
        assertThat(created.code).isEqualTo("tester")
        assertThat(created.permissionCodes).containsExactly("book:read")
        assertThat(roleService.get(id)).isEqualTo(created)
        assertThat(roleService.list().map { it.code }).contains("tester")

        // 权限绑定整体覆盖
        val updated = roleService.update(
            id,
            RoleForm("tester", "测试角色（改）", listOf("book:read", "book:write")),
        )
        assertThat(updated.name).isEqualTo("测试角色（改）")
        assertThat(updated.permissionCodes).containsExactly("book:read", "book:write")
        assertThat(roleService.get(id).permissionCodes).containsExactly("book:read", "book:write")

        roleService.delete(id)
        assertThat(roleService.list().map { it.code }).doesNotContain("tester")
        // 关联数据由应用层维护，删除角色时需一并清理
        assertThat(roleRepository.findPermissionCodesByRoleId(id)).isEmpty()

        val ex = assertThrows<BaseException> { roleService.get(id) }
        assertThat(ex.statusCode.value()).isEqualTo(404)
        assertThat(ex.body.detail).isEqualTo("角色不存在：$id")
    }

    @Test
    fun `duplicate code is rejected`() {
        val ex = assertThrows<BaseException> {
            roleService.create(RoleForm("admin", "重复角色"))
        }
        assertThat(ex.statusCode.value()).isEqualTo(400)
        assertThat(ex.body.detail).isEqualTo("角色编码已存在：admin")
    }

    @Test
    fun `unknown permission code is rejected`() {
        val ex = assertThrows<BaseException> {
            roleService.create(RoleForm("tester-unknown", "未知权限", listOf("not:exist")))
        }
        assertThat(ex.statusCode.value()).isEqualTo(400)
        assertThat(ex.body.detail).isEqualTo("未知的权限编码：not:exist")
    }

    @Test
    fun `missing role throws not found`() {
        val getEx = assertThrows<BaseException> { roleService.get(-1) }
        assertThat(getEx.statusCode.value()).isEqualTo(404)
        assertThat(getEx.body.detail).isEqualTo("角色不存在：-1")

        val deleteEx = assertThrows<BaseException> { roleService.delete(-1) }
        assertThat(deleteEx.statusCode.value()).isEqualTo(404)
    }

    /**
     * 权限按角色解析并缓存：修改角色权限后缓存失效，已签发的 token 无需重新登录即可拿到新权限。
     */
    @Test
    fun `permission change takes effect without new token`() {
        val reader = roleService.list().first { it.code == "reader" }
        roleService.update(reader.id, RoleForm("reader", reader.name, listOf("book:read", "book:write")))
        assertThat(rolePermissionResolver.resolve(listOf("reader")))
            .containsExactly("book:read", "book:write")
    }
}
