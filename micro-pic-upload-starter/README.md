# micro-pic-upload-starter

图片上传功能的 Spring Boot Starter，提供简单易用的图片上传服务。

## 功能特性

- 支持多种图片格式（jpg、jpeg、png、gif、webp）
- 自动生成随机文件名
- 可配置上传路径
- 使用官方的 SDK, 安全可靠

目前支持以下服务商上传图片

- [x] 本地上传
- [x] 阿里云 OSS
- [x] 华为云 OBS
- [x] MinIO (aka S3)
- [x] EasyImages 2.0

## 使用方法

### 添加依赖

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-pic-upload-starter</artifactId>
</dependency>
```

### 配置属性

在`application.properties`或`application.yml`中添加配置，决定文件的上传位置

```properties
# 上传提供者，可选值：ali, hw, easyimages
micro.pic.upload.provider=minio
# 默认允许的文件类型
micro.pic.upload.allowed-extensions=jpg,jpeg,png,gif,webp
# 最大文件大小
micro.pic.upload.max-file-size=5242880
# 上传路径，必须, target-name 可以替换为任意的名称，同时支持覆盖默认配置
micro.pic.upload.dirs.target-name.path=/common
# micro.pic.upload.dirs.target-name.provider=ali
```

除此之外，还需要引入对于 provider 所需的 SDK，并配置相应的属性

#### MinIO

```xml

<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.17</version>
</dependency>
```

```properties
minio.endpoint=http://localhost:9000
minio.access-key=access-key
minio.secret-key=secret-key
minio.bucket=bucket
```

#### Ali OSS

```xml

<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.18.1</version>
</dependency>
```

```properties
ali.oss.endpoint=https://oss-cn-hangzhou.aliyuncs.com
ali.oss.region=cn-hangzhou
ali.oss.bucket-name=bucket-name
ali.oss.url=https://bucket-name.oss-cn-hangzhou.aliyuncs.com
# 你需要配置密钥，也可以从系统环境中读取
#ali.oss.access-key-id=access-key-id
#ali.oss.secret-access-key=secret-access-key
```

#### Huawei OBS

```xml

<dependency>
    <groupId>com.huaweicloud</groupId>
    <artifactId>esdk-obs-java-bundle</artifactId>
    <version>3.23.9.1</version>
</dependency>
```

```properties
hw.obs.end-point=https://obs.cn-north-1.myhuaweicloud.com
hw.obs.bucket-name=bucket-name
hw.obs.key=key
hw.obs.secret=secret
```

#### EasyImages 2.0

```properties
easyimages.api.url=http://localhost:40061/api/index.php
easyimages.api.token=token
```

### 在代码中使用

```java
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final PicUploadService picUploadService;

    public UploadController(PicUploadService picUploadService) {
        this.picUploadService = picUploadService;
    }

    @PostMapping("/image")
    public PicUploadResult uploadImage(@RequestParam("target") String target, @RequestParam("file") MultipartFile file) throws IOException {
        return picUploadService.upload(target, file);
    }
}
```

## 返回结果

上传成功后返回的`PicUploadResult`对象包含以下信息：

- originalFileName: 原始文件名
- fileName: 新文件名
- fileUrl: 文件访问路径
- thumbnailUrl: 缩略图路径（如果存在）
