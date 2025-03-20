package com.jiangtj.micro.auth.context;

import org.springframework.core.annotation.Order;

@Order(3)
public class Test2H implements AuthContextHandler{
    @Override
    public void handle(AuthContext ctx) {
        ctx.subject().setName(ctx.subject().getName() + "2");
    }
}
