package com.jiangtj.micro.demobackend.validation;

import com.jiangtj.micro.test.JMicroMvcTest;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@JMicroMvcTest
class ValidatorTest {

    @Resource
    Validator validator;

    @Test
    void testNest() {
        NestDto object = new NestDto();
        object.setNest(new NestDto.Example1());
        Set<ConstraintViolation<NestDto>> validate = validator.validate(object);
        assertEquals(2, validate.size());
    }
}
