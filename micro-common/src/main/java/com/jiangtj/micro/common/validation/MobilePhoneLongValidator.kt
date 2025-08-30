package com.jiangtj.micro.common.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class MobilePhoneLongValidator : ConstraintValidator<MobilePhone, Long?> {
    override fun isValid(value: Long?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }
        return value in 10000000000L..<20000000000L
    }
}
