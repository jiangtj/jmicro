# 图片上传（micro-business，包 com.jiangtj.micro.business.pic）

## 前提

- 依赖 `com.jiangtj.micro:micro-business`。
- **必须开启系统配置**：`jmicro.config.enabled=true`，否则各 provider 上传服务不会注册（所有 provider 配置项都由 JMicroConfig 提供）。
- 启动配置只有开关：`jmicro.pic.upload.enable`（默认 `true`）。
- 需要 `spring-boot-starter-webmvc`（`MultipartFile`）与 multipart 配置：`spring.servlet.multipart.max-file-size`（demo `10MB`）、`spring.servlet.multipart.max-request-size`（demo `20MB`）。Spring 默认单文件 1MB，超限直接 413，需与 `jmicro.pic.upload.max-file-size` 同时放开。

## 用法

```kotlin
@RestController
@RequestMapping("pic")
class PicUploadController(private val picUploadService: PicUploadService) {
    @PostMapping("upload")
    fun upload(target: String, file: MultipartFile): PicUploadResult =
        picUploadService.upload(target, file)
}
```

- `target`：目录映射的 key，对应系统配置项 `jmicro.pic.upload.dirs` 的 Map key；未匹配时回退默认 provider 与默认限制。
- 返回 `PicUploadResult`：`originalFileName`、`fileName`、`fileUrl`、`thumbnailUrl`。

## 通用配置项（由 PicUploadConfigLoader 注册）

| 键 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| `jmicro.pic.upload.provider` | TEXT | `local` | local / ali / hw / minio / easyimages / rustfs |
| `jmicro.pic.upload.allowed-extensions` | TEXT | `jpg,jpeg,png,gif,webp,bmp,svg,avif` | 逗号分隔 |
| `jmicro.pic.upload.max-file-size` | TEXT | `10MB` | 支持 `10MB` / `1KB` / 字节数 |
| `jmicro.pic.upload.dirs` | JSON | `{}` | `Map<String, Dir>`，`Dir.path` 必填，可选 `provider`、`maxFileSize` |

配置方式（两种等价）：

```properties
jmicro.config.enabled=true
jmicro.config.kv.jmicro.pic.upload.provider=local
jmicro.config.kv.jmicro.pic.upload.max-file-size=5MB
jmicro.config.kv.jmicro.pic.upload.dirs={"avatar":{"path":"/avatar"}}
```

```kotlin
jmicroConfigService.updateConfig("jmicro.pic.upload.provider", "local")
jmicroConfigService.updateMapEntry("jmicro.pic.upload.dirs", "avatar", mapOf("path" to "/avatar"))
```

## 本地 provider（默认）

```properties
jmicro.config.kv.jmicro.pic.upload.local.upload-path=/data/upload
jmicro.config.kv.jmicro.pic.upload.local.url=http://localhost:8080
```

`upload-path` 默认 `java.io.tmpdir`，`url` 默认 `http://localhost:8080`（用于拼接访问地址）。

## 其他 provider

| Provider | 配置项前缀 | 额外 SDK |
| --- | --- | --- |
| `ali` | `ali.oss.*` | `com.aliyun.oss:aliyun-sdk-oss` |
| `hw` | `hw.obs.*` | 华为云 OBS SDK |
| `minio` | `minio.*` | `io.minio:minio` |
| `easyimages` | `easyimages.api.*` | 无（纯 HTTP） |
| `rustfs` | `jmicro.rustfs.*`（需 `jmicro.rustfs.enable=true`） | AWS S3 SDK |

MinIO 示例：

```properties
jmicro.config.enabled=true
jmicro.config.kv.minio.endpoint=http://localhost:9000
jmicro.config.kv.minio.access-key=access-key
jmicro.config.kv.minio.secret-key=secret-key
jmicro.config.kv.minio.bucket=bucket
jmicro.config.kv.minio.is-allow-get-object=false
jmicro.config.kv.jmicro.pic.upload.provider=minio
```

provider SDK 在 `micro-business` 中是 `compileOnly`，使用哪个就显式引入哪个；每次上传实时读取配置，改配置无需重启。

## 注意

- 上传路径遍历、扩展名白名单、大小限制由 `PicUploadService` 统一校验，不要在外层重复实现。
- 多实例部署时本地 provider 需共享存储或换对象存储。
