package com.jiangtj.micro.auth.casdoor;

import com.jiangtj.micro.auth.AuthRequestAttributes;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.AuthContextConverter;
import jakarta.annotation.Resource;
import org.casbin.casdoor.config.Config;
import org.casbin.casdoor.entity.User;
import org.springframework.http.HttpRequest;
import org.springframework.lang.Nullable;

import java.util.List;

public class CasdoorAuthContextConverter implements AuthContextConverter {

    @Resource
    private CasdoorAuthService casdoorAuthService;
    @Resource
    private Config config;

    @Override
    @Nullable
    public AuthContext convert(HttpRequest request) {
        List<String> headers = request.getHeaders().get(AuthRequestAttributes.TOKEN_HEADER_NAME);
        if (headers == null || headers.size() != 1) {
            return null;
        }
        String issuer = config.getEndpoint();
        String token = headers.get(0);
        String headerPrefix = AuthRequestAttributes.TOKEN_HEADER_PREFIX;
        User user = casdoorAuthService.parseJwtToken(token.substring(headerPrefix.length()));
        return new CasdoorUserContextImpl(issuer, user);
    }

}
