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
            FormRule rule = new FormRule();
            rule.setType("string");
            rule.setPattern("^1[0-9]{10}$");
            rule.setMessage(element.message());
            return rule;
        }
        if (type.isAssignableFrom(Long.class)) {
            FormRule rule = new FormRule();
            rule.setType("number");
            rule.setMin(1000000000);
            rule.setMax(1999999999);
            rule.setMessage(element.message());
            return rule;
        }
        throw new RuntimeException("不支持的规则类型");
    }
}
