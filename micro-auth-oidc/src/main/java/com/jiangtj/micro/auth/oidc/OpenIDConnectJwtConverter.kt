package com.jiangtj.micro.auth.oidc

import com.fasterxml.jackson.databind.JsonNode
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.jiangtj.micro.auth.context.AuthContext
import com.jiangtj.micro.auth.context.AuthContextConverter
import com.jiangtj.micro.auth.context.AuthRequest
import com.jiangtj.micro.common.JsonUtils
import com.jiangtj.micro.common.exceptions.MicroException
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Jwks
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.security.Key
import java.util.concurrent.TimeUnit

class OpenIDConnectJwtConverter(private val wellKnownProperties: WellKnownProperties) : AuthContextConverter {

    data class OICF(
        val issuer: String,
        val jwks_uri: String
    )

    data class JwksSet(
        val keys: List<JsonNode>
    )

    val log = KotlinLogging.logger {}
    val rest = RestClient.create()

    val cache:Cache<String, Key> = Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.DAYS)
        .build()

    override fun convert(request: AuthRequest): AuthContext {
        val headers = request.getHeaders("Authorization")
        if (headers.isEmpty() || headers.size > 1) {
            return AuthContext.unLogin()
        }
        try {
            val parser: JwtParser = Jwts.parser()
                .keyLocator { header ->
                    val kid = header["kid"] as String
                    return@keyLocator cache.get(kid) {
                        val oicf = rest.get().uri(wellKnownProperties.openidConfiguration!!)
                            .retrieve()
                            .body<OICF>()
                        var rKey: Key? = null
                        oicf?.let { rest.get().uri(it.jwks_uri) }
                            ?.retrieve()
                            ?.body<JwksSet>()
                            ?.keys
                            ?.forEach { key ->
                                val parse = Jwks.parser().build().parse(JsonUtils.toJson(key))
                                val fetchKid = parse["kid"] as String
                                val fetchKey = parse.toKey()
                                cache.put(fetchKid, fetchKey)
                                if (fetchKid == kid) {
                                    rKey = fetchKey
                                }
                            }
                        return@get rKey ?: throw MicroException("no key")
                    }
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