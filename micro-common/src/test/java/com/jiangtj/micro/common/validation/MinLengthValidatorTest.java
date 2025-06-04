package com.jiangtj.micro.common.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.Data;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MinLengthValidatorTest {

    @Data
    static class MinLO {
        @MinLength(5)
        private String name;
    }

    @Test
    void testBase() {
        Validator validator;
        try (ValidatorFactory factory = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()) {
            validator = factory.getValidator();
            MinLO obj = new MinLO();
            obj.setName("12345");
            Set<ConstraintViolation<MinLO>> validate1 = validator.validate(obj);
            assertTrue(validate1.isEmpty());
            obj.setName("123456");
            Set<ConstraintViolation<MinLO>> validate2 = validator.validate(obj);
            assertTrue(validate2.isEmpty());
            obj.setName("1");
            Set<ConstraintViolation<MinLO>> validate3 = validator.validate(obj);
            assertFalse(validate3.isEmpty());
            assertEquals("最小字符串长度不能小于 5", validate3.iterator().next().getMessage());
        }
    }

    @Data
    static class MinLOTrim {
        @MinLength(value = 5, trim = true)
        private String name;
    }

    @Test
    void testTrim() {
        Validator validator;
        try (ValidatorFactory factory = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()) {
            validator = factory.getValidator();
            MinLO obj = new MinLO();
            obj.setName(" 1234");
            Set<ConstraintViolation<MinLO>> validate1 = validator.validate(obj);
            assertTrue(validate1.isEmpty());
            MinLOTrim trim = new MinLOTrim();
            trim.setName(" 1234");
            Set<ConstraintViolation<MinLOTrim>> validate2 = validator.validate(trim);
            assertFalse(validate2.isEmpty());
        }
    }

}