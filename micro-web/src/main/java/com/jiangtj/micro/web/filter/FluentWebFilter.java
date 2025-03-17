package com.jiangtj.micro.web.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.server.WebFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Getter
public class FluentWebFilter {

    private final List<FluentWebFilterPath> paths = new ArrayList<>();
    private WebFilter action;
    private List<String> exclude;

    public FluentWebFilter(WebFilter action) {
        this.action = action;
    }

    public FluentWebFilter path(List<String> include, Supplier<FluentWebFilter> nest) {
        this.paths.add(new FluentWebFilterPath(include, nest.get()));
        return this;
    }

    public FluentWebFilterPathBuilder path(String... include) {
        return new FluentWebFilterPathBuilder(List.of(include), this);
    }

    public FluentWebFilter action(WebFilter action) {
        this.action = action;
        return this;
    }

    public FluentWebFilter exclude(String... exclude) {
        this.exclude = List.of(exclude);
        return this;
    }

    public static FluentWebFilter create() {
        return new FluentWebFilter(((exchange, chain) -> chain.filter(exchange)));
    }

    public static FluentWebFilter createAction(WebFilter action) {
        return new FluentWebFilter(action);
    }

    @Getter
    @AllArgsConstructor
    public static class FluentWebFilterPath {
        private List<String> include;
        private FluentWebFilter nest;
    }

    @Getter
    @AllArgsConstructor
    public static class FluentWebFilterPathBuilder {
        private List<String> include;
        private FluentWebFilter context;
        public FluentWebFilter action(WebFilter action) {
            return context.path(include, () -> FluentWebFilter.createAction(action));
        }
        public FluentWebFilter nest(Supplier<FluentWebFilter> nest) {
            return context.path(include, nest);
        }
    }

}
