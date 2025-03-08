
# J Micro

![author](https://img.shields.io/badge/author-mrtt-blue.svg)
[![email](https://img.shields.io/badge/email-jiang.taojie@foxmail.com-blue.svg)](mailto:jiang.taojie@foxmail.com)
![status](https://img.shields.io/badge/status-developing-yellow.svg)

[//]: # (![Maven Central Version]&#40;https://img.shields.io/maven-central/v/com.jiangtj.platform/parent&#41;)

J Micro 是一个轻量的 Spring Boot 模板，封装了常用功能，方便开发者快速开发。

### 使用

在开发阶段，建议使用本地安装。即 `clone` 后，`mvn install -P lib` 安装到本地

### 开发环境

#### 创建 casdoor

由 casdoor 提供认证和鉴权功能，下面命令行创建一个体验版的casdoor，你在线上部署时，请参考官方文档配置数据库。

```shell
docker run -p 28000:8000 -d casbin/casdoor-all-in-one
```

*开发环境demo-backend设置的端口是28000，不采用8000是考虑到，8000可能会有端口冲突*

##### 创建应用

访问 http://localhost:28000/ 添加应用，然后获取 clientId 和 clientSecret (todo 待完善)

