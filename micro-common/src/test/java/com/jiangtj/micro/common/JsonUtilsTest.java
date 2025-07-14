package com.jiangtj.micro.common;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.time.*;

@Slf4j
class JsonUtilsTest {

    @Test
    void toJson() {
        val text = JsonUtils.toJson(new JsonUtilsKtTest.Pair2(1, "2"));
        log.info(text);
    }
    @Test
    void fromJson() {
        val fromJson = JsonUtils.fromJson("{\"first\":1,\"second\":\"2\"}", JsonUtilsKtTest.Pair2.class);
        log.info(String.valueOf(fromJson));
    }

    @Test
    void getListFromJson() {
        val fromJson = JsonUtils.getListFromJson("[{\"first\":1,\"second\":\"2\"},{\"first\":3,\"second\":\"x\"}]", JsonUtilsKtTest.Pair2.class);
        log.info(String.valueOf(fromJson));
    }

}