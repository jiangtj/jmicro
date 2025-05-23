package com.jiangtj.micro.auth;

import com.jiangtj.micro.auth.context.AuthContextConverter;
import com.jiangtj.micro.auth.context.AuthContextFactory;
import com.jiangtj.micro.auth.context.AuthContextHandler;
import com.jiangtj.micro.auth.core.AuthProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

@AutoConfiguration
@EnableConfigurationProperties({AuthProperties.class})
public class AuthAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AuthContextFactory authContextFactory(ObjectProvider<AuthContextConverter> converters, ObjectProvider<AuthContextHandler> handlers) {
        return new AuthContextFactory(converters, handlers);
    }

}
