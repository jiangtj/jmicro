package com.jiangtj.micro.demobackend.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * demo 的 JWT 服务：基于 HS256 签发与校验 token。
 * <p>
 * 角色与权限直接写入 claims，服务端无需保存会话（无状态）。
 */
@Component
public class DemoJwtService {

    public static final String CLAIM_NAME = "name";
    public static final String CLAIM_TYPE = "type";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_PERMISSIONS = "permissions";

    /**
     * 标识 demo 自己签发的账号体系
     */
    public static final String SUBJECT_TYPE = "demo";

    private final SecretKey signKey;
    private final DemoJwtProperties properties;

    public DemoJwtService(DemoJwtProperties properties) {
        this.properties = properties;
        this.signKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 为登录成功的用户签发 JWT
     */
    public String createToken(DemoUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
            .issuer(properties.getIssuer())
            .subject(user.username())
            .claim(CLAIM_NAME, user.nickname())
            .claim(CLAIM_TYPE, SUBJECT_TYPE)
            .claim(CLAIM_ROLES, user.roles())
            .claim(CLAIM_PERMISSIONS, user.permissions())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(properties.getTtl())))
            .signWith(signKey, Jwts.SIG.HS256)
            .compact();
    }

    /**
     * 校验签名与有效期，失败（含格式错误、过期）返回 empty
     */
    public Optional<Claims> parse(String token) {
        try {
            return Optional.of(Jwts.parser()
                .verifyWith(signKey)
                .build()
                .parseSignedClaims(token)
                .getPayload());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public List<String> roles(Claims claims) {
        return stringList(claims, CLAIM_ROLES);
    }

    public List<String> permissions(Claims claims) {
        return stringList(claims, CLAIM_PERMISSIONS);
    }

    private List<String> stringList(Claims claims, String name) {
        if (claims.get(name) instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }
}
