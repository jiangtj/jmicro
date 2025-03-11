package com.jiangtj.micro.auth.reactive;

import com.jiangtj.micro.auth.KeyUtils;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.exceptions.AuthExceptionUtils;
import com.jiangtj.micro.web.BaseExceptionUtils;
import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

public class AuthReactorService {

    @Resource
    private AuthReactorHolder authReactorHolder;

    public <T> Function<T, Mono<T>> tokenTypeInterceptor(Class<?> type) {
        return t -> isTokenType(type).thenReturn(t);
    }

    public Mono<AuthContext> isTokenType(Class<?> type) {
        return authReactorHolder.deferAuthContext()
            .flatMap(tokenTypeHandler(type));
    }

    public Function<AuthContext, Mono<AuthContext>> tokenTypeHandler(Class<?> type) {
        return ctx -> {
            if (!type.isInstance(ctx)) {
                return Mono.error(BaseExceptionUtils.forbidden("不允许访问 todo"));
            }
            return Mono.just(ctx);
        };
    }

    public <T> Function<T, Mono<T>> loginInterceptor() {
        return t -> hasLogin().thenReturn(t);
    }

    public <T> Mono<T> hasLogin(T val) {
        return hasLogin().then(Mono.just(val));
    }

    public Mono<AuthContext> hasLogin() {
        return authReactorHolder.deferAuthContext()
            .flatMap(hasLoginHandler());
    }

    public Function<AuthContext, Mono<AuthContext>> hasLoginHandler() {
        return ctx -> {
            if (!ctx.isLogin()) {
                return Mono.error(AuthExceptionUtils.unLogin());
            }
            return Mono.just(ctx);
        };
    }

    public <T> Function<T, Mono<T>> roleInterceptor(String... roles) {
        return t -> hasLogin(roles).thenReturn(t);
    }

    public Mono<AuthContext> hasRole(String... roles) {
        return authReactorHolder.deferAuthContext()
            .cast(AuthContext.class)
            .flatMap(hasRoleHandler(roles));
    }

    public Function<AuthContext, Mono<AuthContext>> hasRoleHandler(String... roles) {
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

    public <T> Function<T, Mono<T>> permissionInterceptor(String... permissions) {
        return t -> hasPermission(permissions).thenReturn(t);
    }

    public Mono<AuthContext> hasPermission(String... permissions) {
        return authReactorHolder.deferAuthContext()
            .flatMap(hasPermissionHandler(permissions));
    }

    public Function<AuthContext, Mono<AuthContext>> hasPermissionHandler(String... permissions) {
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
