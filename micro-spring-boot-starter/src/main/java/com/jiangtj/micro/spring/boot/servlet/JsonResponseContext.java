package com.jiangtj.micro.spring.boot.servlet;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.function.ServerResponse;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;

public class JsonResponseContext implements ServerResponse.Context {

    List<HttpMessageConverter<?>> messageConverters;

    public JsonResponseContext(JsonMapper jsonMapper) {
        messageConverters = new ArrayList<>();
        messageConverters.add(new JacksonJsonHttpMessageConverter(jsonMapper.rebuild()));
    }

    @Override
    public List<HttpMessageConverter<?>> messageConverters() {
        return messageConverters;
    }

}
