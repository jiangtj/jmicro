package com.jiangtj.micro.auth.context;

/**
 * Auth 上下文
 */
public interface AuthContext {
    Subject subject();
    Authorization authorization();
    void setAuthorization(Authorization authorization);

    default boolean isLogin() {
        return true;
    }

    static AuthContext unLogin() {
        return UnLoginContextImpl.self;
    }

    static AuthContext create(String subjectId) {
        Subject subject = new Subject();
        subject.setId(subjectId);
        return create(subject);
    }

    static AuthContext create(Subject subject) {
        return new DefaultAuthContext(subject);
    }

    static AuthContext create(String subjectId, Authorization authorization) {
        Subject subject = new Subject();
        subject.setId(subjectId);
        return create(subject, authorization);
    }

    static AuthContext create(Subject subject, Authorization authorization) {
        return new DefaultAuthContext(subject, authorization);
    }
}
