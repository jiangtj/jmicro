package com.jiangtj.micro.common.form;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface FormRuleHandler<A extends Annotation> {
    Class<A> getAnnotation();

    FormRule handle(Field field, A element);
}
