package com.jiangtj.micro.business.cas

interface OidcRedirectAuth {

    fun userInfo(authContext: AuthContext): Map<String, Any?>

}
