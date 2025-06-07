package com.jiangtj.micro.common.form;

import com.jiangtj.micro.common.validation.MaxLength;
import com.jiangtj.micro.common.validation.MinLength;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.*;

public class FormRuleGenerator {

    @Getter
    private static final Map<String, Map<String, List<FormRule>>> cache = new HashMap<>();
    @Getter
    private static final List<FormRuleHandler<? extends Annotation>> handlers = new ArrayList<>();

    static {
        addHandler(new PatternHandler());
        addHandler(new MobilePhoneHandler());
    }

    @NonNull
    public static Map<String, List<FormRule>> generate(@NonNull Class<?> clazz) {
        Map<String, List<FormRule>> map = cache.get(clazz.getName());
        if (map != null) {
            return map;
        }
        map = new LinkedHashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            List<FormRule> rules = new ArrayList<>();
            boolean setField = false;
            FormRule rule = new FormRule();

            Class<?> type = field.getType();
            if (Integer.class.isAssignableFrom(type)
                || int.class.isAssignableFrom(type)
                || Byte.class.isAssignableFrom(type)
                || byte.class.isAssignableFrom(type)
                || Long.class.isAssignableFrom(type)
                || long.class.isAssignableFrom(type)
            ) {
                rule.setType("number");
                setField = true;
            }
            if (type.isAssignableFrom(Boolean.class)
                || type.isAssignableFrom(boolean.class)) {
                rule.setType("boolean");
                setField = true;
            }

            Optional<NotNull> notNull = findAnnotation(field, NotNull.class);
            if (notNull.isPresent()) {
                rule.setRequired(true);
                setField = true;
            }

            Optional<NotEmpty> notEmpty = findAnnotation(field, NotEmpty.class);
            if (notEmpty.isPresent()) {
                rule.setType("string");
                rule.setRequired(true);
                setField = true;
            }

            Optional<NotBlank> notBlank = findAnnotation(field, NotBlank.class);
            if (notBlank.isPresent()) {
                rule.setType("string");
                rule.setRequired(true);
                rule.setWhitespace(true);
                setField = true;
            }

            Optional<Min> min = findAnnotation(field, Min.class);
            if (min.isPresent()) {
                rule.setType("number");
                rule.setMin((int) min.get().value());
                setField = true;
            }

            Optional<Max> max = findAnnotation(field, Max.class);
            if (max.isPresent()) {
                rule.setType("number");
                rule.setMax((int) max.get().value());
                setField = true;
            }

            Optional<Size> size = findAnnotation(field, Size.class);
            if (size.isPresent()) {
                if (type.isAssignableFrom(CharSequence.class)) {
                    rule.setType("string");
                }
                if (type.isArray() || Collection.class.isAssignableFrom(type)) {
                    rule.setType("array");
                }
                Size v = size.get();
                if (v.min() != 0) {
                    rule.setMin(v.min());
                }
                if (v.max() != Integer.MAX_VALUE) {
                    rule.setMax(v.max());
                }
                setField = true;
            }

            Optional<Email> email = findAnnotation(field, Email.class);
            if (email.isPresent()) {
                rule.setType("email");
                setField = true;
            }

            Optional<MinLength> minLen = findAnnotation(field, MinLength.class);
            if (minLen.isPresent()) {
                rule.setType("string");
                rule.setMin(minLen.get().value());
                setField = true;
            }

            Optional<MaxLength> maxLen = findAnnotation(field, MaxLength.class);
            if (maxLen.isPresent()) {
                rule.setType("string");
                rule.setMax(maxLen.get().value());
                setField = true;
            }
            if (setField) {
                if (rule.getType() == null && type.isAssignableFrom(String.class)) {
                    rule.setType("string");
                }
                rules.add(rule);
            }

            handle(field, rules);

            Optional<Valid> valid = findAnnotation(field, Valid.class);
            if (valid.isPresent()) {
                FormRule validRule = new FormRule();
                validRule.setType("object");
                validRule.setFields(generate(field.getType()));
                rules.add(validRule);
            }

            if (!rules.isEmpty()) {
                map.put(field.getName(), rules);
            }
        }

        cache.put(clazz.getName(), map);
        return map;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void handle(Field field, List<FormRule> rules) {
        for (FormRuleHandler handler : handlers) {
            Class<? extends Annotation> annotation = handler.getAnnotation();
            findAnnotation(field, annotation)
                .map(value -> handler.handle(field, value))
                .ifPresent(rules::add);
        }
    }

    static <A extends Annotation> Optional<A> findAnnotation(AnnotatedElement element, Class<A> annotationType) {
        return Optional.ofNullable(element.getAnnotation(annotationType));
    }

    static void addHandler(FormRuleHandler<? extends Annotation> handler) {
        handlers.add(handler);
    }

}
