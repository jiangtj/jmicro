/*
 * Copyright 2002-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiangtj.micro.web.filter;

import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class FluentWebFilterChain implements WebFilterChain {

    private final List<FluentWebFilter> allFilters;
    @Nullable
    private final FluentWebFilter currentFilter;
    @Nullable
    private final FluentWebFilterChain chain;
    private final Mono<Void> out;

    public FluentWebFilterChain(List<FluentWebFilter> filters, Mono<Void> out) {
        this.allFilters = Collections.unmodifiableList(filters);
        FluentWebFilterChain chain = initChain(filters, out);
        this.currentFilter = chain.currentFilter;
        this.chain = chain.chain;
        this.out = out;
    }

    private static FluentWebFilterChain initChain(List<FluentWebFilter> filters, Mono<Void> out) {
        FluentWebFilterChain chain = new FluentWebFilterChain(filters, null, null, out);
        ListIterator<? extends FluentWebFilter> iterator = filters.listIterator(filters.size());
        while (iterator.hasPrevious()) {
            chain = new FluentWebFilterChain(filters, iterator.previous(), chain, out);
        }
        return chain;
    }

    /**
     * Private constructor to represent one link in the chain.
     */
    private FluentWebFilterChain(List<FluentWebFilter> allFilters,
                                  @Nullable FluentWebFilter currentFilter, @Nullable FluentWebFilterChain chain, Mono<Void> out) {

        this.allFilters = allFilters;
        this.currentFilter = currentFilter;
        this.chain = chain;
        this.out = out;
    }

    @Override
    public @NotNull Mono<Void> filter(@NotNull ServerWebExchange exchange) {
        return Mono.defer(() ->
            this.currentFilter != null && this.chain != null ?
                invokeFilter(this.currentFilter, this.chain, exchange) : out);
    }

    private Mono<Void> invokeFilter(FluentWebFilter current, FluentWebFilterChain chain, ServerWebExchange exchange) {
        String currentName = current.getClass().getName();
        return current.getAction().filter(exchange, chain).checkpoint(currentName + " [FluentWebFilterChain]");
    }

}
