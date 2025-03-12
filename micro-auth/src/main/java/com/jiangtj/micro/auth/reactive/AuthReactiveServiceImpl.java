package com.jiangtj.micro.auth.reactive;

import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;
import com.jiangtj.micro.auth.core.AuthReactiveService;
import com.jiangtj.micro.auth.core.AuthUtils;
import jakarta.annotation.Resource;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class AuthReactiveServiceImpl implements AuthReactiveService {

    @Resource
    private AuthReactiveHolder authReactiveHolder;

    @Override
    public Mono<Subject> getSubject() {
        return authReactiveHolder.deferAuthContext()
            .map(AuthContext::subject);
    }

    @Override
    public Mono<Authorization> getAuthorization() {
        return authReactiveHolder.deferAuthContext()
            .map(AuthContext::authorization);
    }

    public <T> Function<T, Mono<T>> loginInterceptor() {
        return t -> hasLogin().thenReturn(t);
    }

    public <T> Mono<T> hasLogin(T val) {
        return hasLogin().then(Mono.just(val));
    }

    @Override
    public Mono<Void> hasLogin() {
        return authReactiveHolder.deferAuthContext()
            .flatMap(hasLoginHandler())
            .then();
    }

    public Function<AuthContext, Mono<AuthContext>> hasLoginHandler() {
        return ctx -> {
            AuthUtils.hasLogin(ctx);
            return Mono.just(ctx);
        };
    }

    public <T> Function<T, Mono<T>> roleInterceptor(String... roles) {
        return t -> hasLogin(roles).thenReturn(t);
    }

    @Override
    public Mono<Void> hasRole(String... roles) {
        return authReactiveHolder.deferAuthContext()
            .cast(AuthContext.class)
            .flatMap(hasRoleHandler(roles))
            .then();
    }

    public Function<AuthContext, Mono<AuthContext>> hasRoleHandler(String... roles) {
        return ctx -> {
            AuthUtils.hasRole(ctx, roles);
            return Mono.just(ctx);
        };
    }

    public <T> Function<T, Mono<T>> permissionInterceptor(String... permissions) {
        return t -> hasPermission(permissions).thenReturn(t);
    }

    @Override
    public Mono<Void> hasPermission(String... permissions) {
        return authReactiveHolder.deferAuthContext()
            .flatMap(hasPermissionHandler(permissions))
            .then();
    }

    public Function<AuthContext, Mono<AuthContext>> hasPermissionHandler(String... permissions) {
        return ctx -> {
            AuthUtils.hasPermission(ctx, permissions);
            return Mono.just(ctx);
        };
    }

}
