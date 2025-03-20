package com.jiangtj.micro.auth.context;

import org.springframework.core.annotation.Order;

@Order(1)
public class Test1H implements AuthContextHandler{
    @Override
    public void handle(AuthContext ctx) {
        ctx.subject().setName(ctx.subject().getName() + "1");
    }
}
