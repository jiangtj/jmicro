package com.jiangtj.micro.auth.oidc.cas

import io.jsonwebtoken.Claims

interface OidcRedirectAuth {

    fun userInfo(): Claims

}