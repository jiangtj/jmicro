package com.jiangtj.micro.auth.oidc

interface OidcRedirectAuth {

    fun userInfo(): OidcEndpointService.UserInfo

}