package com.jiangtj.micro.auth.context;

import com.jiangtj.micro.auth.AuthAutoConfiguration;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {
    TestACConfig.class,
    AuthAutoConfiguration.class
})
class AuthContextFactoryTest {

    @Resource
    private AuthContextFactory authContextFactory;

    @Test
    void getAuthContext() {
        AuthContext authContext = authContextFactory.getAuthContext(null);
        assertEquals("test", authContext.subject().getId());
        assertEquals("null132", authContext.subject().getName());
    }
}