package com.jiangtj.micro.demobackend

import com.jiangtj.micro.auth.annotations.HasLogin
import com.jiangtj.micro.auth.annotations.HasPermission
import com.jiangtj.micro.auth.annotations.HasRole
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/anno")
class RBACController {

    @HasLogin
    @GetMapping("/hasLogin")
    fun hasLogin(): String = "hasLogin !!"

    @HasRole("roleA")
    @GetMapping("/hasRoleA")
    fun hasRoleA(): String = "hasRoleA !!"

    @HasPermission("hasPermissionB")
    @GetMapping("/hasPermissionB")
    fun hasPermissionB(): String = "hasPermissionB !!"
}
