package com.jiangtj.micro.business.cas

data class AuthContext(
    val clientId: String? = null,
    val redirectUri: String,
    val scope: String,
)
