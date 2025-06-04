package com.jiangtj.micro.common.form;

import com.jiangtj.micro.common.validation.MobilePhone;

import java.lang.reflect.Field;

public class MobilePhoneHandler implements FormRuleHandler<MobilePhone> {
    @Override
    public Class<MobilePhone> getAnnotation() {
        return MobilePhone.class;
    }

    @Override
    public FormRule handle(Field field, MobilePhone element) {
        Class<?> type = field.getType();
        if (type.isAssignableFrom(String.class)) {
            return FormRule.builder()
                .type("string")
                .pattern("^1[0-9]{10}$")
                .message(element.message())
                .build();
        }
        if (type.isAssignableFrom(Long.class)) {
            return FormRule.builder()
                .type("number")
                .min(1000000000)
                .max(1999999999)
                .message(element.message())
                .build();
        }
        throw new RuntimeException("不支持的规则类型");
    }
}
