package com.jiangtj.micro.auth.reactive;

import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.core.AuthReactiveService;
import reactor.core.publisher.Mono;

public class SimpleAuthReactiveService implements AuthReactiveService {

    @Override
    public Mono<AuthContext> getContext() {
        return AuthReactiveHolder.deferAuthContext();
    }
}
