package com.jiangtj.micro.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxLengthValidator implements ConstraintValidator<MaxLength, String> {

    private int max;
    private boolean trim;

    @Override
    public void initialize(MaxLength constraintAnnotation) {
        this.max = constraintAnnotation.value();
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
        return length <= max;
    }
}
