package com.jiangtj.micro.test;

import com.jiangtj.micro.auth.context.AuthContext;

public class TestAuthContextHolder {

    private static AuthContext ctx = null;

    public static void setAuthContext(AuthContext context) {
        ctx = context;
    }

    public static AuthContext getAuthContext() {
        return ctx;
    }

    public static void clearAuthContext() {
        ctx = null;
    }
}
