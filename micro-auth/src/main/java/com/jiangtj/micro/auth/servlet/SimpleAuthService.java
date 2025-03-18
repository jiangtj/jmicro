package com.jiangtj.micro.auth.servlet;

import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.core.AuthService;
import jakarta.annotation.Resource;

public class SimpleAuthService implements AuthService {
    @Resource
    private AuthHolder authHolder;

    @Override
    public AuthContext getContext() {
        return authHolder.getAuthContext();
    }
}
