package com.jiangtj.micro.pic.upload;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PicUploadServiceTest {

    private PicUploadService picUploadService;
    private PicUploadProperties properties;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        properties = new PicUploadProperties();
        properties.setUploadPath(tempDir.toString());
        picUploadService = new PicUploadService(properties);
    }

    @Test
    void testUploadValidImage() throws IOException {
        // 创建模拟的图片文件
        byte[] content = new byte[1024];
        MockMultipartFile file = new MockMultipartFile(
                "test-image",
                "test-image.jpg",
                "image/jpeg",
                content
        );

        // 上传图片
        PicUploadResult result = picUploadService.upload(file);

        // 验证结果
        assertNotNull(result);
        assertEquals("test-image.jpg", result.getOriginalFileName());
        assertTrue(result.getFileName().endsWith(".jpg"));
        assertEquals(1024, result.getFileSize());
        assertEquals("image/jpeg", result.getFileType());
        assertTrue(result.getFilePath().startsWith(tempDir.toString()));
    }

    @Test
    void testUploadInvalidExtension() {
        // 创建不支持的文件类型
        byte[] content = new byte[1024];
        MockMultipartFile file = new MockMultipartFile(
                "test-file",
                "test-file.exe",
                "application/octet-stream",
                content
        );

        // 验证上传不支持的文件类型会抛出异常
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            picUploadService.upload(file);
        });

        assertTrue(exception.getMessage().contains("不支持的文件类型"));
    }

    @Test
    void testUploadExceedMaxSize() {
        // 设置最大文件大小为500字节
        properties.setMaxFileSize(500);

        // 创建超过大小限制的文件
        byte[] content = new byte[1000];
        MockMultipartFile file = new MockMultipartFile(
                "test-image",
                "test-image.jpg",
                "image/jpeg",
                content
        );

        // 验证上传超过大小限制的文件会抛出异常
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            picUploadService.upload(file);
        });

        assertTrue(exception.getMessage().contains("文件大小超过限制"));
    }
}