package com.jiangtj.micro.auth.context;

import org.jspecify.annotations.NonNull;
import org.springframework.core.annotation.Order;

@Order(3)
public class Test2H implements AuthContextHandler{
    @Override
    public void handle(@NonNull AuthContext ctx) {
        ctx.subject().setName(ctx.subject().getName() + "2");
    }
}
