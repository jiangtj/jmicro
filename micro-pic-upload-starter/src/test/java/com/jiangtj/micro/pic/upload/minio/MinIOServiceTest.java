package com.jiangtj.micro.pic.upload.minio;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MinIOServiceTest {

    @Test
    void testUri() {
        URI base = URI.create("https://www.example.com/");
        assertEquals("https://www.example.com/api", base.resolve("api").toString());
        assertEquals("https://www.example.com/api", base.resolve("/api").toString());
    }

}