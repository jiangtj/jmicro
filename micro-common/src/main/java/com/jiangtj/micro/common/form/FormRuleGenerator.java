package com.jiangtj.micro.common.form;

import com.jiangtj.micro.common.validation.MaxLength;
import com.jiangtj.micro.common.validation.MinLength;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.*;

public class FormRuleGenerator {

    @Getter
    private static final Map<String, Map<String, List<FormRule>>> cache = new HashMap<>();

    public static Map<String, List<FormRule>> generate(Class<?> clazz) {
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
            if (type.isAssignableFrom(String.class)) {
                rule.setType("string");
                setField = true;
            }
            if (type.isAssignableFrom(Integer.class)
                || type.isAssignableFrom(int.class)
                || type.isAssignableFrom(Byte.class)
                || type.isAssignableFrom(byte.class)
                || type.isAssignableFrom(Long.class)
                || type.isAssignableFrom(long.class)
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

            Optional<Min> min = findAnnotation(field, Min.class);
            if (min.isPresent()) {
                rule.setMin((int) min.get().value());
                setField = true;
            }

            Optional<Max> max = findAnnotation(field, Max.class);
            if (max.isPresent()) {
                rule.setMax((int) max.get().value());
                setField = true;
            }

            Optional<MinLength> minLen = findAnnotation(field, MinLength.class);
            if (minLen.isPresent()) {
                rule.setMin(minLen.get().value());
                setField = true;
            }

            Optional<MaxLength> maxLen = findAnnotation(field, MaxLength.class);
            if (maxLen.isPresent()) {
                rule.setMax(maxLen.get().value());
                setField = true;
            }

            if (setField) {
                rules.add(rule);
            }
            if (!rules.isEmpty()) {
                map.put(field.getName(), rules);
            }
        }

        cache.put(clazz.getName(), map);
        return map;
    }

    static <A extends Annotation> Optional<A> findAnnotation(AnnotatedElement element, Class<A> annotationType) {
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS)
            .get(annotationType)
            .synthesize(MergedAnnotation::isPresent);
    }

}
