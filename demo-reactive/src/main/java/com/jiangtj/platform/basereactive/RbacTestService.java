package com.jiangtj.platform.basereactive;

import com.jiangtj.micro.auth.annotations.HasLogin;
import com.jiangtj.micro.auth.annotations.HasPermission;
import com.jiangtj.micro.auth.annotations.HasRole;
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
}
