package com.jiangtj.micro.auth.reactive;

import com.jiangtj.micro.auth.KeyUtils;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.exceptions.AuthExceptionUtils;
import com.jiangtj.micro.web.BaseExceptionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

public interface AuthReactorUtils {

    static <T> Function<T, Mono<T>> tokenTypeInterceptor(Class<?> type) {
        return t -> isTokenType(type).thenReturn(t);
    }

    static Mono<AuthContext> isTokenType(Class<?> type) {
        return AuthReactorHolder.deferAuthContext()
            .flatMap(tokenTypeHandler(type));
    }

    static Function<AuthContext, Mono<AuthContext>> tokenTypeHandler(Class<?> type) {
        return ctx -> {
            if (!type.isInstance(ctx)) {
                return Mono.error(BaseExceptionUtils.forbidden("不允许访问 todo"));
            }
            return Mono.just(ctx);
        };
    }

    static <T> Function<T, Mono<T>> loginInterceptor() {
        return t -> hasLogin().thenReturn(t);
    }

    static <T> Mono<T> hasLogin(T val) {
        return hasLogin().then(Mono.just(val));
    }

    static Mono<AuthContext> hasLogin() {
        return AuthReactorHolder.deferAuthContext()
            .flatMap(AuthReactorUtils.hasLoginHandler());
    }

    static Function<AuthContext, Mono<AuthContext>> hasLoginHandler() {
        return ctx -> {
            if (!ctx.isLogin()) {
                return Mono.error(AuthExceptionUtils.unLogin());
            }
            return Mono.just(ctx);
        };
    }

    static <T> Function<T, Mono<T>> roleInterceptor(String... roles) {
        return t -> hasLogin(roles).thenReturn(t);
    }

    static Mono<AuthContext> hasRole(String... roles) {
        return AuthReactorHolder.deferAuthContext()
            .cast(AuthContext.class)
            .flatMap(hasRoleHandler(roles));
    }

    static Function<AuthContext, Mono<AuthContext>> hasRoleHandler(String... roles) {
        return ctx -> {
            List<String> userRoles = ctx.authorization().roles();
            return Flux.just(roles)
                .map(KeyUtils::toKey)
                .doOnNext(role -> {
                    if (!userRoles.contains(role)) {
                        throw AuthExceptionUtils.noRole(role);
                    }
                })
                .then(Mono.just(ctx));
        };
    }

    static <T> Function<T, Mono<T>> permissionInterceptor(String... permissions) {
        return t -> hasPermission(permissions).thenReturn(t);
    }

    static Mono<AuthContext> hasPermission(String... permissions) {
        return AuthReactorHolder.deferAuthContext()
            .flatMap(hasPermissionHandler(permissions));
    }

    static Function<AuthContext, Mono<AuthContext>> hasPermissionHandler(String... permissions) {
        return ctx -> {
            List<String> userPermissions = ctx.authorization().permissions();
            return Flux.just(permissions)
                .map(KeyUtils::toKey)
                .doOnNext(perm -> {
                    if (!userPermissions.contains(perm)) {
                        throw AuthExceptionUtils.noPermission(perm);
                    }
                })
                .then(Mono.just(ctx));
        };
    }

}
