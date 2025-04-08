package com.jiangtj.micro.test;

import com.jiangtj.micro.auth.context.AuthRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class MockAuthRequest implements AuthRequest {
    private final RequestPath path;
    private final URI uri;
    private final HttpMethod method;
    private final Map<String, List<String>> headers;
    private final Map<String, List<String>> queryParams;

    public MockAuthRequest(HttpMethod method, URI uri) {
        this.uri = uri;
        this.path = RequestPath.parse(this.uri, null);
        this.method = method;
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();

        String query = uri.getQuery();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                String key = idx > 0 ? pair.substring(0, idx) : pair;
                String value = idx > 0 && idx < pair.length() - 1 ? pair.substring(idx + 1) : "";
                addQueryParam(key, value);
            }
        }
    }

    public void contextPath(String path) {
        this.path.modifyContextPath(path);
    }

    public void addHeader(String name, String value) {
        headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
    }

    public void addQueryParam(String name, String value) {
        queryParams.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
    }

    @Override
    public String getPath() {
        return path.pathWithinApplication().value();
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
    public List<String> getQueryParams(String name) {
        return queryParams.getOrDefault(name, Collections.emptyList());
    }

    @Override
    public List<String> getHeaders(String name) {
        return headers.getOrDefault(name, Collections.emptyList());
    }

    public static MockAuthRequest create(HttpMethod method, String urlString) {
        try {
            return new MockAuthRequest(method, new URI(urlString));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}