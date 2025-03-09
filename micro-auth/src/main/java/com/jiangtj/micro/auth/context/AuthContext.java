package com.jiangtj.micro.auth.context;

/**
 * Auth 上下文
 */
public interface AuthContext {
    Subject subject();
    Authorization authorization();
    void setAuthorization(Authorization authorization);

    static AuthContext create(Subject subject) {
        return new DefaultAuthContext(subject);
    }

    static AuthContext create(Subject subject, Authorization authorization) {
        return new DefaultAuthContext(subject, authorization);
    }
}
