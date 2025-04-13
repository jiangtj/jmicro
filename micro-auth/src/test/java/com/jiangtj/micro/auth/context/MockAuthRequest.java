package com.jiangtj.micro.auth.context;

import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.util.*;

public class MockAuthRequest implements AuthRequest {
    private final String path;
    private final URI uri;
    private final HttpMethod method;
    private final Map<String, List<String>> headers;
    private final Map<String, List<String>> queryParams;
    private final Map<String, Object> attributes;

    public MockAuthRequest(String path, URI uri, HttpMethod method) {
        this.path = path;
        this.uri = uri;
        this.method = method;
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();
        this.attributes = new HashMap<>();
    }

    public void addHeader(String name, String value) {
        headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
    }

    public void addQueryParam(String name, String value) {
        queryParams.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
    }

    public void addAttributes(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public List<String> getQueryParams(@NonNull String name) {
        return queryParams.getOrDefault(name, Collections.emptyList());
    }

    @Override
    public List<String> getHeaders(@NonNull String name) {
        return headers.getOrDefault(name, Collections.emptyList());
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getSessionAttribute(@NonNull String name) {
        return (T)attributes.get(name);
    }
}