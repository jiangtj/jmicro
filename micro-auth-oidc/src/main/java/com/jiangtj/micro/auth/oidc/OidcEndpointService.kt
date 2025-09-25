package com.jiangtj.micro.auth.oidc

import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse

class OidcEndpointService(
    private val oidcServerProperties: OidcServerProperties
) {

    fun endpoints(): RouterFunction<ServerResponse> {
        TODO()
    }

}