# OIDC 授权码模式实现总结

## 概述

本项目在 `OidcEndpointService` 中实现了 OIDC 授权码模式（Authorization Code Flow），支持 PKCE（Proof Key for Code Exchange）扩展。

## 核心功能

### 1. 授权端点 (`/oidc/auth`)

**支持的参数：**
- `client_id`: 客户端标识符
- `redirect_uri`: 重定向 URI
- `response_type`: 响应类型（仅支持 `code`）
- `scope`: 授权范围
- `state`: 防 CSRF 状态参数
- `code_challenge`: PKCE 代码挑战
- `code_challenge_method`: PKCE 方法（仅支持 `S256`）

**验证逻辑：**
- 验证 `response_type` 必须为 `code`
- 验证必需参数（`client_id`, `redirect_uri`, `response_type`）
- 验证 `code_challenge_method` 必须为 `S256`
- 验证客户端配置存在且 `redirect_uri` 匹配配置的回调 URI

**响应：**
- 成功：生成授权码并重定向到指定 URI，包含 `code` 和 `state` 参数
- 失败：重定向到错误 URI 或返回 400 错误

### 2. 令牌端点 (`/oidc/token`)

**支持的参数：**
- `grant_type`: 授权类型（仅支持 `authorization_code`）
- `code`: 授权码
- `client_id`: 客户端标识符
- `redirect_uri`: 重定向 URI
- `code_verifier`: PKCE 代码验证器

**验证逻辑：**
- 验证 `grant_type` 必须为 `authorization_code`
- 验证授权码存在且未过期
- 验证 `client_id` 和 `redirect_uri` 与授权请求时一致
- 验证 PKCE：`code_verifier` 的 SHA256 哈希值与 `code_challenge` 匹配

**响应：**
- 成功：返回访问令牌、ID 令牌和相关信息
- 失败：返回 OAuth2 标准错误响应

### 3. 发现文档 (`/.well-known/openid-configuration`)

**配置信息：**
- 仅支持 `authorization_code` 授权类型
- 仅支持 `code` 响应类型
- 仅支持 `query` 响应模式
- 包含所有端点 URL

### 4. JWKS 端点 (`/jwks`)

提供用于验证 ID 令牌的公钥信息。

## 配置

### 客户端配置
在 `application.properties` 中配置 OIDC 客户端：

```properties
# OIDC 服务器配置
oidc.server.enabled=true
oidc.server.base-url=http://localhost:17001
oidc.server.clients[0].client-id=demo-client
oidc.server.clients[0].client-secret=secret
oidc.server.clients[0].callback-uri[0]=http://localhost:3000/callback
oidc.server.clients[0].callback-uri[1]=http://localhost:8080/callback
```

### 数据模型

**AuthCodeData** 存储授权码相关信息：
- `code`: 授权码
- `clientId`: 客户端 ID
- `redirectUri`: 重定向 URI
- `scope`: 授权范围
- `codeChallenge`: PKCE 代码挑战
- `codeChallengeMethod`: PKCE 方法
- `expiry`: 过期时间

## 测试

### 1. 使用测试控制器
访问 `/oidc-test/auth-url` 生成授权 URL，包含 PKCE 参数。

### 2. 使用演示页面
打开 `http://localhost:17001/oidc-demo.html` 进行交互式测试。

### 3. 手动测试步骤

1. **生成 PKCE 参数：**
   ```
   Code Verifier: 随机字符串 (43-128 字符)
   Code Challenge: Base64URL(SHA256(Code Verifier))
   State: 随机字符串
   ```

2. **构建授权 URL：**
   ```
   http://localhost:17001/oidc/auth?
     client_id=demo-client&
     redirect_uri=http://localhost:3000/callback&
     response_type=code&
     scope=openid profile&
     code_challenge={code_challenge}&
     code_challenge_method=S256&
     state={state}
   ```

3. **获取访问令牌：**
   ```bash
   curl -X POST http://localhost:17001/oidc/token \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "grant_type=authorization_code" \
     -d "code={授权码}" \
     -d "client_id=demo-client" \
     -d "redirect_uri=http://localhost:3000/callback" \
     -d "code_verifier={code_verifier}"
   ```

## 安全特性

1. **PKCE 支持**：防止授权码拦截攻击
2. **状态参数**：防止 CSRF 攻击
3. **重定向 URI 验证**：确保回调地址安全
4. **授权码过期**：授权码有效期为 10 分钟
5. **一次性授权码**：授权码使用后立即失效

## 错误处理

所有错误都遵循 OAuth2 标准，包含 `error` 和 `error_description` 参数：
- `invalid_request`: 请求参数缺失或无效
- `unsupported_response_type`: 不支持的响应类型
- `invalid_client`: 客户端验证失败
- `invalid_grant`: 授权码无效或过期
- `invalid_request`: PKCE 验证失败

## 限制

当前实现仅支持授权码模式，不支持：
- 隐式模式（Implicit Flow）
- 混合模式（Hybrid Flow）
- 刷新令牌（Refresh Token）
- 客户端凭据模式（Client Credentials）

## 相关文件

- `OidcEndpointService.kt`: 核心服务实现
- `OidcServerProperties.java`: 服务器配置
- `OidcServerClientProperties.java`: 客户端配置
- `OidcTestController.java`: 测试控制器
- `oidc-demo.html`: 演示页面