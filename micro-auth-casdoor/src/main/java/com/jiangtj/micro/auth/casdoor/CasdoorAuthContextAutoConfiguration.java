package com.jiangtj.micro.auth.casdoor;

import org.casbin.casdoor.config.CasdoorConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class CasdoorAuthContextAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CasdoorAuthContextConverter casdoorAuthContextConverter() {
        return new CasdoorAuthContextConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    CasdoorAuthService casdoorAuthService(CasdoorConfiguration config) {
        return new CasdoorAuthService(config);
    }

}
