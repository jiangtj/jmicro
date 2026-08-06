package com.jiangtj.micro.business.cas

interface OidcRedirectAuth {

    fun userInfo(): Map<String, Any?>

}
