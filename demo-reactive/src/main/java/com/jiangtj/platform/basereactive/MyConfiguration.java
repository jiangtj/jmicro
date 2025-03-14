package com.jiangtj.platform.basereactive;

import com.jiangtj.micro.auth.core.AuthReactiveService;
import com.jiangtj.micro.web.FluentWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class MyConfiguration implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedHeaders("*")
            .allowedMethods("*");
    }

    @Bean
    public FluentWebFilter fluentWebFilter(AuthReactiveService authReactiveService) {
        return FluentWebFilter.create()
            .exclude("/", "/insecure/**", "/toLogin", "/login")
            .action((exchange, chain) ->
                authReactiveService.hasLogin().then(chain.filter(exchange)))
            .path("/role-test-1").action((exchange, chain) ->
                authReactiveService.hasRole("role-test1").then(chain.filter(exchange)));
    }
    /*@Bean
    public FluentWebFilter fluentWebFilter(AuthReactiveService authReactiveService) {
        return FluentWebFilter.create()
            .addPath("/**").nest(() -> FluentWebFilter
                .createAction((exchange, chain) ->
                    authReactiveService.hasLogin().then(chain.filter(exchange)))
                .exclude("/", "/insecure/**", "/toLogin", "/login")
                .addPath("/role-test-1").action((exchange, chain) ->
                    authReactiveService.hasRole("role-test1").then(chain.filter(exchange))));
    }*/
}
