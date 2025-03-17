package com.jiangtj.micro.spring.boot.reactive;

import jakarta.annotation.Resource;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.List;

public class NoViewResponseContext implements ServerResponse.Context {

    @Resource
    ServerCodecConfigurer serverCodecConfigurer;

    @Override
    @NonNull
    public List<HttpMessageWriter<?>> messageWriters() {
        return serverCodecConfigurer.getWriters();
    }

    @Override
    @NonNull
    public List<ViewResolver> viewResolvers() {
        return Collections.emptyList();
    }
}
