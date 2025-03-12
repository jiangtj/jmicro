package com.jiangtj.micro.auth.core;

import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.exceptions.AuthExceptionUtils;

import java.util.List;
import java.util.stream.Stream;

public interface AuthUtils {

    static void hasLogin(AuthContext ctx) {
        if (!ctx.isLogin()) {
            throw AuthExceptionUtils.unLogin();
        }
    }

    static void hasRole(AuthContext ctx, String... roles) {
        List<String> userRoles = ctx.authorization().roles();
        Stream.of(roles)
            .forEach(role -> {
                if (!userRoles.contains(role)) {
                    throw AuthExceptionUtils.noRole(role);
                }
            });
    }

    static void hasPermission(AuthContext ctx, String... permissions) {
        List<String> userPermissions = ctx.authorization().permissions();
        Stream.of(permissions)
            .forEach(perm -> {
                if (!userPermissions.contains(perm)) {
                    throw AuthExceptionUtils.noPermission(perm);
                }
            });
    }

}
