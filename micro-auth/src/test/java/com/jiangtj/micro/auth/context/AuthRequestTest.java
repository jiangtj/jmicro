package com.jiangtj.micro.auth.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthRequestTest {

    private MockAuthRequest request;

    @BeforeEach
    void setUp() {
        request = new MockAuthRequest(
            "/api/users",
            URI.create("http://localhost:8080/api/users?id=1&name=test"),
            HttpMethod.GET);
        request.addHeader("Authorization", "Bearer token");
        request.addHeader("Accept", "application/json");
        request.addQueryParam("id", "1");
        request.addQueryParam("name", "test");
    }

    @Test
    void testGetPath() {
        assertEquals("/api/users", request.getPath());
    }

    @Test
    void testMatch() {
        assertTrue(request.match("/api/**"));
        assertTrue(request.match("/api/users"));
        assertFalse(request.match("/api/posts"));
        assertFalse(request.match());
    }

    @Test
    void testGetURI() {
        assertEquals(
            "http://localhost:8080/api/users?id=1&name=test",
            request.getURI().toString());
    }

    @Test
    void testGetMethod() {
        assertEquals(HttpMethod.GET, request.getMethod());
    }

    @Test
    void testGetQueryParams() {
        List<String> idParams = request.getQueryParams("id");
        assertEquals(1, idParams.size());
        assertEquals("1", idParams.get(0));

        List<String> nameParams = request.getQueryParams("name");
        assertEquals(1, nameParams.size());
        assertEquals("test", nameParams.get(0));

        List<String> nonExistentParams = request.getQueryParams("nonexistent");
        assertTrue(nonExistentParams.isEmpty());
    }

    @Test
    void testGetHeaders() {
        List<String> authHeaders = request.getHeaders("Authorization");
        assertEquals(1, authHeaders.size());
        assertEquals("Bearer token", authHeaders.get(0));

        List<String> acceptHeaders = request.getHeaders("Accept");
        assertEquals(1, acceptHeaders.size());
        assertEquals("application/json", acceptHeaders.get(0));

        List<String> nonExistentHeaders = request.getHeaders("nonexistent");
        assertTrue(nonExistentHeaders.isEmpty());
    }

    @Test
    void testGetHeader() {
        assertTrue(request.getHeader("Authorization").isPresent());
        assertEquals("Bearer token", request.getHeader("Authorization").get());
        assertTrue(request.getHeader("nonexistent").isEmpty());
    }

    @Test
    void testGetQueryParam() {
        assertTrue(request.getQueryParam("id").isPresent());
        assertEquals("1", request.getQueryParam("id").get());
        assertTrue(request.getQueryParam("nonexistent").isEmpty());
    }
}