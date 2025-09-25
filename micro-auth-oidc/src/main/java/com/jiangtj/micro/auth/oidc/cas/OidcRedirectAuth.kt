package com.jiangtj.micro.auth.oidc.cas

interface OidcRedirectAuth {

    fun userInfo(): OidcEndpointService.UserInfo

}