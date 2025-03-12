package com.jiangtj.micro.auth.reactive.aop;

import com.jiangtj.micro.web.aop.AopAnnotationUtils;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public abstract class ReactiveAnnotationMethodBeforeAdvice<A extends Annotation> extends ReactiveMethodBeforeAdvice {

    abstract public Class<A> getAnnotationType();

    abstract public Mono<Void> before(List<A> annotations, Method method, Object[] args, @Nullable Object target);

    @Override
    public Mono<Void> before(Method method, Object[] args, @Nullable Object target) {
        Class<A> annotationType = getAnnotationType();
        List<A> annotations = AopAnnotationUtils.findAnnotation(annotationType, method, target);
        return before(annotations, method, args, target);
    }
}
