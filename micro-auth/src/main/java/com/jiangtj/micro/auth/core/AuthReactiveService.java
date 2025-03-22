package com.jiangtj.micro.auth.core;

import com.jiangtj.micro.auth.annotations.Logic;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;
import reactor.core.publisher.Mono;

public interface AuthReactiveService {
    Mono<AuthContext> getContext();

    default Mono<Subject> getSubject() {
        return getContext().map(AuthContext::subject);
    }

    default Mono<Authorization> getAuthorization() {
        return getContext().map(AuthContext::authorization);
    }

    default Mono<Void> hasLogin() {
        return getContext()
            .doOnNext(AuthUtils::hasLogin)
            .then();
    }

    default Mono<Void> hasSubject(Subject subject) {
        return getContext()
            .doOnNext(ctx -> AuthUtils.hasSubject(ctx, subject))
            .then();
    }

    default Mono<Void> hasRole(String... roles) {
        return hasRole(Logic.AND, roles);
    }

    default Mono<Void> hasRole(Logic logic, String... roles) {
        return getContext()
            .cast(AuthContext.class)
            .doOnNext(ctx -> AuthUtils.hasRole(ctx, logic, roles))
            .then();
    }

    default Mono<Void> hasPermission(String... permissions) {
        return hasPermission(Logic.AND, permissions);
    }

    default Mono<Void> hasPermission(Logic logic, String... permissions) {
        return getContext()
            .doOnNext(ctx -> AuthUtils.hasPermission(ctx, logic, permissions))
            .then();
    }
}
