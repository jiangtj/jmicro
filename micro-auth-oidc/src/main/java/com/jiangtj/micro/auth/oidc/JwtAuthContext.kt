package com.jiangtj.micro.auth.oidc

import com.jiangtj.micro.auth.context.DefaultAuthContext
import com.jiangtj.micro.auth.context.Subject
import io.jsonwebtoken.Claims

class JwtAuthContext(val payload: Claims): DefaultAuthContext(Subject.builder()
    .id(payload.subject)
    .name(payload.get("preferredUsername", String::class.java))
    .displayName(payload.get("name", String::class.java))
    .avatar(payload.get("picture", String::class.java))
    .type(payload.get("type", String::class.java))
    .issuer(payload.issuer)
    .build()) {
}