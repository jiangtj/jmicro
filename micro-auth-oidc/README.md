# Micro Auth OIDC

基于 PKCE 流程的轻量权限控制模块

我们推荐使用第三方开源的 OIDC 认证服务，如 logto, keycloak, casdoor, zitadel 等, 并在 Web 单页应用中认证获取 id_token, 然后在后端验证 token 有效性。

## 配置

```properties
jmicro.jwt.oidc[0].matcher-style=prefix
# 这将匹配 /custom-oidc-key/*** 的 kid 使用 openid-configuration 获取 public key
jmicro.jwt.oidc[0].pattern=custom-oidc-key/
jmicro.jwt.oidc[0].openid-configuration=https://your-oidc-server/oidc/.well-known/openid-configuration
```

## Cas

这是我们在某次开发中遇到的问题时的处理方案，你可以不使用这块内容，默认是关闭的

启用后，我们会在该应用中实现一个简化版的 oidc 服务，目的是为了实现，单页 web 应用中也能安全可靠的获取 cas 应用中的登录状态
