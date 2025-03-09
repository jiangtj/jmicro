package com.jiangtj.micro.common;

import com.jiangtj.micro.common.stream.DistinctWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class StreamTest {

    @Test
    public void testDistinctWrapper() {
        List<User> users = Arrays.asList(
                new User(1, "n1"),
                new User(2, "n2"),
                new User(1, "n3"));
        long count1 = users.stream().distinct().count();
        Assertions.assertEquals(3, count1);
        long count2 = users.stream()
                .map(user -> new DistinctWrapper<>(user, User::id))
                .distinct()
                .map(DistinctWrapper::value)
                .count();
        Assertions.assertEquals(2, count2);

    }

    record User(int id, String name) {
    }

}