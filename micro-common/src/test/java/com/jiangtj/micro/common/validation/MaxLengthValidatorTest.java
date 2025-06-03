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

class MaxLengthValidatorTest {

    @Data
    static class Example {
        @MaxLength(5)
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
            Example obj = new Example();
            obj.setName("1234");
            Set<ConstraintViolation<Example>> validate1 = validator.validate(obj);
            assertTrue(validate1.isEmpty());
            obj.setName("12345");
            Set<ConstraintViolation<Example>> validate2 = validator.validate(obj);
            assertTrue(validate2.isEmpty());
            obj.setName("123456");
            Set<ConstraintViolation<Example>> validate3 = validator.validate(obj);
            assertFalse(validate3.isEmpty());
            assertEquals("最小字符串长度不能大于 5", validate3.iterator().next().getMessage());
        }
    }

    @Data
    static class ExampleTrim {
        @MaxLength(value = 5, trim = false)
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
            Example obj = new Example();
            obj.setName(" 12345");
            Set<ConstraintViolation<Example>> validate1 = validator.validate(obj);
            assertTrue(validate1.isEmpty());
            ExampleTrim trim = new ExampleTrim();
            trim.setName(" 12345");
            Set<ConstraintViolation<ExampleTrim>> validate2 = validator.validate(trim);
            assertFalse(validate2.isEmpty());
        }
    }

}