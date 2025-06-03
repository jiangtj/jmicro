package com.jiangtj.micro.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = MaxLengthValidator.class)
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface MaxLength {
    int value();

    boolean trim() default true;

    String message() default "最小字符串长度不能大于 {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
