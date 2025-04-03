package com.jiangtj.micro.auth.wechat;

import com.jiangtj.micro.auth.AuthRequestAttributes;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.AuthContextConverter;
import com.jiangtj.micro.auth.context.Subject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpRequest;
import org.springframework.lang.Nullable;

import java.util.List;

public class WeChatAuthContextConverter implements AuthContextConverter {

    @Nullable
    @Override
    public AuthContext convert(HttpRequest request) {
        List<String> headers = request.getHeaders().get(AuthRequestAttributes.TOKEN_HEADER_NAME);
        if (headers == null || headers.size() != 1) {
            return null;
        }
        String token = headers.get(0);
        String headerPrefix = AuthRequestAttributes.TOKEN_HEADER_PREFIX;
        String jwt = token.substring(headerPrefix.length());
        Claims payload = Jwts.parser()
            .verifyWith(KeyHolder.getKey())
            .build()
            .parseSignedClaims(jwt)
            .getPayload();
        return AuthContext.create(Subject.builder()
            .id(payload.getSubject())
            .type("wechat-mini")
            .build());
    }
}
