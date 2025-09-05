package com.jiangtj.micro.common.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.util.regex.Pattern

class MobilePhoneStringValidator : ConstraintValidator<MobilePhone, String?> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }
        return p.matcher(value).matches()
    }

    companion object {
        var p: Pattern = Pattern.compile("^1[0-9]{10}$")
    }
}
