package com.jiangtj.micro.auth.core;

import com.jiangtj.micro.auth.annotations.Logic;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;

public interface AuthService {
    AuthContext getContext();

    default Subject getSubject(){
        return getContext().subject();
    }

    default Authorization getAuthorization(){
        return getContext().authorization();
    }

    default void hasLogin() {
        AuthUtils.hasLogin(getContext());
    }

    default void hasSubject(Subject subject) {
        AuthUtils.hasSubject(getContext(), subject);
    }

    default void hasRole(String... roles) {
        hasRole(Logic.AND, roles);
    }

    default void hasRole(Logic logic, String... roles) {
        AuthUtils.hasRole(getContext(), logic, roles);
    }

    default void hasPermission(String... permissions) {
        hasPermission(Logic.AND, permissions);
    }

    default void hasPermission(Logic logic, String... permissions) {
        AuthUtils.hasPermission(getContext(), logic, permissions);
    }
}
