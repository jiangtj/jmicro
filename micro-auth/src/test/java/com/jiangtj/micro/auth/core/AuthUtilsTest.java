package com.jiangtj.micro.auth.core;

import com.jiangtj.micro.auth.annotations.Logic;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;
import com.jiangtj.micro.auth.exceptions.NoPermissionException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthUtilsTest {

    @Test
    void hasAntPermission() {
        AuthContext ctx = createPermissionContext("a:*", "b:**", "c:*:c");
        assertDoesNotThrow(() -> AuthUtils.hasAntPermission(ctx, Logic.AND, "a:a"));
        assertThrows(NoPermissionException.class, () -> AuthUtils.hasAntPermission(ctx, Logic.AND, "a:a:a"));
        assertDoesNotThrow(() -> AuthUtils.hasAntPermission(ctx, Logic.AND, "b:b"));
        assertDoesNotThrow(() -> AuthUtils.hasAntPermission(ctx, Logic.AND, "b:b:b"));
        assertThrows(NoPermissionException.class, () -> AuthUtils.hasAntPermission(ctx, Logic.AND, "c:c"));
        assertDoesNotThrow(() -> AuthUtils.hasAntPermission(ctx, Logic.AND, "c:c:c"));

        assertDoesNotThrow(() -> AuthUtils.hasAntPermission(ctx, Logic.AND, "a:a", "b:b", "b:b:b", "c:c:c"));
        assertDoesNotThrow(() -> AuthUtils.hasAntPermission(ctx, Logic.OR, "a:a:a", "b:b", "c:c"));
        assertThrows(NoPermissionException.class, () -> AuthUtils.hasAntPermission(ctx, Logic.AND, "a:a", "b:b", "b:b:b", "c:c", "c:c:c"));
        assertThrows(NoPermissionException.class, () -> AuthUtils.hasAntPermission(ctx, Logic.OR, "a:a:a", "c:c"));
    }


    AuthContext createPermissionContext(String... permissions) {
        return AuthContext.create(new Subject(), Authorization.create(List.of(), List.of(permissions)));
    }
}