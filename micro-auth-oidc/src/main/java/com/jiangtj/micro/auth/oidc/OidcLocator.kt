package com.jiangtj.micro.auth.oidc

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.jiangtj.micro.auth.oidc.cas.OidcKeyService
import com.jiangtj.micro.common.JsonUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Header
import io.jsonwebtoken.Locator
import io.jsonwebtoken.security.Jwks
import org.springframework.core.annotation.Order
import org.springframework.util.AntPathMatcher
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.security.Key
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

const val ORDER: Int = 10000

@Order(ORDER)
class OidcLocator(private val jwtProperties: JwtProperties, private val oidcKeyService: OidcKeyService?) :
    Locator<Key> {

    data class OICF(
        val issuer: String,
        val jwks_uri: String
    )

    data class JwksSet(
        val keys: List<*>
    )

    val rest = RestClient.create()

    val cache: Cache<String, Key> = Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.DAYS)
        .build()

    override fun locate(header: Header): Key? {
        val kid = header.getKid() ?: return null

        if (oidcKeyService != null) {
            if (kid == oidcKeyService.getKid()) {
                return oidcKeyService.getVerifyKey()
            }
        }

        val key = cache.getIfPresent(kid)
        if (key != null) {
            return key
        }

        jwtProperties.oidc.forEach {
            if (match(it, kid)) {
                log.debug { "matched oidc configuration: $it" }
                val key = handle(it, kid)
                if (key != null) {
                    return key
                }
                log.debug { "no key for oidc kid: $kid" }
            }
        }
        log.debug { "no matched oidc for kid: $kid" }
        return null
    }

    fun match(oidc: OidcProperties, kid: String): Boolean {
        if (oidc.matcherStyle == MatcherStyle.ALWAYS) {
            return true
        }
        if (oidc.matcherStyle == MatcherStyle.ANT) {
            return AntPathMatcher(oidc.pathSeparator).match(oidc.pattern, kid)
        }
        if (oidc.matcherStyle == MatcherStyle.REGEX) {
            return oidc.pattern.toRegex().matches(kid)
        }
        if (oidc.matcherStyle == MatcherStyle.PREFIX) {
            return kid.startsWith(oidc.pattern)
        }
        return false
    }

    fun handle(oidc: OidcProperties, kid: String): Key? {
        var jwksUri = oidc.jwksUri

        if (jwksUri == null) {
            val oicf = rest.get().uri(oidc.openidConfiguration)
                .retrieve()
                .body<OICF>()
            log.debug { "oidc configuration: $oicf" }
            jwksUri = oicf?.jwks_uri
        }

        if (jwksUri == null) {
            log.debug { "no jwks_uri for oidc: $oidc" }
            return null
        }

        rest.get().uri(jwksUri)
            .retrieve()
            .body<JwksSet>()
            ?.keys
            ?.forEach { key ->
                val parse = Jwks.parser().build().parse(JsonUtils.toJson(key))
                log.debug { "oidc key: $parse" }
                val fetchKid = parse["kid"] as String
                val fetchKey = parse.toKey()
                cache.put(fetchKid, fetchKey)
            }

        return cache.getIfPresent(kid)
    }
}