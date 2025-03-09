package com.jiangtj.micro.test;

import com.jiangtj.micro.auth.context.RoleProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithMockRoleProvider {
    Class<? extends RoleProvider> value();
}
