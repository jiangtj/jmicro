package com.jiangtj.micro.auth.reactive;


import com.jiangtj.micro.auth.annotations.HasLogin;
import com.jiangtj.micro.auth.annotations.HasPermission;
import com.jiangtj.micro.auth.annotations.HasRole;
import com.jiangtj.micro.auth.annotations.HasSubject;
import com.jiangtj.micro.auth.core.AuthReactiveService;
import com.jiangtj.micro.auth.reactive.rbac.HasLoginAdvice;
import com.jiangtj.micro.auth.reactive.rbac.HasPermissionAdvice;
import com.jiangtj.micro.auth.reactive.rbac.HasRoleAdvice;
import com.jiangtj.micro.auth.reactive.rbac.HasSubjectAdvice;
import com.jiangtj.micro.web.aop.AnnotationPointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuthReactiveService authReactorService() {
        return new DefaultAuthReactiveService();
    }

    @Bean
    public ServerWebExchangeContextFixFilter serverWebExchangeContextFixFilter() {
        return new ServerWebExchangeContextFixFilter();
    }

    @Bean
    public ReactiveAuthContextFilter reactiveAuthContextFilter() {
        return new ReactiveAuthContextFilter();
    }


    @Bean
    public HasLoginAdvice hasLoginAdvice() {
        return new HasLoginAdvice();
    }

    @Bean
    public HasSubjectAdvice hasSubjectAdvice() {
        return new HasSubjectAdvice();
    }

    @Bean
    public HasRoleAdvice hasRoleAdvice() {
        return new HasRoleAdvice();
    }

    @Bean
    public HasPermissionAdvice hasPermissionAdvice() {
        return new HasPermissionAdvice();
    }

    @Bean
    public Advisor hasLoginAdvisor(HasLoginAdvice advice) {
        return new DefaultPointcutAdvisor(new AnnotationPointcut<>(HasLogin.class), advice);
    }

    @Bean
    public Advisor hasSubjectAdvisor(HasSubjectAdvice advice) {
        return new DefaultPointcutAdvisor(new AnnotationPointcut<>(HasSubject.class), advice);
    }

    @Bean
    public Advisor hasRoleAdvisor(HasRoleAdvice advice) {
        return new DefaultPointcutAdvisor(new AnnotationPointcut<>(HasRole.class), advice);
    }

    @Bean
    public Advisor hasPermissionAdvisor(HasPermissionAdvice advice) {
        return new DefaultPointcutAdvisor(new AnnotationPointcut<>(HasPermission.class), advice);
    }

}
