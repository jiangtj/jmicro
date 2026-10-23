package com.jiangtj.micro.demobackend.auth;

import java.util.List;

/**
 * demo 账户，包含角色与权限
 */
public record DemoUser(String username, String password, String nickname,
                       List<String> roles, List<String> permissions) {
}
