package com.jiangtj.micro.demobackend.auth;

import com.jiangtj.micro.web.BaseExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * demo 账户存储（内存实现，重启后失效）
 */
@Component
public class DemoUserStore {

    private final Map<String, DemoUser> users = new LinkedHashMap<>();
    private final Map<String, DemoUser> tokens = new ConcurrentHashMap<>();

    public DemoUserStore() {
        // 管理员：拥有全部图书权限
        register(new DemoUser("admin", "admin123", "管理员", List.of("admin"), List.of("book:*")));
        // 普通读者：只能查看图书
        register(new DemoUser("reader", "reader123", "读者", List.of("reader"), List.of("book:read")));
    }

    private void register(DemoUser user) {
        users.put(user.username(), user);
    }

    /**
     * 校验用户名密码，失败抛出 401
     */
    public DemoUser login(String username, String password) {
        DemoUser user = users.get(username);
        if (user == null || !user.password().equals(password)) {
            throw BaseExceptionUtils.unauthorized("用户名或密码错误");
        }
        return user;
    }

    /**
     * 登录成功后签发 token（demo 直接使用随机字符串）
     */
    public String createToken(DemoUser user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokens.put(token, user);
        return token;
    }

    public Optional<DemoUser> findByToken(String token) {
        return Optional.ofNullable(tokens.get(token));
    }
}
