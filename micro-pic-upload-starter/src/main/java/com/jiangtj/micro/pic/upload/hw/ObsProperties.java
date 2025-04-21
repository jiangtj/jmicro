package com.jiangtj.micro.pic.upload.hw;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "obs")
public class ObsProperties {
    private String key;
    private String secret;
    private String endPoint;
    private String bucketName;
    private List<Dir> dirs;

    @Data
    public static class Dir {
        private Integer type;
        private String path;
        private String fileType = ".jpg,.jpeg,.gif,.png";
    }
}
