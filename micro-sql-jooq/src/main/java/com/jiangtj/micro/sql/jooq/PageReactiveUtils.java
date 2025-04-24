package com.jiangtj.micro.sql.jooq;

import org.jooq.Record;
import org.jooq.Record1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 响应式分页查询工具类。
 */
public interface PageReactiveUtils {

    /**
     * 将查询结果转换为指定类型的响应式分页步骤。
     *
     * @param clz 目标类型
     * @param <R> 记录类型
     * @param <T> 目标类型
     * @return 响应式分页步骤处理器
     */
    static <R extends Record, T> ResultStepHandler<Flux<R>, Mono<Record1<Integer>>, PageUtils.ResultStep<Flux<T>, Mono<Integer>>> into(
            Class<T> clz) {
        return (Flux<R> result, Mono<Record1<Integer>> count, Pageable pageable) -> new PageUtils.ResultStep<>(
                result.map(record -> record.into(clz)), count.map(Record1::value1), pageable);
    }

    /**
     * 将响应式查询结果转换为分页对象。
     *
     * @param result   查询结果流
     * @param count    总记录数
     * @param pageable 分页参数
     * @param <T>      结果类型
     * @return 分页对象的 Mono
     */
    static <T> Mono<Page<T>> toPage(Flux<T> result, Mono<Integer> count, Pageable pageable) {
        return Mono.zip(result.collectList(), count)
                .map(tuple -> PageUtils.toPage(tuple.getT1(), tuple.getT2(), pageable));
    }

    /**
     * 将查询结果直接转换为分页对象。
     *
     * @param clz 目标类型
     * @param <R> 记录类型
     * @param <T> 目标类型
     * @return 分页对象的响应式处理器
     */
    static <R extends Record, T> ResultStepHandler<Flux<R>, Mono<Record1<Integer>>, Mono<Page<T>>> toPage(
            Class<T> clz) {
        return (Flux<R> result, Mono<Record1<Integer>> count, Pageable pageable) -> PageReactiveUtils
                .toPage(result.map(record -> record.into(clz)), count.map(Record1::value1), pageable);
    }

}
