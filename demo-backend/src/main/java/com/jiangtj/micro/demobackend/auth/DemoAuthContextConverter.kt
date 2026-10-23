package com.jiangtj.micro.demobackend.auth;

import com.jiangtj.micro.auth.AuthRequestAttributes;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.AuthContextConverter;
import com.jiangtj.micro.auth.context.AuthRequest;
import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;
import io.jsonwebtoken.Claims;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 将请求头中的 JWT 转换为鉴权上下文。
 * <p>
 * token 由 {@code POST /login} 以 HS256 签发；签名或有效期校验失败时视为未登录。
 * 优先级高于内置的 {@code JwtConverter}（后者用于 OIDC/JWKS 场景）。
 */
@Component
public class DemoAuthContextConverter implements AuthContextConverter, Ordered {

    private final DemoJwtService jwtService;

    public DemoAuthContextConverter(DemoJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public AuthContext convert(AuthRequest request) {
        return request.getHeader(AuthRequestAttributes.TOKEN_HEADER_NAME)
            .map(this::removePrefix)
            .flatMap(jwtService::parse)
            .map(this::toContext)
            .orElseGet(AuthContext::unLogin);
    }

    private AuthContext toContext(Claims claims) {
        Subject subject = Subject.builder()
            .id(claims.getSubject())
            .name(claims.getSubject())
            .displayName(claims.get(DemoJwtService.CLAIM_NAME, String.class))
            .type(claims.get(DemoJwtService.CLAIM_TYPE, String.class))
            .issuer(claims.getIssuer())
            .build();
        return AuthContext.create(subject,
            Authorization.create(jwtService.roles(claims), jwtService.permissions(claims)));
    }

    private String removePrefix(String token) {
        String prefix = AuthRequestAttributes.TOKEN_HEADER_PREFIX;
        return token.startsWith(prefix) ? token.substring(prefix.length()).trim() : token;
    }
}
