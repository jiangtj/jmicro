package com.jiangtj.micro.demoreactive;

import com.jiangtj.micro.test.WithMockUser;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@WithMockUser(subject = "admin", roles = {"admin"})
public @interface WithMockAdmin {
}
