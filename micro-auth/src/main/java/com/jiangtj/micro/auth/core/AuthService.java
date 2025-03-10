package com.jiangtj.micro.auth.core;

import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;

public interface AuthService {
    Subject getSubject();
    Authorization getAuthorization();
    void hasLogin();
    void hasRole(String... roles);
    void hasPermission(String... permissions);
}
