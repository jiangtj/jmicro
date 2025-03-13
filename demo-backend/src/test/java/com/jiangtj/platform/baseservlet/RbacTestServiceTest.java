package com.jiangtj.platform.baseservlet;

import com.jiangtj.micro.auth.exceptions.NoPermissionException;
import com.jiangtj.micro.auth.exceptions.NoRoleException;
import com.jiangtj.micro.auth.exceptions.UnLoginException;
import com.jiangtj.micro.test.JMicroTest;
import com.jiangtj.micro.test.WithMockPermission;
import com.jiangtj.micro.test.WithMockRole;
import com.jiangtj.micro.test.WithMockUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@JMicroTest
class RbacTestServiceTest {

    @Resource
    RbacTestService rbacTestService;

    @Test
    void testNoLogin() {
        assertThrows(UnLoginException.class, () -> rbacTestService.hasLogin());
        assertThrows(UnLoginException.class, () -> rbacTestService.hasRoleA());
        assertThrows(UnLoginException.class, () -> rbacTestService.hasPermissionB());
    }

    @Test
    @WithMockUser
    void testOnlyLogin() {
        assertDoesNotThrow(() -> rbacTestService.hasLogin());
        assertThrows(NoRoleException.class, () -> rbacTestService.hasRoleA());
        assertThrows(NoPermissionException.class, () -> rbacTestService.hasPermissionB());
    }

    @Test
    @WithMockRole("roleA")
    @WithMockPermission("permissionA")
    void testHasA() {
        assertDoesNotThrow(() -> rbacTestService.hasLogin());
        assertDoesNotThrow(() -> rbacTestService.hasRoleA());
        assertThrows(NoPermissionException.class, () -> rbacTestService.hasPermissionB());
    }

    @Test
    @WithMockRole("roleB")
    @WithMockPermission("permissionB")
    void testHasB() {
        assertDoesNotThrow(() -> rbacTestService.hasLogin());
        assertThrows(NoRoleException.class, () -> rbacTestService.hasRoleA());
        assertDoesNotThrow(() -> rbacTestService.hasPermissionB());
    }

    @Test
    @WithMockUser(subject = "1", roles = {"roleA"}, permissions = {"permissionA"})
    @WithMockRole("roleB")
    @WithMockPermission("permissionB")
    void testOver() {
        assertDoesNotThrow(() -> rbacTestService.hasLogin());
        assertThrows(NoRoleException.class, () -> rbacTestService.hasRoleA());
        assertDoesNotThrow(() -> rbacTestService.hasPermissionB());
        assertThrows(NoRoleException.class, () -> rbacTestService.hasAdminOrUser());
    }

    @Test
    @WithMockAdmin
    void testCustomAnno() {
        assertDoesNotThrow(() -> rbacTestService.hasLogin());
        assertDoesNotThrow(() -> rbacTestService.hasAdmin());
        assertThrows(NoRoleException.class, () -> rbacTestService.hasRoleA());
        assertDoesNotThrow(() -> rbacTestService.hasAdminOrUser());
    }

    @Test
    @WithMockRole("user")
    void testUser() {
        assertDoesNotThrow(() -> rbacTestService.hasAdminOrUser());
    }
}