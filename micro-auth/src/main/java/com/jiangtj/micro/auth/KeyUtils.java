package com.jiangtj.micro.auth;

@Deprecated
public interface KeyUtils {

    static String toKey(String name) {
        return name.toLowerCase()
            .replace("-", "")
            .replace("_", "")
            .replace(" ", "");
    }

}
