package com.jiangtj.micro.auth.context;


import com.jiangtj.micro.auth.exceptions.AuthExceptionUtils;

public class UnLoginContextImpl implements AuthContext {

    public static UnLoginContextImpl self = new UnLoginContextImpl();

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public Subject subject() {
        throw AuthExceptionUtils.unLogin();
    }

    @Override
    public Authorization authorization() {
        throw AuthExceptionUtils.unLogin();
    }

    @Override
    public void setAuthorization(Authorization authorization) {
        throw AuthExceptionUtils.unLogin();
    }

}
