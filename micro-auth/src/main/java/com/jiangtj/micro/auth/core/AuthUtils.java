package com.jiangtj.micro.auth.core;

import com.jiangtj.micro.auth.annotations.Logic;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.exceptions.AuthExceptionUtils;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Predicate;

public interface AuthUtils {

    static void hasLogin(AuthContext ctx) {
        if (!ctx.isLogin()) {
            throw AuthExceptionUtils.unLogin();
        }
    }

    static void hasRole(AuthContext ctx, Logic logic, String... roles) {
        List<String> userRoles = ctx.authorization().roles();
        List<String> unAuth = predicate(List.of(roles), logic, userRoles::contains);
        if (!unAuth.isEmpty()) {
            throw AuthExceptionUtils.noRole(String.join(",", unAuth));
        }
    }

    static void hasPermission(AuthContext ctx, Logic logic, String... permissions) {
        List<String> userPermissions = ctx.authorization().permissions();
        List<String> unAuth = predicate(List.of(permissions), logic, userPermissions::contains);
        if (!unAuth.isEmpty()) {
            throw AuthExceptionUtils.noPermission(String.join(",", unAuth));
        }
    }

    AntPathMatcher dotMatcher = new AntPathMatcher(":");

    static void hasAntPermission(AuthContext ctx, Logic logic, String... permissions) {
        List<String> userPermissions = ctx.authorization().permissions();
        List<String> unAuth = predicate(List.of(permissions), logic, perm ->
            userPermissions.stream().anyMatch(p -> dotMatcher.match(p, perm)));
        if (!unAuth.isEmpty()) {
            throw AuthExceptionUtils.noPermission(String.join(",", unAuth));
        }
    }

    static <T> List<T> predicate(List<T> list, Logic logic, Predicate<T> predicate) {
        if (logic == Logic.AND) {
            return list.stream().filter(p -> !predicate.test(p)).toList();
        } else {
            boolean anyMatch = list.stream().anyMatch(predicate);
            return anyMatch ? List.of() : list;
        }
    }

}
