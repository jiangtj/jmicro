package com.jiangtj.micro.auth.servlet;

import com.jiangtj.micro.auth.KeyUtils;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.exceptions.AuthExceptionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public interface AuthUtils {

    static void isTokenType(Class<?> type) {
        AuthContext ctx = AuthHolder.getAuthContext();
        if (!type.isInstance(ctx)) {
            throw AuthExceptionUtils.unSupport();
        }
    }

    static void hasLogin() {
        AuthContext ctx = getLoginAuthContext();
        if (!ctx.isLogin()) {
            throw AuthExceptionUtils.unLogin();
        }
    }

    static AuthContext getLoginAuthContext() {
        AuthContext ctx = AuthHolder.getAuthContext();
        Objects.requireNonNull(ctx);
        return ctx;
    }

    static void hasRole(String... roles) {
        AuthContext ctx = getLoginAuthContext();
        List<String> userRoles = ctx.authorization().roles();
        Stream.of(roles)
            .map(KeyUtils::toKey)
            .forEach(role -> {
                if (!userRoles.contains(role)) {
                    throw AuthExceptionUtils.noRole(role);
                }
            });
    }

    static void hasPermission(String... permissions) {
        AuthContext ctx = getLoginAuthContext();
        List<String> userPermissions = ctx.authorization().permissions();
        Stream.of(permissions)
            .map(KeyUtils::toKey)
            .forEach(perm -> {
                if (!userPermissions.contains(perm)) {
                    throw AuthExceptionUtils.noPermission(perm);
                }
            });
    }

}
