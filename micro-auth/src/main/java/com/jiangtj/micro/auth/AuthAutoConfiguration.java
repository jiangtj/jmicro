package com.jiangtj.micro.auth;

import com.jiangtj.micro.auth.context.AuthContextConverter;
import com.jiangtj.micro.auth.context.AuthContextFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class AuthAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuthContextFactory authContextFactory(ObjectProvider<AuthContextConverter> converters) {
        return new AuthContextFactory(converters);
    }

}
