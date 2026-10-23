package com.jiangtj.micro.test

import com.jiangtj.micro.auth.context.AuthContext
import com.jiangtj.micro.auth.context.Authorization
import com.jiangtj.micro.auth.context.Subject
import com.jiangtj.micro.web.AnnotationUtils
import org.springframework.context.ApplicationContext
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method

class DefaultTestAuthHandler : TestAuthHandler {

    override fun convert(testMethod: Method, context: ApplicationContext): AuthContext =
        convertAnnotatedElementToAutContext(testMethod, context)

    override fun convert(testClass: Class<*>, context: ApplicationContext): AuthContext =
        convertAnnotatedElementToAutContext(testClass, context)

    fun convertAnnotatedElementToAutContext(element: AnnotatedElement, context: ApplicationContext): AuthContext {
        val subject = Subject()
        var roles: List<String> = emptyList()
        var permissions: List<String> = emptyList()

        AnnotationUtils.findAnnotation(element, WithMockUser::class.java).ifPresent { user ->
            subject.setId(user.subject)
            roles = user.roles.toList()
            permissions = user.permissions.toList()
        }

        AnnotationUtils.findAnnotation(element, WithMockSubject::class.java).ifPresent { item ->
            subject.setId(item.id)
            subject.setName(item.name)
            subject.setDisplayName(item.displayName)
            subject.setType(item.type)
            subject.setIssuer(item.issuer)
        }

        AnnotationUtils.findAnnotation(element, WithMockRole::class.java).ifPresent { mockRole ->
            roles = mockRole.value.toList()
        }

        AnnotationUtils.findAnnotation(element, WithMockPermission::class.java).ifPresent { mockPermission ->
            permissions = mockPermission.value.toList()
        }

        return AuthContext.create(subject, Authorization.create(roles, permissions))
    }
}
