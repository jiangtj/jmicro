package com.jiangtj.micro.common.utils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class RandomStringUtilsTest {

    @Test
    void testGet() {
        // 测试指定长度的随机字符串生成
        String result = RandomStringUtils.get(10);
        assertEquals(10, result.length());
        assertTrue(Pattern.matches("^[0-9A-Z]+$", result));

        // 测试长度为0的情况
        assertEquals("", RandomStringUtils.get(0));
    }

    @Test
    void testGetWithFormat() {
        // 测试带格式的随机字符串生成
        String result = RandomStringUtils.get("test-???-test");
        assertEquals(13, result.length());
        assertTrue(result.startsWith("test-"));
        assertTrue(result.endsWith("-test"));
        assertTrue(Pattern.matches("^test-[0-9A-Z]{3}-test$", result));

        // 测试空字符串
        assertEquals("", RandomStringUtils.get(""));
    }

    @Test
    void testGetWithFormatAndEsc() {
        // 测试自定义转义字符的随机字符串生成
        String result = RandomStringUtils.get("test-###-test", '#');
        assertEquals(13, result.length());
        assertTrue(result.startsWith("test-"));
        assertTrue(result.endsWith("-test"));
        assertTrue(Pattern.matches("^test-[0-9A-Z]{3}-test$", result));
    }

    @Test
    void testGetNum() {
        // 测试指定长度的随机数字生成
        String result = RandomStringUtils.getNum(8);
        assertEquals(8, result.length());
        assertTrue(Pattern.matches("^\\d+$", result));

        // 测试长度为0的情况
        assertEquals("", RandomStringUtils.getNum(0));
    }

    @Test
    void testGetNumWithFormat() {
        // 测试带格式的随机数字生成
        String result = RandomStringUtils.getNum("SN-??-???");
        assertEquals(9, result.length());
        assertTrue(Pattern.matches("^SN-\\d{2}-\\d{3}$", result));

        // 测试空字符串
        assertEquals("", RandomStringUtils.getNum(""));
    }

    @Test
    void testGetNumWithFormatAndEsc() {
        // 测试自定义转义字符的随机数字生成
        String result = RandomStringUtils.getNum("SN-##-###", '#');
        assertEquals(9, result.length());
        assertTrue(Pattern.matches("^SN-\\d{2}-\\d{3}$", result));
    }

    @Test
    void testGetHex() {
        // 测试指定长度的十六进制字符串生成
        String result = RandomStringUtils.getHex(6);
        assertEquals(6, result.length());
        assertTrue(Pattern.matches("^[0-9A-F]+$", result));

        // 测试长度为0的情况
        assertEquals("", RandomStringUtils.getHex(0));
    }

    @Test
    void testGetHexWithFormat() {
        // 测试带格式的十六进制字符串生成
        String result = RandomStringUtils.getHex("0x????");
        assertEquals(6, result.length());
        assertTrue(Pattern.matches("^0x[0-9A-F]{4}$", result));

        // 测试空字符串
        assertEquals("", RandomStringUtils.getHex(""));
    }

    @Test
    void testGetHexWithFormatAndEsc() {
        // 测试自定义转义字符的十六进制字符串生成
        String result = RandomStringUtils.getHex("0x####", '#');
        assertEquals(6, result.length());
        assertTrue(Pattern.matches("^0x[0-9A-F]{4}$", result));
    }

    @Test
    void testGetNextVal() throws InterruptedException {
        // 测试唯一码生成
        String result = RandomStringUtils.getNextVal();
        assertEquals(26, result.length());
        assertTrue(Pattern.matches("^\\d{26}$", result));

        // 测试并发情况下的唯一性
        int threadCount = 10;
        int iterationCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount * iterationCount);

        // 存储生成的唯一码
        java.util.Set<String> uniqueCodes = java.util.Collections.synchronizedSet(new java.util.HashSet<>());

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < iterationCount; j++) {
                    String code = RandomStringUtils.getNextVal();
                    assertTrue(uniqueCodes.add(code), "Duplicate code generated: " + code);
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
        assertEquals(threadCount * iterationCount, uniqueCodes.size());
    }

    @Test
    void testSubStringByLength() {
        // 测试正常分割
        List<String> result = RandomStringUtils.subStringByLength("123456789", 3);
        assertEquals(3, result.size());
        assertEquals("123", result.get(0));
        assertEquals("456", result.get(1));
        assertEquals("789", result.get(2));

        // 测试不能整除的情况
        result = RandomStringUtils.subStringByLength("1234567890", 3);
        assertEquals(4, result.size());
        assertEquals("123", result.get(0));
        assertEquals("456", result.get(1));
        assertEquals("789", result.get(2));
        assertEquals("0", result.get(3));

        // 测试空字符串
        result = RandomStringUtils.subStringByLength("", 3);
        assertEquals(0, result.size());
    }

    @Test
    void testSubStringByLengthAndSize() {
        // 测试指定大小分割
        List<String> result = RandomStringUtils.subStringByLengthAndSize("123456789", 3, 3);
        assertEquals(3, result.size());
        assertEquals("123", result.get(0));
        assertEquals("456", result.get(1));
        assertEquals("789", result.get(2));

        // 测试size大于实际需要的大小
        result = RandomStringUtils.subStringByLengthAndSize("123456", 3, 3);
        assertEquals(3, result.size());
        assertEquals("123", result.get(0));
        assertEquals("456", result.get(1));
        assertNull(result.get(2));
    }

    @Test
    void testSubstring() {
        // 测试正常截取
        assertEquals("234", RandomStringUtils.substring("12345", 1, 4));

        // 测试起始位置超出字符串长度
        assertNull(RandomStringUtils.substring("12345", 6, 8));

        // 测试结束位置超出字符串长度
        assertEquals("45", RandomStringUtils.substring("12345", 3, 8));
    }
}