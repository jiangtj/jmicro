package com.jiangtj.micro.auth.core;

import com.jiangtj.micro.auth.annotations.Logic;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.Subject;
import com.jiangtj.micro.auth.exceptions.AuthExceptionUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface AuthUtils {

    static void hasLogin(AuthContext ctx) {
        if (!ctx.isLogin()) {
            throw AuthExceptionUtils.unLogin();
        }
    }

    static void hasSubject(AuthContext ctx, Subject subject) {
        Subject user = ctx.subject();
        checkSubjectAttribute(user, subject, Subject::getId);
        checkSubjectAttribute(user, subject, Subject::getName);
        checkSubjectAttribute(user, subject, Subject::getDisplayName);
        checkSubjectAttribute(user, subject, Subject::getType);
        checkSubjectAttribute(user, subject, Subject::getIssuer);
    }

    static void checkSubjectAttribute(Subject user, Subject subject, Function<Subject, String> getAttr) {
        String subjectAttr = getAttr.apply(subject);
        if (StringUtils.hasLength(subjectAttr)) {
            if (!subjectAttr.equals(getAttr.apply(user))) {
                throw AuthExceptionUtils.unAuthorization("Current subject is not permitted.");
            }
        }
    }

    static void hasRole(AuthContext ctx, Logic logic, String... roles) {
        List<String> userRoles = ctx.authorization().roles();
        List<String> unAuth = nonMatch(List.of(roles), logic, userRoles::contains);
        if (!unAuth.isEmpty()) {
            throw AuthExceptionUtils.noRole(unAuth);
        }
    }

    static void hasPermission(AuthContext ctx, Logic logic, String... permissions) {
        List<String> userPermissions = ctx.authorization().permissions();
        List<String> unAuth = nonMatch(List.of(permissions), logic, userPermissions::contains);
        if (!unAuth.isEmpty()) {
            throw AuthExceptionUtils.noPermission(unAuth);
        }
    }

    AntPathMatcher dotMatcher = new AntPathMatcher(":");

    static void hasAntPermission(AuthContext ctx, Logic logic, String... permissions) {
        hasAntPermission(ctx, dotMatcher, logic, permissions);
    }

    static void hasAntPermission(AuthContext ctx, AntPathMatcher matcher, Logic logic, String... permissions) {
        List<String> userPermissions = ctx.authorization().permissions();
        List<String> unAuth = nonMatch(List.of(permissions), logic, perm ->
            userPermissions.stream().anyMatch(p -> matcher.match(p, perm)));
        if (!unAuth.isEmpty()) {
            throw AuthExceptionUtils.noPermission(unAuth);
        }
    }

    static <T> List<T> nonMatch(List<T> list, Logic logic, Predicate<T> predicate) {
        if (logic == Logic.AND) {
            return list.stream().filter(p -> !predicate.test(p)).toList();
        } else {
            boolean anyMatch = list.stream().anyMatch(predicate);
            return anyMatch ? List.of() : list;
        }
    }

}
