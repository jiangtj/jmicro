package com.jiangtj.micro.auth.servlet;

import com.jiangtj.micro.auth.annotations.Logic;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.core.AuthProperties;
import com.jiangtj.micro.auth.core.AuthService;
import com.jiangtj.micro.auth.core.AuthUtils;
import jakarta.annotation.Resource;
import org.jspecify.annotations.Nullable;
import org.springframework.util.AntPathMatcher;

public class DefaultAuthService implements AuthService {
    @Resource
    private AuthHolder authHolder;
    @Resource
    private AuthProperties properties;

    @Nullable
    private AntPathMatcher matcher;

    @Override
    public AuthContext getContext() {
        return authHolder.getAuthContext();
    }

    @Override
    public void hasPermission(Logic logic, String... permissions) {
        if (properties.getPermissionMatch() == AuthProperties.MatchMode.Simple) {
            AuthUtils.hasPermission(getContext(), logic, permissions);
        } else {
            if (matcher == null) {
                matcher = new AntPathMatcher(properties.getPathSeparator());
            }
            AuthUtils.hasAntPermission(getContext(), matcher, logic, permissions);
        }
    }
}
