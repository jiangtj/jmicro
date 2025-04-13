package com.jiangtj.micro.auth.context;

import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 认证请求接口，用于封装 HTTP 请求的通用操作
 */
public interface AuthRequest {

    AntPathMatcher matcher = new AntPathMatcher();

    /**
     * 获取请求路径(应用内的)
     *
     * @return 请求路径
     */
    String getPath();

    /**
     * 使用 AntPathMatcher 匹配当前请求路径
     *
     * @param patterns 匹配模式
     * @return 如果任一模式匹配则返回 true
     */
    default boolean match(String... patterns) {
        if (patterns.length == 0) {
            return false;
        }
        String path = getPath();
        return Arrays.stream(patterns)
            .anyMatch(pattern -> matcher.match(pattern, path));
    }

    /**
     * 获取完整的请求 URI
     *
     * @return 完整的请求 URI，包含查询参数
     */
    URI getURI();

    /**
     * 获取 HTTP 请求方法
     *
     * @return HTTP 请求方法（Spring 的 HttpMethod 枚举类型）
     */
    HttpMethod getMethod();

    /**
     * 获取查询参数值
     *
     * @param name 参数名
     * @return 参数值列表，如果参数不存在则返回空列表
     */
    List<String> getQueryParams(String name);

    /**
     * 获取请求头值
     *
     * @param name 请求头名称
     * @return 请求头值列表，如果请求头不存在则返回空列表
     */
    List<String> getHeaders(String name);

    /**
     * 获取session中的属性值
     *
     * @param name 属性名称
     * @return session中的属性值，如果属性不存在则返回null
     */
    @Nullable
    Object getSessionAttribute(String name);

    /**
     * 获取单个请求头值
     *
     * @param name 请求头名称
     * @return 请求头值，如果请求头不存在或有多个值则返回 null
     */
    default Optional<String> getHeader(String name) {
        List<String> headers = getHeaders(name);
        return headers.size() == 1 ? Optional.of(headers.get(0)) : Optional.empty();
    }

    /**
     * 获取单个查询参数值
     *
     * @param name 参数名
     * @return 参数值，如果参数不存在或有多个值则返回 null
     */
    default Optional<String> getQueryParam(String name) {
        List<String> params = getQueryParams(name);
        return params.size() == 1 ? Optional.of(params.get(0)) : Optional.empty();
    }

    /**
     * 获取session中的属性值并转换为指定类型
     *
     * @param name 属性名称
     * @param <T>  返回值类型
     * @return session中的属性值，如果属性不存在则返回null
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <T> T getSessionValue(String name) {
        return (T) getSessionAttribute(name);
    }
}