package com.jiangtj.micro.sql.jooq;

import org.jooq.Record;
import org.jooq.Record1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PageReactiveUtils {

    static <R extends Record, T> ResultStepHandler<Flux<R>, Mono<Record1<Integer>>, PageUtils.ResultStep<Flux<T>, Mono<Integer>>> into(Class<T> clz) {
        return (Flux<R> result, Mono<Record1<Integer>> count, Pageable pageable) -> new PageUtils.ResultStep<>(
            result.map(record -> record.into(clz)),
            count.map(Record1::value1),
            pageable
        );
    }

    static <T> Mono<Page<T>> toPage(Flux<T> result, Mono<Integer> count, Pageable pageable) {
        return Mono.zip(result.collectList(), count)
            .map(tuple -> PageUtils.toPage(tuple.getT1(), tuple.getT2(), pageable));
    }

    static <R extends Record, T> ResultStepHandler<Flux<R>, Mono<Record1<Integer>>, Mono<Page<T>>> toPage(Class<T> clz) {
        return (Flux<R> result, Mono<Record1<Integer>> count, Pageable pageable) -> PageReactiveUtils.toPage(
            result.map(record -> record.into(clz)),
            count.map(Record1::value1),
            pageable
        );
    }

}
