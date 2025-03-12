package com.jiangtj.micro.auth.core;

import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;
import reactor.core.publisher.Mono;

public interface AuthReactiveService {
    Mono<Subject> getSubject();
    Mono<Authorization> getAuthorization();
    Mono<Void> hasLogin();
    Mono<Void> hasRole(String... roles);
    Mono<Void> hasPermission(String... permissions);
}
