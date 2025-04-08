package com.jiangtj.micro.test;

import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.AuthContextConverter;
import com.jiangtj.micro.auth.context.AuthRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class TestAuthContextConverter implements AuthContextConverter {

    @Override
    public AuthContext convert(@NonNull AuthRequest request) {
        return TestAuthContextHolder.getAuthContext();
    }
}
