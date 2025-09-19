package com.jiangtj.micro.auth.oidc

import com.jiangtj.micro.auth.context.AuthContext
import com.jiangtj.micro.auth.context.AuthContextConverter
import com.jiangtj.micro.auth.context.AuthRequest
import com.jiangtj.micro.common.exceptions.MicroException
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Locator
import java.security.Key

class JwtConverter(private val locators: List<Locator<out Key>>) : AuthContextConverter {

    val log = KotlinLogging.logger {}

    override fun convert(request: AuthRequest): AuthContext {
        val headers = request.getHeaders("Authorization")
        if (headers.isEmpty() || headers.size > 1) {
            return AuthContext.unLogin()
        }
        try {
            val parser: JwtParser = Jwts.parser()
                .keyLocator { header ->
                    locators.forEach { locator ->
                        val key = locator.locate(header)
                        if (key != null) {
                            return@keyLocator key
                        }
                    }
                    throw MicroException("no key")
                }
                .build()
            val claims = parser.parseSignedClaims(headers[0].substring(7))
            val payload = claims.payload
            return JwtAuthContext(payload)
        } catch (e: Exception) {
            log.error(e) { "parse jwt failed" }
            return AuthContext.unLogin()
        }
    }
}