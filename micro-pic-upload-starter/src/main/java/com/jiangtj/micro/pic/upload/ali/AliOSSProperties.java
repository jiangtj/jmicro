package com.jiangtj.micro.pic.upload.ali;

import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("ali.oss")
public class AliOSSProperties {
    // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
    private String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
    // 填写Bucket名称，例如examplebucket。
    private String bucketName = "examplebucket";
    // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
//    private String objectName = "exampledir/exampleobject.txt";
    // 填写网络流地址。
    private String url = "https://www.aliyun.com/";
    // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。
    private String region = "cn-hangzhou";

    @Nullable
    private String accessKeyId;
    @Nullable
    private String secretAccessKey;
}
