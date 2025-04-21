# micro-pic-upload-starter

图片上传功能的 Spring Boot Starter，提供简单易用的图片上传服务。

## 功能特性

- 支持多种图片格式（jpg、jpeg、png、gif、webp）
- 文件大小限制
- 自动生成随机文件名
- 可配置上传路径
- 可选的缩略图生成功能

## 使用方法

### 添加依赖

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-pic-upload-starter</artifactId>
    <version>0.0.20</version>
</dependency>
```

### 配置属性

在`application.properties`或`application.yml`中配置：

```yaml
micro:
  pic:
    upload:
      upload-path: /path/to/upload/directory # 上传目录，默认为系统临时目录
      allowed-extensions: jpg,jpeg,png,gif,webp # 允许的文件类型
      max-file-size: 5242880 # 最大文件大小，默认5MB
      generate-thumbnail: false # 是否生成缩略图
      thumbnail-width: 200 # 缩略图宽度
      thumbnail-height: 200 # 缩略图高度
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
    public PicUploadResult uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return picUploadService.upload(file);
    }
}
```

## 返回结果

上传成功后返回的`PicUploadResult`对象包含以下信息：

- originalFileName: 原始文件名
- fileName: 新文件名
- filePath: 文件保存路径
- fileSize: 文件大小（字节）
- fileType: 文件类型
- thumbnailPath: 缩略图路径（如果生成）
