package com.jiangtj.micro.web;

import com.jiangtj.micro.common.JsonUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.json.JsonMapper;

@AutoConfiguration
public class JMicroCommonAutoConfiguration {

    @Resource
    private JsonMapper mapper;

    @PostConstruct
    public void setup() {
        JsonUtils.init(mapper);
    }

    @Bean
    public ApplicationProperty applicationProperty() {
        return new ApplicationProperty();
    }

}
