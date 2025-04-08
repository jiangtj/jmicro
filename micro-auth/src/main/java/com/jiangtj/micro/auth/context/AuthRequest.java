package com.jiangtj.micro.auth.context;

import java.util.List;
import java.util.Optional;

/**
 * 认证请求接口，用于封装 HTTP 请求的通用操作
 */
public interface AuthRequest {

    /**
     * 获取请求路径
     *
     * @return 请求路径
     */
    String getPath();

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
}