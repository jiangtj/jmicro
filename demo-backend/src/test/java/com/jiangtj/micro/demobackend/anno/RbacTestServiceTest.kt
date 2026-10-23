package com.jiangtj.micro.demobackend.anno

import com.jiangtj.micro.auth.exceptions.NoPermissionException
import com.jiangtj.micro.auth.exceptions.NoRoleException
import com.jiangtj.micro.auth.exceptions.UnLoginException
import com.jiangtj.micro.demobackend.WithMockAdmin
import com.jiangtj.micro.test.JMicroTest
import com.jiangtj.micro.test.WithMockPermission
import com.jiangtj.micro.test.WithMockRole
import com.jiangtj.micro.test.WithMockUser
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

@JMicroTest
class RbacTestServiceTest {

    @Autowired
    lateinit var rbacTestService: RbacTestService

    @Test
    fun testNoLogin() {
        assertThrows<UnLoginException> { rbacTestService.hasLogin() }
        assertThrows<UnLoginException> { rbacTestService.hasRoleA() }
        assertThrows<UnLoginException> { rbacTestService.hasPermissionB() }
    }

    @Test
    @WithMockUser
    fun testOnlyLogin() {
        assertDoesNotThrow { rbacTestService.hasLogin() }
        assertThrows<NoRoleException> { rbacTestService.hasRoleA() }
        assertThrows<NoPermissionException> { rbacTestService.hasPermissionB() }
    }

    @Test
    @WithMockRole("roleA")
    @WithMockPermission("permissionA")
    fun testHasA() {
        assertDoesNotThrow { rbacTestService.hasLogin() }
        assertDoesNotThrow { rbacTestService.hasRoleA() }
        assertThrows<NoPermissionException> { rbacTestService.hasPermissionB() }
    }

    @Test
    @WithMockRole("roleB")
    @WithMockPermission("permissionB")
    fun testHasB() {
        assertDoesNotThrow { rbacTestService.hasLogin() }
        assertThrows<NoRoleException> { rbacTestService.hasRoleA() }
        assertDoesNotThrow { rbacTestService.hasPermissionB() }
    }

    @Test
    @WithMockUser(subject = "1", roles = ["roleA"], permissions = ["permissionA"])
    @WithMockRole("roleB")
    @WithMockPermission("permissionB")
    fun testOver() {
        assertDoesNotThrow { rbacTestService.hasLogin() }
        assertThrows<NoRoleException> { rbacTestService.hasRoleA() }
        assertDoesNotThrow { rbacTestService.hasPermissionB() }
        assertThrows<NoRoleException> { rbacTestService.hasAdminOrUser() }
    }

    @Test
    @WithMockAdmin
    fun testCustomAnno() {
        assertDoesNotThrow { rbacTestService.hasLogin() }
        assertDoesNotThrow { rbacTestService.hasAdmin() }
        assertThrows<NoRoleException> { rbacTestService.hasRoleA() }
        assertDoesNotThrow { rbacTestService.hasAdminOrUser() }
    }

    @Test
    @WithMockRole("user")
    fun testUser() {
        assertDoesNotThrow { rbacTestService.hasAdminOrUser() }
    }

    @Test
    @WithMockPermission("a:*")
    fun testAntPermission() {
        assertDoesNotThrow { rbacTestService.hasAntPermissionAA() }
        assertThrows<NoPermissionException> { rbacTestService.hasAntPermissionAAA() }
    }
}
