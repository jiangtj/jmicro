package com.jiangtj.micro.demobackend.validation

import com.jiangtj.micro.test.JMicroMvcTest
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@JMicroMvcTest
class ValidatorTest {

    @Autowired
    lateinit var validator: Validator

    @Test
    fun testNest() {
        val obj = NestDto(nest = NestDto.Example1())
        assertEquals(2, validator.validate(obj).size)
    }
}
