
package com.jiangtj.micro.auth.servlet;


import com.jiangtj.micro.auth.annotations.HasLogin;
import com.jiangtj.micro.auth.annotations.HasPermission;
import com.jiangtj.micro.auth.annotations.HasRole;
import com.jiangtj.micro.auth.annotations.HasSubject;
import com.jiangtj.micro.auth.core.AuthService;
import com.jiangtj.micro.auth.servlet.rbac.HasLoginAdvice;
import com.jiangtj.micro.auth.servlet.rbac.HasPermissionAdvice;
import com.jiangtj.micro.auth.servlet.rbac.HasRoleAdvice;
import com.jiangtj.micro.auth.servlet.rbac.HasSubjectAdvice;
import com.jiangtj.micro.web.aop.AnnotationPointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ServletAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuthService authService() {
        return new DefaultAuthService();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AuthHolder authHolder() {
        return new AuthHolder();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor hasLoginAdvisor(ObjectProvider<AuthService> authService) {
        return new DefaultPointcutAdvisor(new AnnotationPointcut<>(HasLogin.class), new HasLoginAdvice(authService));
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor hasSubjectAdvisor(ObjectProvider<AuthService> authService) {
        return new DefaultPointcutAdvisor(new AnnotationPointcut<>(HasSubject.class), new HasSubjectAdvice(authService));
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor hasRoleAdvisor(ObjectProvider<AuthService> authService) {
        return new DefaultPointcutAdvisor(new AnnotationPointcut<>(HasRole.class), new HasRoleAdvice(authService));
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor hasPermissionAdvisor(ObjectProvider<AuthService> authService) {
        return new DefaultPointcutAdvisor(new AnnotationPointcut<>(HasPermission.class), new HasPermissionAdvice(authService));
    }

}
