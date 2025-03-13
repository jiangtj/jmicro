package com.jiangtj.platform.basereactive;

import com.jiangtj.micro.auth.exceptions.NoPermissionException;
import com.jiangtj.micro.auth.exceptions.NoRoleException;
import com.jiangtj.micro.auth.exceptions.UnLoginException;
import com.jiangtj.micro.test.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;

@Slf4j
@JMicroTest
@AutoConfigureWebTestClient
class RbacTestServiceTest {

    @Resource
    RbacTestService rbacTestService;

    @Test
    void testNoLogin() {
        AuthStepVerifier.create(rbacTestService.hasLogin())
            .expectError(UnLoginException.class)
            .verify();
        AuthStepVerifier.create(rbacTestService.hasRoleA())
            .expectError(UnLoginException.class)
            .verify();
        AuthStepVerifier.create(rbacTestService.hasPermissionB())
            .expectError(UnLoginException.class)
            .verify();
    }

    @Test
    @WithMockUser
    void testOnlyLogin() {
        AuthStepVerifier.create(rbacTestService.hasLogin())
            .expectComplete()
            .verify();
        AuthStepVerifier.create(rbacTestService.hasRoleA())
            .expectError(NoRoleException.class)
            .verify();
        AuthStepVerifier.create(rbacTestService.hasPermissionB())
            .expectError(NoPermissionException.class)
            .verify();
    }

    @Test
    @WithMockRole("roleA")
    @WithMockPermission("permissionA")
    void testHasA() {
        AuthStepVerifier.create(rbacTestService.hasLogin())
            .expectComplete()
            .verify();
        AuthStepVerifier.create(rbacTestService.hasRoleA())
            .expectComplete()
            .verify();
        AuthStepVerifier.create(rbacTestService.hasPermissionB())
            .expectError(NoPermissionException.class)
            .verify();
    }

    @Test
    @WithMockRole("roleB")
    @WithMockPermission("permissionB")
    void testHasB() {
        AuthStepVerifier.create(rbacTestService.hasLogin())
            .expectComplete()
            .verify();
        AuthStepVerifier.create(rbacTestService.hasRoleA())
            .expectError(NoRoleException.class)
            .verify();
        AuthStepVerifier.create(rbacTestService.hasPermissionB())
            .expectComplete()
            .verify();
    }

    @Test
    @WithMockUser(subject = "1", roles = {"roleA"}, permissions = {"permissionA"})
    @WithMockRole("roleB")
    @WithMockPermission("permissionB")
    void testOver() {
        AuthStepVerifier.create(rbacTestService.hasLogin())
            .expectComplete()
            .verify();
        AuthStepVerifier.create(rbacTestService.hasRoleA())
            .expectError(NoRoleException.class)
            .verify();
        AuthStepVerifier.create(rbacTestService.hasPermissionB())
            .expectComplete()
            .verify();
        AuthStepVerifier.create(rbacTestService.hasAdminOrUser())
            .expectError(NoRoleException.class)
            .verify();
    }

    @Test
    @WithMockAdmin
    void testCustomAnno() {
        AuthStepVerifier.create(rbacTestService.hasLogin())
            .expectComplete()
            .verify();
        AuthStepVerifier.create(rbacTestService.hasAdmin())
            .expectComplete()
            .verify();
        AuthStepVerifier.create(rbacTestService.hasRoleA())
            .expectError(NoRoleException.class)
            .verify();
        AuthStepVerifier.create(rbacTestService.hasAdminOrUser())
            .expectComplete()
            .verify();
    }

    @Test
    @WithMockRole("user")
    void testUser() {
        AuthStepVerifier.create(rbacTestService.hasAdminOrUser())
            .expectComplete()
            .verify();
    }
}