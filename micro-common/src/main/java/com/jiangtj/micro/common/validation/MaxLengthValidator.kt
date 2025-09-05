package com.jiangtj.micro.common.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class MaxLengthValidator : ConstraintValidator<MaxLength, String?> {
    private var max = 0
    private var trim = false

    override fun initialize(constraintAnnotation: MaxLength) {
        this.max = constraintAnnotation.value
        this.trim = constraintAnnotation.trim
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        var value = value ?: return true
        if (trim) {
            value = value.trim()
        }
        val length = value.length
        return length <= max
    }
}
