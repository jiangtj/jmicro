package com.jiangtj.micro.pic.upload;

import com.jiangtj.micro.pic.upload.ex.PicUploadCheckException;
import com.jiangtj.micro.pic.upload.local.LocalPicUploadProperties;
import com.jiangtj.micro.pic.upload.local.LocalPicUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LocalPicUploadServiceTest {

    private LocalPicUploadService localPicUploadService;
    private LocalPicUploadProperties localProperties;
    private PicUploadService picUploadService;
    private PicUploadProperties properties;
    PicUploadProperties.Dir dir;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        localProperties = new LocalPicUploadProperties();
        localProperties.setUploadPath(tempDir.toString());
        localPicUploadService = new LocalPicUploadService(localProperties);

        properties = new PicUploadProperties();
        Map<String, PicUploadProperties.Dir> dirs = new HashMap<>();
        dir = new PicUploadProperties.Dir();
        dir.setPath("test");
        dirs.put("test", dir);
        properties.setDirs(dirs);
        picUploadService = new PicUploadService(properties, List.of(localPicUploadService));
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
        PicUploadResult result = picUploadService.upload("test", file);

        // 验证结果
        assertNotNull(result);
        assertEquals("test-image.jpg", result.getOriginalFileName());
        assertTrue(result.getFileName().endsWith(".jpg"));
        assertTrue(result.getFileUrl().startsWith("http://localhost:8080/"));
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
        Exception exception = assertThrows(PicUploadCheckException.class, () -> {
            picUploadService.upload("test", file);
        });

        assertTrue(exception.getMessage().contains("不支持的文件类型"));
    }

    @Test
    void testUploadExceedMaxSize() {
        // 设置最大文件大小为500字节
        dir.setMaxFileSize(DataSize.ofKilobytes(1));

        // 创建超过大小限制的文件
        byte[] content = new byte[2000];
        MockMultipartFile file = new MockMultipartFile(
            "test-image",
            "test-image.jpg",
            "image/jpeg",
            content
        );

        // 验证上传超过大小限制的文件会抛出异常
        Exception exception = assertThrows(PicUploadCheckException.class, () -> {
            picUploadService.upload("test", file);
        });

        assertTrue(exception.getMessage().contains("文件大小超过限制"));
    }

    @Test
    void testDataSize() {
        assertEquals(1024 * 1024, DataSize.ofMegabytes(1).toBytes());
        assertEquals(1024, DataSize.ofKilobytes(1).toBytes());
    }
}