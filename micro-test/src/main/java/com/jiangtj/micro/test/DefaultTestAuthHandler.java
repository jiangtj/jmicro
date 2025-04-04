package com.jiangtj.micro.test;

import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;
import com.jiangtj.micro.web.AnnotationUtils;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DefaultTestAuthHandler implements TestAuthHandler{

    @Override
    public AuthContext convert(Method testMethod, ApplicationContext context) {
        return convertAnnotatedElementToAutContext(testMethod, context);
    }

    @Override
    public AuthContext convert(Class<?> testClass, ApplicationContext context) {
        return convertAnnotatedElementToAutContext(testClass, context);
    }

    public AuthContext convertAnnotatedElementToAutContext(AnnotatedElement element, ApplicationContext context) {

        Subject subject = new Subject();
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        Optional<WithMockUser> mockUser = AnnotationUtils.findAnnotation(element, WithMockUser.class);
        if (mockUser.isPresent()) {
            WithMockUser user = mockUser.get();
            subject.setId(user.subject());
            roles = Stream.of(user.roles()).toList();
            permissions = Stream.of(user.permissions()).toList();
        }

        AnnotationUtils.findAnnotation(element, WithMockSubject.class)
            .ifPresent(item -> {
                subject.setId(item.id());
                subject.setName(item.name());
                subject.setDisplayName(item.displayName());
                subject.setType(item.type());
                subject.setIssuer(item.issuer());
            });

        Optional<WithMockRole> mockRole = AnnotationUtils.findAnnotation(element, WithMockRole.class);
        if (mockRole.isPresent()) {
            roles = Stream.of(mockRole.get().value()).toList();
        }

        Optional<WithMockPermission> mockPermission = AnnotationUtils.findAnnotation(element, WithMockPermission.class);
        if (mockPermission.isPresent()) {
            permissions = Stream.of(mockPermission.get().value()).toList();
        }

        return AuthContext.create(subject, Authorization.create(roles, permissions));
    }
}
