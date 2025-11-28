package com.jiangtj.micro.web.aop;

import org.jspecify.annotations.Nullable;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public abstract class AnnotationMethodBeforeAdvice<A extends Annotation> implements MethodBeforeAdvice {

    abstract public Class<A> getAnnotationType();

    abstract public void before(List<A> annotations, Method method, @Nullable Object[] args,  @Nullable Object target);

    @Override
    public void before(Method method, @Nullable Object[] args, @Nullable Object target) throws Throwable {
        Class<A> annotationType = getAnnotationType();
        List<A> annotations = AopAnnotationUtils.findAnnotation(annotationType, method, target);
        before(annotations, method, args, target);
    }

}
