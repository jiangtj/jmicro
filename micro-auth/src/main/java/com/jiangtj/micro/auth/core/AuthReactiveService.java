package com.jiangtj.micro.auth.core;

import com.jiangtj.micro.auth.annotations.Logic;
import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;
import reactor.core.publisher.Mono;

public interface AuthReactiveService {
    Mono<Subject> getSubject();

    Mono<Authorization> getAuthorization();

    Mono<Void> hasLogin();

    Mono<Void> hasRole(Logic logic, String... roles);

    Mono<Void> hasPermission(Logic logic, String... permissions);

    default Mono<Void> hasRole(String... roles) {
        return hasRole(Logic.AND, roles);
    }

    default Mono<Void> hasPermission(String... permissions) {
        return hasPermission(Logic.AND, permissions);
    }
}
