package com.jiangtj.micro.common.utils;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileNameUtilsTest {

    @Test
    void testGetRandomFileNameWithSuffix() {
        // 测试带点的后缀
        String result1 = FileNameUtils.getRandomFileNameWithSuffix(".txt");
        assertTrue(result1.endsWith(".txt"));
        assertEquals(26, result1.length()); // base64压缩UUID长度(22) + 后缀长度(4)

        // 测试不带点的后缀
        String result2 = FileNameUtils.getRandomFileNameWithSuffix("txt");
        assertTrue(result2.endsWith(".txt"));
        assertEquals(26, result2.length());

        // 测试生成的文件名的唯一性
        Set<String> fileNames = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String fileName = FileNameUtils.getRandomFileNameWithSuffix(".txt");
            assertTrue(fileNames.add(fileName), "生成的文件名应该是唯一的");
        }
    }
}