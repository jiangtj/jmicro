package com.jiangtj.micro.business.oidc.cas

interface OidcRedirectAuth {

    fun userInfo(): Map<String, Any?>

}
