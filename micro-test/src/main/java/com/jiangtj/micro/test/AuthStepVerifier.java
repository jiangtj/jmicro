package com.jiangtj.micro.test;

import com.jiangtj.micro.auth.context.AuthContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public interface AuthStepVerifier {

    static <T> StepVerifier.FirstStep<T> create(Mono<? extends T> publisher) {
        return StepVerifier.create(publisher
            .contextWrite(ctx -> ctx.put(AuthContext.class, TestAuthContextHolder.getAuthContext())));
    }

}
