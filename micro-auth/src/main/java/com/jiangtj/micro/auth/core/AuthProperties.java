package com.jiangtj.micro.auth.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("jmicro.auth")
public class AuthProperties {
    /**
     * 权限的匹配模式
     *   Simple: 默认模式，使用简单的字符串匹配
     *   Ant: 使用 Ant 风格的路径匹配模式
     */
    private MatchMode permissionMatch = MatchMode.Simple;

    /**
     * 路径分隔符，默认 ':'
     */
    private String pathSeparator = ":";

    public enum MatchMode {
        Simple, Ant
    };
}
