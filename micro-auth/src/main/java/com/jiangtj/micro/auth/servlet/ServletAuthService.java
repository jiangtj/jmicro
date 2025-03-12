package com.jiangtj.micro.auth.servlet;

import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;
import com.jiangtj.micro.auth.core.AuthService;
import com.jiangtj.micro.auth.core.AuthUtils;
import jakarta.annotation.Resource;

public class ServletAuthService implements AuthService {
    @Resource
    private AuthHolder authHolder;

    @Override
    public Subject getSubject() {
        return authHolder.getAuthContext().subject();
    }

    @Override
    public Authorization getAuthorization() {
        return authHolder.getAuthContext().authorization();
    }

    @Override
    public void hasLogin() {
        AuthUtils.hasLogin(authHolder.getAuthContext());
    }

    @Override
    public void hasRole(String... roles) {
        AuthUtils.hasRole(authHolder.getAuthContext(), roles);
    }

    @Override
    public void hasPermission(String... permissions) {
        AuthUtils.hasPermission(authHolder.getAuthContext(), permissions);

    }
}
