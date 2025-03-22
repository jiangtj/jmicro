package com.jiangtj.micro.demoreactive.anno;

import com.jiangtj.micro.auth.annotations.HasLogin;
import com.jiangtj.micro.auth.annotations.HasPermission;
import com.jiangtj.micro.auth.annotations.HasRole;
import com.jiangtj.micro.auth.annotations.Logic;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RbacTestService {

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
}
