package com.jiangtj.micro.auth.core;

import com.jiangtj.micro.auth.annotations.Logic;
import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;

public interface AuthService {
    Subject getSubject();

    Authorization getAuthorization();

    void hasLogin();

    void hasRole(Logic logic, String... roles);

    void hasPermission(Logic logic, String... permissions);

    default void hasRole(String... roles) {
        hasRole(Logic.AND, roles);
    }

    default void hasPermission(String... permissions) {
        hasPermission(Logic.AND, permissions);
    }
}
