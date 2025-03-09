package com.jiangtj.micro.auth.servlet.rbac;

import com.jiangtj.micro.auth.annotations.TokenType;
import com.jiangtj.micro.auth.servlet.AuthUtils;
import com.jiangtj.micro.web.aop.AnnotationMethodBeforeAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class HasTokenTypeAdvice extends AnnotationMethodBeforeAdvice<TokenType> implements Ordered {

    @Override
    public Class<TokenType> getAnnotationType() {
        return TokenType.class;
    }

    @Override
    public void before(List<TokenType> annotations, Method method, Object[] args, Object target) {
        for (TokenType annotation : annotations) {
            AuthUtils.isTokenType(annotation.value());
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
