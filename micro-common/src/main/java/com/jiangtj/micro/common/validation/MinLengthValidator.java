package com.jiangtj.micro.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MinLengthValidator implements ConstraintValidator<MinLength, String> {

    private int min;
    private boolean trim;

    @Override
    public void initialize(MinLength constraintAnnotation) {
        this.min = constraintAnnotation.value();
        this.trim = constraintAnnotation.trim();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (trim) {
            value = value.trim();
        }
        int length = value.length();
        return length >= min;
    }
}
