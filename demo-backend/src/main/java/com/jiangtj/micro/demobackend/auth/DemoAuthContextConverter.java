package com.jiangtj.micro.demobackend.auth;

import com.jiangtj.micro.auth.AuthRequestAttributes;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.AuthContextConverter;
import com.jiangtj.micro.auth.context.AuthRequest;
import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 将请求头中的 token 转换为鉴权上下文。
 * <p>
 * token 由 {@code POST /login} 签发；无法识别时返回 null（交由后续转换器处理）。
 */
@Component
public class DemoAuthContextConverter implements AuthContextConverter {

    @Resource
    private DemoUserStore userStore;

    @Override
    public AuthContext convert(AuthRequest request) {
        return request.getHeader(AuthRequestAttributes.TOKEN_HEADER_NAME)
            .map(this::removePrefix)
            .flatMap(userStore::findByToken)
            .map(user -> {
                Subject subject = Subject.builder()
                    .id(user.username())
                    .name(user.username())
                    .displayName(user.nickname())
                    .type("demo")
                    .issuer("jmicro-demo")
                    .build();
                return AuthContext.create(subject, Authorization.create(user.roles(), user.permissions()));
            })
            .orElse(null);
    }

    private String removePrefix(String token) {
        String prefix = AuthRequestAttributes.TOKEN_HEADER_PREFIX;
        return token.startsWith(prefix) ? token.substring(prefix.length()).trim() : token;
    }
}
