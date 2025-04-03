package com.jiangtj.micro.auth.wechat;

import io.jsonwebtoken.Jwts;
import lombok.Getter;

import javax.crypto.SecretKey;

public abstract class KeyHolder {

    @Getter
    static SecretKey key = Jwts.SIG.HS256.key().build();

    static public void setKey(SecretKey key) {
        key = Jwts.SIG.HS256.key().build();
    }

}
