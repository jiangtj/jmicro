package com.jiangtj.micro.web;

import jakarta.annotation.Resource;
import org.jspecify.annotations.Nullable;
import org.springframework.core.env.Environment;

public class ApplicationProperty {

    @Resource
    private Environment environment;

    @Nullable
    private String applicationName;

    @Nullable
    public String getName() {
        if (applicationName != null) {
            return applicationName;
        }
        applicationName = environment.getProperty("spring.application.name");
        if (applicationName == null) {
            return null;
        }
        applicationName = applicationName.toLowerCase();
        return applicationName;
    }
}
