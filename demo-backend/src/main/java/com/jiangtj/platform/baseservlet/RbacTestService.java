package com.jiangtj.platform.baseservlet;

import com.jiangtj.micro.auth.annotations.HasLogin;
import com.jiangtj.micro.auth.annotations.HasPermission;
import com.jiangtj.micro.auth.annotations.HasRole;
import org.springframework.stereotype.Service;

@Service
public class RbacTestService {

    @HasLogin
    public String hasLogin(){
        return "hasLogin !!";
    }

    @HasRole("roleA")
    public String hasRoleA(){
        return "hasRoleA !!";
    }

    @HasPermission("permissionB")
    public String hasPermissionB(){
        return "hasPermissionB !!";
    }

    @HasRole("admin")
    public void hasAdmin(){
    }
}
