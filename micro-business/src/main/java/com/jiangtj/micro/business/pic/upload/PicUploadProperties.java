package com.jiangtj.micro.business.pic.upload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jiangtj.micro.business.config.jackson.DataSizeDeserializer;
import com.jiangtj.micro.business.config.jackson.DataSizeSerializer;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

/**
 * 图片上传配置属性
 *
 * `dirs` 既能通过 {@code jmicro.pic.upload.dirs.*} 绑定，也能作为系统配置项
 * {@code jmicro.pic.upload.dirs}（JSON）在运行期覆盖，见
 * {@link PicUploadDirsConfigLoader}。
 */
@Data
@ConfigurationProperties(prefix = "jmicro.pic.upload")
public class PicUploadProperties {

    /**
     * 默认的文件上传提供者
     */
    private String provider = "local";

    /**
     * 默认允许的文件类型
     */
    private String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "webp", "bmp", "svg", "avif"};

    /**
     * 默认最大文件大小 10mb
     */
    private DataSize maxFileSize = DataSize.ofMegabytes(10);

    private Map<String, Dir> dirs = new HashMap<>();

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Dir {
        private String path;
        @Nullable
        private String provider;
        @Nullable
        @JsonSerialize(using = DataSizeSerializer.class)
        @JsonDeserialize(using = DataSizeDeserializer.class)
        private DataSize maxFileSize;
        @Nullable
        private String[] allowedExtensions;

        public String resolve(String fileName) {
            if (path.endsWith("/")) {
                return path + fileName;
            }
            return path + "/" + fileName;
        }
    }
}
