package com.jiangtj.micro.business.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "system.config")
public class SystemConfigProperties {
    private Boolean enabled = false;
    private String filePath = "upload";
    private Map<String, String> kv = new HashMap<>();
}
