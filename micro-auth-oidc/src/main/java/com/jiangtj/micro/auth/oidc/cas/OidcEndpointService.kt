package com.jiangtj.micro.auth.oidc.cas

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.jiangtj.micro.common.utils.UUIDUtils
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Encoders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import java.net.URI
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit

class OidcEndpointService(
    private val oidcServerProperties: OidcServerProperties,
    private val oidcKeyService: OidcKeyService,
    private val oidcRedirectAuth: OidcRedirectAuth
) {

    private val authCodes: Cache<String, AuthCodeData> = Caffeine.newBuilder()
        .expireAfterWrite(15, TimeUnit.MINUTES)
        .build()

    fun endpoints(): RouterFunction<ServerResponse> = router {
        // OIDC 发现文档端点
        GET(oidcServerProperties.wellKnown) { request ->
            val baseUri = URI.create(oidcServerProperties.baseUrl ?: getBaseUrl(request))
            val discoveryDocument = mapOf(
                "issuer" to baseUri,
                "authorization_endpoint" to baseUri.resolve(oidcServerProperties.authorizationEndpoint),
                "token_endpoint" to baseUri.resolve(oidcServerProperties.tokenEndpoint),
                "jwks_uri" to baseUri.resolve(oidcServerProperties.jwksUri),
                "response_types_supported" to listOf(
                    "code",
                    "token",
                    "id_token",
                    "code token",
                    "code id_token",
                    "token id_token",
                    "code token id_token"
                ),
                "subject_types_supported" to listOf("public"),
                "id_token_signing_alg_values_supported" to listOf("ES384"),
                "scopes_supported" to listOf("openid", "profile", "email"),
                "token_endpoint_auth_methods_supported" to listOf("private_key_jwt"),
                "claims_supported" to listOf("sub", "iss", "auth_time", "name", "email", "preferred_username"),
                "grant_types_supported" to listOf("authorization_code", "implicit", "refresh_token"),
                "response_modes_supported" to listOf("query", "fragment"),
                "claim_types_supported" to listOf("normal"),
                "code_challenge_methods_supported" to listOf("S256"),
                "authorization_response_iss_parameter_supported" to true
            )
            ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(discoveryDocument)
        }

        // JWKS 端点
        GET(oidcServerProperties.jwksUri) { _ ->
            val jwks = mapOf(
                "keys" to listOf(oidcKeyService.getPublicJwk())
            )
            ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(jwks)
        }

        // 授权端点
        GET(oidcServerProperties.authorizationEndpoint) { request ->
            val params = request.params()
            val redirectUri = params.getFirst("redirect_uri") ?: ""
            val codeChallenge = params["code_challenge"]?.first() ?: ""
            val state = params.getFirst("state")
            val nonce = params.getFirst("nonce")

            // 验证必要参数
            if (redirectUri.isEmpty() || codeChallenge.isEmpty()) {
                return@GET ServerResponse.badRequest()
                    .body("Missing required parameters: redirect_uri, or code_challenge")
            }

            // 验证回调地址
            val callbackUri = oidcServerProperties.callbackUri
            if (callbackUri.isNotEmpty()) {
                if (!callbackUri.contains(redirectUri)) {
                    return@GET ServerResponse.badRequest()
                        .body("Callback uri not match: $redirectUri")
                }
            }

            val user = oidcRedirectAuth.userInfo()
            val authCode = AuthCodeData(
                codeChallenge = codeChallenge,
                state = state,
                nonce = nonce,
                user = user
            )
            val code = UUIDUtils.generateBase64Compressed()
            authCodes.put(code, authCode)

            ServerResponse.temporaryRedirect(
                URI.create("$redirectUri?code=$code${state?.let { "&state=$it" } ?: ""}")
            ).build()

        }

        // 令牌端点
        POST(oidcServerProperties.tokenEndpoint) { request ->
            val baseUri = URI.create(oidcServerProperties.baseUrl ?: getBaseUrl(request))
            val params = request.params()
            val code = params["code"]?.firstOrNull() ?: ""
            val codeVerifier = params["code_verifier"]?.firstOrNull() ?: ""

            // 验证必要参数
            if (code.isEmpty() || codeVerifier.isEmpty()) {
                return@POST ServerResponse.badRequest()
                    .body("Missing required parameters: code, or code_verifier")
            }

            // 验证授权码
            val authCodeData = authCodes.getIfPresent(code)
                ?: return@POST ServerResponse.badRequest()
                    .body("Invalid authorization code")

            val digest = MessageDigest.getInstance("SHA-256")
            val bytes = digest.digest(codeVerifier.toByteArray())
            val calcCodeChallenge = Encoders.BASE64URL.encode(bytes)
            if (calcCodeChallenge != authCodeData.codeChallenge) {
                return@POST ServerResponse.badRequest()
                    .body("Invalid code verifier")
            }

            // 生成ID令牌和访问令牌
            val idToken = generateIdToken(baseUri.toString(), authCodeData.user, authCodeData.nonce)

            val tokenResponse = mapOf(
                "access_token" to "none",
                "token_type" to "Bearer",
                "expires_in" to 7200,
                "refresh_token" to "none",
                "id_token" to idToken,
                "scope" to "openid profile"
            )

            // 移除已使用的授权码
            authCodes.invalidate(code)

            ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(tokenResponse)
        }
    }

    private fun getBaseUrl(request: ServerRequest): String {
        val uri = request.uri()
        return "${uri.scheme}://${uri.host}:${uri.port}"
    }

    private fun generateIdToken(issuer: String, user: Map<String, Any?>, nonce: String?): String {
        // 生成真实的JWT ID令牌
        val now = System.currentTimeMillis()
        val expiration = now + 3600 * 1000 * 24 // 24小时后过期

        return Jwts.builder()
            .header()
            .keyId(oidcKeyService.getKid())
            .and()
            .claims(user)
            .issuer(issuer)
            .expiration(Date(expiration))
            .notBefore(Date(now))
            .issuedAt(Date(now))
            .apply { nonce?.let { claim("nonce", it) } }
            .signWith(oidcKeyService.getSignKey())
            .compact()
    }

    // 授权码数据类
    data class AuthCodeData(
        val codeChallenge: String,
        val state: String? = null,
        val nonce: String? = null,
        val timestamp: Long = System.currentTimeMillis(),
        val user: Map<String, Any?>,
    )
}