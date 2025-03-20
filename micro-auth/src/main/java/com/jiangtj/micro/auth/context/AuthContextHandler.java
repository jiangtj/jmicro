package com.jiangtj.micro.auth.context;

/**
 * 处理转换后的 AuthContext
 * 有时候在 AuthContext 转换时，只是基础的用户信息，我们可以通过 AuthContextHandler 处理其他信息，比如权限信息，角色信息等
 */
public interface AuthContextHandler {
    void handle(AuthContext ctx);
}
