package com.jiangtj.micro.test;

import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.RoleProvider;

public class TestAuthContextHolder {

    private static AuthContext ctx = null;
    private static RoleProvider provider = null;

    public static void setAuthContext(AuthContext context) {
        ctx = context;
    }

    public static AuthContext getAuthContext() {
        return ctx;
    }

    public static void clearAuthContext() {
        ctx = null;
    }

    public static RoleProvider getProvider() {
        return provider;
    }

    public static void setProvider(RoleProvider provider) {
        TestAuthContextHolder.provider = provider;
    }

    public static void clearProvider() {
        provider = null;
    }
}
