package com.jiangtj.micro.pic.upload.easyimages;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "easyimages.api")
public class EasyImagesProperties {
    private String url;    // API服务器地址
    private String token;  // API Token
}