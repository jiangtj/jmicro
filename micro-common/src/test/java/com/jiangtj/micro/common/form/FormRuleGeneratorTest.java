package com.jiangtj.micro.common.form;

import com.jiangtj.micro.common.JsonUtils;
import com.jiangtj.micro.common.validation.MaxLength;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class FormRuleGeneratorTest {

    @Data
    static class Example {
        @MaxLength(5)
        private String name;
        @Min(18)
        private int age;
        private String address;
        @NotNull
        private String phone;
    }

    @Test
    void testGenerate() {
        Map<String, List<FormRule>> generate = FormRuleGenerator.generate(Example.class);
        String json = JsonUtils.toJson(generate);
        log.error(json);
        assertTrue(json.contains("\"name\":[{\"type\":\"string\",\"max\":5}]"));
        assertTrue(json.contains("\"age\":[{\"type\":\"number\",\"min\":18}]"));
        assertTrue(json.contains("\"address\":[{\"type\":\"string\"}]"));
        assertTrue(json.contains("\"phone\":[{\"type\":\"string\",\"required\":true}]"));
    }

    @Test
    void testCache() {
        FormRuleGenerator.generate(Example.class);
        Map<String, Map<String, List<FormRule>>> cache = FormRuleGenerator.getCache();
        assertTrue(cache.containsKey(Example.class.getName()));
    }
}