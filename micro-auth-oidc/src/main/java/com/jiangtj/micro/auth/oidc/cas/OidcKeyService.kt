package com.jiangtj.micro.auth.oidc.cas

import com.jiangtj.micro.common.utils.UUIDUtils
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.EcPrivateJwk
import io.jsonwebtoken.security.EcPublicJwk
import io.jsonwebtoken.security.Jwks
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey

class OidcKeyService(private val oidcServerProperties: OidcServerProperties) {

    var pair: KeyPair? = null
    var jwk: EcPrivateJwk? = null

    fun getKid(): String = jwk!!.id

    fun getSignKey(): PrivateKey = pair!!.private

    fun getVerifyKey(): PublicKey = pair!!.public

    fun getPublicJwk(): EcPublicJwk = jwk!!.toPublicJwk()

    fun refreshKeys() {
        pair = Jwts.SIG.ES384.keyPair().build()
        jwk = Jwks.builder()
            .id((oidcServerProperties.kidPrefix?.let { "$it/" } ?: "")
                    + UUIDUtils.generateBase64Compressed())
            .ecKeyPair(pair)
            .build()
    }
}