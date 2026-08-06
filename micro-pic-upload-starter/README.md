# J Micro Pic Upload Starter

![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-pic-upload-starter/api)

图片上传功能的 Spring Boot Starter，提供简单易用的图片上传服务。配置好参数即可将图片转换为可访问 URL。

## 目录

- [功能特性](#功能特性)
- [使用方法](#使用方法)
  - [添加依赖](#添加依赖)
  - [通用配置](#通用配置)
  - [各服务商配置](#各服务商配置)
  - [在代码中使用](#在代码中使用)
- [返回结果](#返回结果)

## 功能特性

- 支持多种图片格式（jpg、jpeg、png、gif、webp）
- 自动生成随机文件名
- 可配置上传路径与目录映射
- 使用官方 SDK，安全可靠

目前支持以下服务商上传图片：

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

> 建议配合 `micro-dependencies` BOM 使用，省略版本号（见[根 README](../README.md)）。

### 通用配置

在 `application.properties` 或 `application.yml` 中添加配置，决定文件的上传位置：

```properties
# 上传提供者，可选值：local, ali, hw, minio, easyimages
micro.pic.upload.provider=minio
# 默认允许的文件类型
micro.pic.upload.allowed-extensions=jpg,jpeg,png,gif,webp
# 最大文件大小（字节）
micro.pic.upload.max-file-size=5242880
# 上传路径（必填）。target-name 可替换为任意名称，同时支持覆盖默认配置
micro.pic.upload.dirs.target-name.path=/common
# micro.pic.upload.dirs.target-name.provider=ali
```

不同 `provider` 对应不同的属性命名空间，并可能需要引入对应 SDK：

| Provider | 命名空间 | 说明 |
| --- | --- | --- |
| `local` | `micro.pic.upload.dirs.*` | 本地磁盘上传，无需额外 SDK |
| `ali` | `ali.oss.*` | 阿里云 OSS |
| `hw` | `hw.obs.*` | 华为云 OBS |
| `minio` | `minio.*` | 兼容 S3 的 MinIO |
| `easyimages` | `easyimages.api.*` | EasyImages 2.0（纯 HTTP API，无需 SDK） |

### 各服务商配置

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

#### 本地上传（local）

无需引入额外 SDK，配置 `provider=local` 并指定目录路径与访问 URL 即可：

```properties
micro.pic.upload.provider=local
# 本地存储根路径
micro.pic.upload.dirs.target-name.path=/data/uploads
# 对应的可访问 URL 前缀（由你的静态资源映射暴露）
micro.pic.upload.dirs.target-name.url-prefix=/files
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

上传成功后返回的 `PicUploadResult` 对象包含以下信息：

- `originalFileName`：原始文件名
- `fileName`：新文件名
- `fileUrl`：文件访问路径
- `thumbnailUrl`：缩略图路径（如果存在）
