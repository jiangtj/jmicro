package com.jiangtj.micro.common.form;

import com.jiangtj.micro.common.JsonUtils;
import com.jiangtj.micro.common.validation.MaxLength;
import com.jiangtj.micro.common.validation.MobilePhone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
        @MobilePhone
        private String phone;
        @Pattern(regexp = "^1[0-9]{2}$")
        private String patternVar;
        @Valid
        private NestDto nest;
        @Size(min = 1)
        private String[] arr1;
        @Size(max = 2)
        private List<String> arr2;
        @Size(min = 3, max = 4)
        private String char1;
        @Email
        private String mail;
    }

    @Data
    static class NestDto {
        @NotBlank
        private String name;
    }

    @Test
    void testGenerate() {
        Map<String, List<FormRule>> generate = FormRuleGenerator.generate(Example.class);
        String json = JsonUtils.toJson(generate);
        log.error(json);
        assertTrue(json.contains("\"name\":[{\"type\":\"string\",\"max\":5}]"));
        assertTrue(json.contains("\"age\":[{\"type\":\"number\",\"min\":18}]"));
        assertFalse(json.contains("\"address\""));
        assertTrue(json.contains("\"phone\":[{\"type\":\"string\",\"required\":true},{\"type\":\"string\",\"pattern\":\"^1[0-9]{10}$\",\"message\":\"手机号格式不正确\"}]"));
        assertTrue(json.contains("\"patternVar\":[{\"type\":\"string\",\"pattern\":\"^1[0-9]{2}$\",\"message\":\"需要匹配正则表达式: ^1[0-9]{2}$\"}]"));
        assertTrue(json.contains("\"nest\":[{\"type\":\"object\",\"defaultField\":{\"name\":[{\"type\":\"string\",\"required\":true,\"whitespace\":true}]}}]"));
        assertTrue(json.contains("\"arr1\":[{\"type\":\"array\",\"min\":1}]"));
        assertTrue(json.contains("\"arr2\":[{\"type\":\"array\",\"max\":2}]"));
        assertTrue(json.contains("\"char1\":[{\"type\":\"string\",\"min\":3,\"max\":4}]"));
        assertTrue(json.contains("\"mail\":[{\"type\":\"email\"}]"));
    }

    @Test
    void testCache() {
        FormRuleGenerator.generate(Example.class);
        Map<String, Map<String, List<FormRule>>> cache = FormRuleGenerator.INSTANCE.getCache();
        assertTrue(cache.containsKey(Example.class.getName()));
    }

    @Data
    static class ExampleForArr {
        @NotEmpty
        private List<String> mail;
        @Valid
        private List<NestL> nestLS;
    }

    @Data
    static class NestL {
        @NotBlank
        private String name;
    }

    @Test
    void testListV() {
        Map<String, List<FormRule>> generate = FormRuleGenerator.generate(ExampleForArr.class);
        String json = JsonUtils.toJson(generate);
        log.error(json);
        assertEquals("{" +
            "\"mail\":[{\"type\":\"array\",\"required\":true}]," +
            "\"nestLS\":[{\"type\":\"array\",\"defaultField\":{\"name\":[{\"type\":\"string\",\"required\":true,\"whitespace\":true}]}}]" +
            "}", json);
    }
}