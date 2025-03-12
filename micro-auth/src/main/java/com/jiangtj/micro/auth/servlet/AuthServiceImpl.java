package com.jiangtj.micro.auth.servlet;

import com.jiangtj.micro.auth.annotations.Logic;
import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;
import com.jiangtj.micro.auth.core.AuthService;
import com.jiangtj.micro.auth.core.AuthUtils;
import jakarta.annotation.Resource;

public class AuthServiceImpl implements AuthService {
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
    public void hasRole(Logic logic, String... roles) {
        AuthUtils.hasRole(authHolder.getAuthContext(), logic, roles);
    }

    @Override
    public void hasPermission(Logic logic, String... permissions) {
        AuthUtils.hasPermission(authHolder.getAuthContext(), logic, permissions);

    }
}
