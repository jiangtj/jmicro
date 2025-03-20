package com.jiangtj.micro.auth.context;

import org.springframework.core.annotation.Order;

@Order(2)
public class Test3H implements AuthContextHandler{
    @Override
    public void handle(AuthContext ctx) {
        ctx.subject().setName(ctx.subject().getName() + "3");
    }
}
