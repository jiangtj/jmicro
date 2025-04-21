package com.jiangtj.micro.pic.upload.hw;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "hw.obs")
public class ObsProperties {
    private String key;
    private String secret;
    private String endPoint;
    private String bucketName;
}
