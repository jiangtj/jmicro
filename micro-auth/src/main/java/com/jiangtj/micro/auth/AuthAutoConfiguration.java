package com.jiangtj.micro.auth;

import com.jiangtj.micro.auth.context.AuthContextConverter;
import com.jiangtj.micro.auth.context.AuthContextFactory;
import com.jiangtj.micro.auth.context.AuthContextHandler;
import com.jiangtj.micro.auth.core.AuthProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration
@EnableConfigurationProperties({AuthProperties.class})
public class AuthAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuthContextFactory authContextFactory(List<AuthContextConverter> converters, List<AuthContextHandler> handlers) {
        return new AuthContextFactory(converters, handlers);
    }

}
