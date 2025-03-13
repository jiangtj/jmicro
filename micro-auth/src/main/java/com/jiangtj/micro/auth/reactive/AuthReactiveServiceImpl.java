package com.jiangtj.micro.auth.reactive;

import com.jiangtj.micro.auth.annotations.Logic;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;
import com.jiangtj.micro.auth.core.AuthReactiveService;
import com.jiangtj.micro.auth.core.AuthUtils;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class AuthReactiveServiceImpl implements AuthReactiveService {

    @Override
    public Mono<Subject> getSubject() {
        return AuthReactiveHolder.deferAuthContext()
            .map(AuthContext::subject);
    }

    @Override
    public Mono<Authorization> getAuthorization() {
        return AuthReactiveHolder.deferAuthContext()
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
        return AuthReactiveHolder.deferAuthContext()
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
    public Mono<Void> hasRole(Logic logic, String... roles) {
        return AuthReactiveHolder.deferAuthContext()
            .cast(AuthContext.class)
            .flatMap(hasRoleHandler(logic, roles))
            .then();
    }

    public Function<AuthContext, Mono<AuthContext>> hasRoleHandler(Logic logic, String... roles) {
        return ctx -> {
            AuthUtils.hasRole(ctx, logic, roles);
            return Mono.just(ctx);
        };
    }

    public <T> Function<T, Mono<T>> permissionInterceptor(String... permissions) {
        return t -> hasPermission(permissions).thenReturn(t);
    }

    @Override
    public Mono<Void> hasPermission(Logic logic, String... permissions) {
        return AuthReactiveHolder.deferAuthContext()
            .flatMap(hasPermissionHandler(logic, permissions))
            .then();
    }

    public Function<AuthContext, Mono<AuthContext>> hasPermissionHandler(Logic logic, String... permissions) {
        return ctx -> {
            AuthUtils.hasPermission(ctx, logic, permissions);
            return Mono.just(ctx);
        };
    }

}
