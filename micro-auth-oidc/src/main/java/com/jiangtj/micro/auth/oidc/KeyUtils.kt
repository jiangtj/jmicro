package com.jiangtj.micro.auth.oidc

import io.jsonwebtoken.Header

fun Header.getKid(): String? {
    return this["kid"] as String?
}
