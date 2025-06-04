package com.jiangtj.micro.common.form;

import jakarta.validation.constraints.Pattern;

import java.lang.reflect.Field;

public class PatternHandler implements FormRuleHandler<Pattern> {
    @Override
    public Class<Pattern> getAnnotation() {
        return Pattern.class;
    }

    @Override
    public FormRule handle(Field field, Pattern element) {
        String message = element.message();
        if ("{jakarta.validation.constraints.Pattern.message}".equals(message)) {
            message = "需要匹配正则表达式: " + element.regexp();
        }
        return FormRule.builder()
            .type("string")
            .pattern(element.regexp())
            .message(message)
            .build();
    }
}
