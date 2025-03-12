package com.jiangtj.micro.auth.core;

import com.jiangtj.micro.auth.annotations.Logic;
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

    static void hasRole(AuthContext ctx, Logic logic, String... roles) {
        List<String> userRoles = ctx.authorization().roles();
        if (logic == Logic.AND) {
            Stream.of(roles)
                .forEach(role -> {
                    if (!userRoles.contains(role)) {
                        throw AuthExceptionUtils.noRole(role);
                    }
                });
        } else {
            if (Stream.of(roles).noneMatch(userRoles::contains)) {
                throw AuthExceptionUtils.noRole(String.join(",", roles));
            }
        }
    }

    static void hasPermission(AuthContext ctx, Logic logic, String... permissions) {
        List<String> userPermissions = ctx.authorization().permissions();
        if (logic == Logic.AND) {
            Stream.of(permissions)
                .forEach(perm -> {
                    if (!userPermissions.contains(perm)) {
                        throw AuthExceptionUtils.noPermission(perm);
                    }
                });
        } else {
            if (Stream.of(permissions).noneMatch(userPermissions::contains)) {
                throw AuthExceptionUtils.noPermission(String.join(",", permissions));
            }
        }
    }

}
