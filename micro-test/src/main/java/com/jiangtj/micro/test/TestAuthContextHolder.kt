package com.jiangtj.micro.test

import com.jiangtj.micro.auth.context.AuthContext

object TestAuthContextHolder {

    private var ctx: AuthContext? = null

    @JvmStatic
    fun setAuthContext(context: AuthContext?) {
        ctx = context
    }

    @JvmStatic
    fun getAuthContext(): AuthContext? = ctx

    @JvmStatic
    fun clearAuthContext() {
        ctx = null
    }
}
