package com.jiangtj.micro.web;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;

@Slf4j
public class SpelUtilsJTest {

    static class TestClass {
        public int test(int a, int b) {
            return a + b;
        }
    }
    @Test
    public void getMethodContext() {
        Method method = null;
        try {
            method = TestClass.class.getMethod("test", int.class, int.class);
            var parser = new SpelExpressionParser();
            var context = SpelUtils.getMethodContext(method, 2, 3);
            log.info("{}", parser.parseExpression("#a + #b").getValue(context));
            log.info("{}", parser.parseExpression("#args[0] + #args[1]").getValue(context));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
