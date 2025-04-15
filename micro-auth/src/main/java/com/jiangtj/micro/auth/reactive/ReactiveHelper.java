package com.jiangtj.micro.auth.reactive;

import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ReactiveHelper {

    static <V> Function<V, Mono<V>> interceptor(Supplier<Mono<Void>> supplier) {
        return v -> supplier.get().thenReturn(v);
    }

    static <V> Function<V, Mono<V>> interceptor(Mono<Void> mono) {
        return mono::thenReturn;
    }

}
