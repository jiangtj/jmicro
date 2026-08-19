package com.jiangtj.micro.business.cas

import com.jiangtj.micro.auth.oidc.getKid
import com.jiangtj.micro.common.utils.UUIDUtils
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Locator
import io.jsonwebtoken.security.EcPrivateJwk
import io.jsonwebtoken.security.EcPublicJwk
import io.jsonwebtoken.security.Jwks
import org.springframework.core.annotation.Order
import java.security.Key
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey

const val ORDER = -10

@Order(ORDER)
class OidcKeyService(private val oidcServerProperties: OidcServerProperties) : Locator<Key> {

    var pair: KeyPair? = null
    var jwk: EcPrivateJwk? = null

    fun getKid(): String = jwk!!.id

    fun getSignKey(): PrivateKey = pair!!.private

    fun getVerifyKey(): PublicKey = pair!!.public

    fun getPublicJwk(): EcPublicJwk = jwk!!.toPublicJwk()

    fun refreshKeys() {
        pair = Jwts.SIG.ES384.keyPair().build()
        jwk = Jwks.builder()
            .id((oidcServerProperties.kidPrefix ?: "") + UUIDUtils.generateBase64Compressed())
            .ecKeyPair(pair)
            .build()
    }

    override fun locate(header: Header): Key? {
        val kid = header.getKid() ?: return null
        if (kid == getKid()) {
            return getVerifyKey()
        }
        return null
    }
}
