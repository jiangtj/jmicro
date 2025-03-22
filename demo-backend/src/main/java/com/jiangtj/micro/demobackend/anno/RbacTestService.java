package com.jiangtj.micro.demobackend.anno;

import com.jiangtj.micro.auth.annotations.HasLogin;
import com.jiangtj.micro.auth.annotations.HasPermission;
import com.jiangtj.micro.auth.annotations.HasRole;
import com.jiangtj.micro.demobackend.HasAdminOrUser;
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

    @HasAdminOrUser
    public void hasAdminOrUser(){
    }

    @HasPermission("a:a")
    public void hasAntPermissionAA(){
    }

    @HasPermission("a:a:a")
    public void hasAntPermissionAAA(){
    }
}
