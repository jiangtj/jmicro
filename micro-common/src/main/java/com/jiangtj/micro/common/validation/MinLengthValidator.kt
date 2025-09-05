package com.jiangtj.micro.common.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class MinLengthValidator : ConstraintValidator<MinLength, String?> {
    private var min = 0
    private var trim = false

    override fun initialize(constraintAnnotation: MinLength) {
        this.min = constraintAnnotation.value
        this.trim = constraintAnnotation.trim
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        var value = value ?: return true
        if (trim) {
            value = value.trim()
        }
        val length = value.length
        return length >= min
    }
}
