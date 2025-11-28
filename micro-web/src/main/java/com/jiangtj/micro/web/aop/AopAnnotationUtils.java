package com.jiangtj.micro.web.aop;

import com.jiangtj.micro.web.AnnotationUtils;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface AopAnnotationUtils {

    static <A extends Annotation> List<A> findAnnotation(Class<A> aClass, Method method, @Nullable Object target) {
        List<A> list = new ArrayList<>();

        Target targets = aClass.getAnnotation(Target.class);
        ElementType[] elementTypes = targets.value();

        for (ElementType type : elementTypes) {
            if (ElementType.TYPE.equals(type)) {
                Optional.ofNullable(target)
                        .map(Object::getClass)
                        .ifPresent(clz -> AnnotationUtils.streamAnnotations(clz, aClass)
                                .forEach(list::add));
            }
            if (ElementType.METHOD.equals(type)) {
                AnnotationUtils.streamAnnotations(method, aClass)
                        .forEach(list::add);
            }
        }
        return list;
    }

}
