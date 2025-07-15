package com.jiangtj.micro.auth.oidc

import com.jiangtj.micro.auth.context.DefaultAuthContext
import com.jiangtj.micro.auth.context.Subject
import io.jsonwebtoken.Claims

class JwtAuthContext(val payload: Claims): DefaultAuthContext(Subject.builder()
    .name(payload.subject)
    .type(payload.get("type", String::class.java))
    .issuer(payload.issuer)
    .build()) {
}