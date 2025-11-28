package com.jiangtj.micro.auth.context;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;

@Slf4j
public class Test3C implements AuthContextConverter, Ordered {

    @Nullable
    @Override
    public AuthContext convert(@NonNull AuthRequest request) {
        log.error("3");
        return AuthContext.create("test");
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
