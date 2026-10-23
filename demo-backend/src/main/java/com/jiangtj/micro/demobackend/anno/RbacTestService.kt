package com.jiangtj.micro.demobackend.anno

import com.jiangtj.micro.auth.annotations.HasLogin
import com.jiangtj.micro.auth.annotations.HasPermission
import com.jiangtj.micro.auth.annotations.HasRole
import com.jiangtj.micro.demobackend.HasAdminOrUser
import org.springframework.stereotype.Service

@Service
class RbacTestService {

    @HasLogin
    fun hasLogin(): String = "hasLogin !!"

    @HasRole("roleA")
    fun hasRoleA(): String = "hasRoleA !!"

    @HasPermission("permissionB")
    fun hasPermissionB(): String = "hasPermissionB !!"

    @HasRole("admin")
    fun hasAdmin() {
    }

    @HasAdminOrUser
    fun hasAdminOrUser() {
    }

    @HasPermission("a:a")
    fun hasAntPermissionAA() {
    }

    @HasPermission("a:a:a")
    fun hasAntPermissionAAA() {
    }
}
