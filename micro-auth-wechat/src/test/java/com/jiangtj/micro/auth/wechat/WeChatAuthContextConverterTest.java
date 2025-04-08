package com.jiangtj.micro.auth.wechat;

import com.jiangtj.micro.auth.AuthRequestAttributes;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.Subject;
import com.jiangtj.micro.auth.reactive.ReactiveAuthRequest;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import static org.junit.jupiter.api.Assertions.*;

class WeChatAuthContextConverterTest {

    private final WeChatAuthContextConverter converter = new WeChatAuthContextConverter();

    @Test
    public void convert_NoAuthorizationHeader_ReturnsNull() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        AuthContext result = converter.convert(new ReactiveAuthRequest(request));
        assertNull(result);
    }

    @Test
    public void convert_MultipleAuthorizationHeaders_ReturnsNull() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .header(AuthRequestAttributes.TOKEN_HEADER_NAME, "Bearer token1")
                .header(AuthRequestAttributes.TOKEN_HEADER_NAME, "Bearer token2")
                .build();
        AuthContext result = converter.convert(new ReactiveAuthRequest(request));
        assertNull(result);
    }

    @Test
    public void convert_ValidJwt_ReturnsAuthContext() {
        String jwt = Jwts.builder()
                .subject("testUser")
                .signWith(KeyHolder.getKey())
                .compact();
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .header(AuthRequestAttributes.TOKEN_HEADER_NAME, "Bearer " + jwt)
                .build();
        AuthContext result = converter.convert(new ReactiveAuthRequest(request));
        assertNotNull(result);
        Subject subject = result.subject();
        assertEquals("testUser", subject.getId());
        assertEquals("wechat-mini", subject.getType());
    }

    @Test
    public void convert_InvalidJwt_ThrowsException() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .header(AuthRequestAttributes.TOKEN_HEADER_NAME, "Bearer invalidToken")
                .build();
        assertThrows(Exception.class, () -> converter.convert(new ReactiveAuthRequest(request)));
    }
}
