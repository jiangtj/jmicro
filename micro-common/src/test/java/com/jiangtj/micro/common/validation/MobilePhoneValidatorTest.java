package com.jiangtj.micro.common.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.Data;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MobilePhoneValidatorTest {

    @Data
    static class User {
        @MobilePhone
        private String phone;
    }

    @Test
    void testString() {
        Validator validator;
        try (ValidatorFactory factory = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()) {
            validator = factory.getValidator();
            User user = new User();
            user.setPhone("13212341234");
            Set<ConstraintViolation<User>> validate1 = validator.validate(user);
            assertTrue(validate1.isEmpty());
            user.setPhone("21111");
            Set<ConstraintViolation<User>> validate2 = validator.validate(user);
            assertFalse(validate2.isEmpty());
        }
    }

    @Data
    static class UserNumber {
        @MobilePhone
        private Long phone;
    }

    @Test
    void testNumber() {
        Validator validator;
        try (ValidatorFactory factory = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()) {
            validator = factory.getValidator();
            UserNumber user = new UserNumber();
            user.setPhone(13212341234L);
            Set<ConstraintViolation<UserNumber>> validate1 = validator.validate(user);
            assertTrue(validate1.isEmpty());
            user.setPhone(211L);
            Set<ConstraintViolation<UserNumber>> validate2 = validator.validate(user);
            assertFalse(validate2.isEmpty());
        }
    }

}