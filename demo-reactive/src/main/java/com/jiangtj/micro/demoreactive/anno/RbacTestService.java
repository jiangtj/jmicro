package com.jiangtj.micro.demoreactive.anno;

import com.jiangtj.micro.auth.annotations.HasLogin;
import com.jiangtj.micro.auth.annotations.HasPermission;
import com.jiangtj.micro.auth.annotations.HasRole;
import com.jiangtj.micro.auth.annotations.Logic;
import com.jiangtj.micro.auth.core.AuthReactiveService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.jiangtj.micro.auth.reactive.ReactiveHelper.interceptor;

@Service
public class RbacTestService {

    @Resource
    private AuthReactiveService authReactiveService;

    @HasLogin
    public Mono<Void> hasLogin(){
        return Mono.empty();
    }

    @HasRole("roleA")
    public Mono<Void> hasRoleA(){
        return Mono.empty();
    }

    @HasPermission("permissionB")
    public Mono<Void> hasPermissionB(){
        return Mono.empty();
    }

    @HasRole("admin")
    public Mono<Void> hasAdmin(){
        return Mono.empty();
    }

    @HasRole(value = {"admin", "user"}, logic = Logic.OR)
    public Mono<Void> hasAdminOrUser(){
        return Mono.empty();
    }

    public Mono<String> hasAdminOrUserP(Mono<String> mono) {
        return mono
            .flatMap(interceptor(authReactiveService.hasRole(Logic.OR, "admin", "user")))
            .map(s -> s + "result");
    }
}
