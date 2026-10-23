package com.jiangtj.micro.demobackend;

import com.jiangtj.micro.auth.annotations.HasRole;
import com.jiangtj.micro.auth.annotations.Logic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HasRole(value = {"admin", "user"}, logic = Logic.OR)
public @interface HasAdminOrUser {
}
