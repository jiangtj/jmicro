package com.jiangtj.micro.common.form;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static com.jiangtj.micro.common.form.FormRuleGenerator.findAnnotation;

public interface FormRuleHandler<A extends Annotation> extends BaseHandler {
    Class<A> getAnnotation();

    FormRule handle(Field field, A element);

    @Override
    default FormRule handle(Field field) {
        Class<A> annotation = this.getAnnotation();
        return findAnnotation(field, annotation)
            .map(value -> this.handle(field, value))
            .orElse(null);
    }
}
