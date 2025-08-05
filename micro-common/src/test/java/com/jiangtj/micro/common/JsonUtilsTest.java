package com.jiangtj.micro.common;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class JsonUtilsTest {

    @Test
    void toJson() {
        val text = JsonUtils.toJson(new JsonUtilsKtTest.Pair2(1, "2"));
        assertEquals("{\"first\":1,\"second\":\"2\"}", text);
    }
    @Test
    void fromJson() {
        val fromJson = JsonUtils.fromJson("{\"first\":1,\"second\":\"2\"}", JsonUtilsKtTest.Pair2.class);
        log.info(String.valueOf(fromJson));
        Assertions.assertNotNull(fromJson);
        assertEquals(1, fromJson.getFirst());
        assertEquals("2", fromJson.getSecond());
    }

    @Test
    void getListFromJson() {
        val fromJson = JsonUtils.getListFromJson("[{\"first\":1,\"second\":\"2\"},{\"first\":3,\"second\":\"x\"}]", JsonUtilsKtTest.Pair2.class);
        log.info(String.valueOf(fromJson));
        Assertions.assertNotNull(fromJson);
        assertEquals(2, fromJson.size());
        assertEquals(1, fromJson.get(0).getFirst());
        assertEquals("2", fromJson.get(0).getSecond());
    }

}