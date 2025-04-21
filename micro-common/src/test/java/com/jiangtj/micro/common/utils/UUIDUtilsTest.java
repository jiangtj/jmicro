package com.jiangtj.micro.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class UUIDUtilsTest {

    @Test
    void testCompressToBase64() {
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        String base64Compressed = UUIDUtils.compressToBase64(UUID.fromString(uuid));
        assertNotNull(base64Compressed);
        assertTrue(base64Compressed.length() < uuid.length());
        // 验证Base64编码字符集
        assertTrue(base64Compressed.matches("^[A-Za-z0-9_-]+$"));
    }

    @Test
    void testGenerateBase64Compressed() {
        String base64Compressed = UUIDUtils.generateBase64Compressed();
        assertNotNull(base64Compressed);
        log.info("base64Compressed: {}", base64Compressed);
        // 验证Base64编码字符集
        assertTrue(base64Compressed.matches("^[A-Za-z0-9_-]+$"));
    }
}