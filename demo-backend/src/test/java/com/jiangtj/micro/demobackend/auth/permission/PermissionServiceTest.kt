package com.jiangtj.micro.demobackend.auth.permission

import com.jiangtj.micro.test.JMicroTest
import com.jiangtj.micro.test.WithMockUser
import com.jiangtj.micro.web.BaseException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

/**
 * 权限字典 Service 集成测试（数据由可重复迁移脚本 `R__sys_permission.sql` 维护）。
 */
@JMicroTest
@WithMockUser(roles = ["admin"])
class PermissionServiceTest {

    @Autowired
    lateinit var permissionService: PermissionService

    @Test
    fun `list returns permissions ordered by code`() {
        assertThat(permissionService.list(null).map { it.code })
            .containsExactly("book", "book:read", "book:write")
    }

    @Test
    fun `list filters by code or name ignoring case`() {
        assertThat(permissionService.list("write").map { it.code }).containsExactly("book:write")
        assertThat(permissionService.list("查看").map { it.code }).containsExactly("book:read")
        assertThat(permissionService.list("  ").map { it.code })
            .containsExactly("book", "book:read", "book:write")
    }

    @Test
    fun `get returns permission`() {
        val permission = permissionService.get("book:read")
        assertThat(permission.code).isEqualTo("book:read")
        assertThat(permission.name).isEqualTo("图书查看权限")
    }

    @Test
    fun `get missing permission throws not found`() {
        val ex = assertThrows<BaseException> { permissionService.get("not:exist") }
        assertThat(ex.statusCode.value()).isEqualTo(404)
        assertThat(ex.body.detail).isEqualTo("权限不存在：not:exist")
    }
}
