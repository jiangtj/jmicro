/**
 * OIDC 支持模块
 *
 * <p>提供基于 PKCE 流程的轻量级 OIDC token 验证能力，支持通过 openid-configuration
 * 动态获取并缓存 public key，并通过 kid 定位对应的验证密钥。</p>
 */
@NullMarked
package com.jiangtj.micro.auth.oidc;

import org.jspecify.annotations.NullMarked;
