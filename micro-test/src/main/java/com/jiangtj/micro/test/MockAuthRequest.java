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

    private MockAuthRequest(Builder builder) {
        this.uri = builder.uri;
        this.path = RequestPath.parse(this.uri, null);
        this.method = builder.method;
        this.headers = builder.headers;
        this.queryParams = builder.queryParams;

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

    @Override
    public Object getSessionAttribute(String name) {
        return null;
    }

    public static Builder get(String urlString) {
        return new Builder(HttpMethod.GET, urlString);
    }

    public static Builder post(String urlString) {
        return new Builder(HttpMethod.POST, urlString);
    }

    public static Builder put(String urlString) {
        return new Builder(HttpMethod.PUT, urlString);
    }

    public static Builder delete(String urlString) {
        return new Builder(HttpMethod.DELETE, urlString);
    }

    public static class Builder {
        private final URI uri;
        private final HttpMethod method;
        private final Map<String, List<String>> headers = new HashMap<>();
        private final Map<String, List<String>> queryParams = new HashMap<>();

        private Builder(HttpMethod method, String urlString) {
            try {
                this.uri = new URI(urlString);
                this.method = method;
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        public Builder header(String name, String value) {
            headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
            return this;
        }

        public Builder queryParam(String name, String value) {
            queryParams.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
            return this;
        }

        public MockAuthRequest build() {
            return new MockAuthRequest(this);
        }
    }
}