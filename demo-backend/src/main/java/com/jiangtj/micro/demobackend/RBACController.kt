package com.jiangtj.micro.demobackend;

import com.jiangtj.micro.auth.annotations.HasLogin;
import com.jiangtj.micro.auth.annotations.HasPermission;
import com.jiangtj.micro.auth.annotations.HasRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/anno")
public class RBACController {

    @HasLogin
    @GetMapping("/hasLogin")
    public String hasLogin(){
        return "hasLogin !!";
    }

    @HasRole("roleA")
    @GetMapping("/hasRoleA")
    public String hasRoleA(){
        return "hasRoleA !!";
    }

    @HasPermission("hasPermissionB")
    @GetMapping("/hasPermissionB")
    public String hasPermissionB(){
        return "hasPermissionB !!";
    }

}
