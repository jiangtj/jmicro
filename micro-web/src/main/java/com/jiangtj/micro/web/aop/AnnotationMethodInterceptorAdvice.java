package com.jiangtj.micro.web.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public abstract class AnnotationMethodInterceptorAdvice<A extends Annotation> implements MethodInterceptor {

    abstract public Class<A> getAnnotationType();

    @Nullable
    abstract public Object invoke(List<A> annotations, MethodInvocation invocation) throws Throwable;

    @Override
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<A> annotationType = getAnnotationType();

        Method method = invocation.getMethod();
        Object target = invocation.getThis();

        List<A> annotation = AopAnnotationUtils.findAnnotation(annotationType, method, target);
        return invoke(annotation, invocation);
    }

}
