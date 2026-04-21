package com.jiangtj.micro.pic.upload.rustfs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jmicro.rustfs")
public class RustFSProperties {
    private Boolean enable = false;
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private Boolean isAllowGetObject = false;
}
