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
            if (!oidcServerProperties.isShowWellKnown) {
                return@GET ServerResponse.notFound().build()
            }
            val baseUri = URI.create(oidcServerProperties.baseUrl ?: getBaseUrl(request))
            val discoveryDocument = mapOf(
                "issuer" to baseUri,
                "authorization_endpoint" to baseUri.resolve(oidcServerProperties.authorizationEndpoint),
                "token_endpoint" to baseUri.resolve(oidcServerProperties.tokenEndpoint),
                "jwks_uri" to baseUri.resolve(oidcServerProperties.jwksUri),
                "response_types_supported" to listOf(
                    "code"
                ),
                "subject_types_supported" to listOf("public"),
                "id_token_signing_alg_values_supported" to listOf("ES384"),
                "scopes_supported" to listOf("openid", "profile", "email"),
                "token_endpoint_auth_methods_supported" to listOf("private_key_jwt"),
                "claims_supported" to listOf("sub", "iss", "auth_time", "name", "email", "preferred_username"),
                "grant_types_supported" to listOf("authorization_code"),
                "response_modes_supported" to listOf("query"),
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
            val clientId = params.getFirst("client_id") ?: ""
            val redirectUri = params.getFirst("redirect_uri") ?: ""
            val responseType = params.getFirst("response_type") ?: ""
            val codeChallenge = params.getFirst("code_challenge") ?: ""
            val codeChallengeMethod = params.getFirst("code_challenge_method") ?: "S256"
            val scope = params.getFirst("scope") ?: "openid"
            val state = params.getFirst("state")
            val nonce = params.getFirst("nonce")

            // 验证响应类型 - 只支持授权码模式
            /*if (responseType != "code") {
                return@GET handleAuthorizationError(
                    redirectUri,
                    "unsupported_response_type",
                    "Only 'code' is supported",
                    state
                )
            }*/

            // 验证必要参数
            if (redirectUri.isEmpty()) {
                return@GET handleAuthorizationError(
                    redirectUri,
                    "invalid_request",
                    "Missing required parameters: redirect_uri",
                    state
                )
            }

            // 验证挑战方法 - 只支持 S256
            /*if (codeChallengeMethod != "S256") {
                return@GET handleAuthorizationError(redirectUri, "invalid_request", "Unsupported code_challenge_method: $codeChallengeMethod. Only 'S256' is supported", state)
            }*/

            val clientCf = getClientCf(clientId)

            if (codeChallenge.isEmpty() && clientCf == null) {
                return@GET handleAuthorizationError(
                    redirectUri,
                    "invalid_client",
                    "Invalid client_id: $clientId",
                    state
                )
            }

            // 验证回调地址
            val callbackUris = clientCf?.callbackUri ?: emptyList()
            if (callbackUris.isNotEmpty()) {
                // 检查 redirectUri 是否在允许的回调地址列表中
                val isValidRedirectUri = callbackUris.any { allowedUri ->
                    // 完全匹配或者以允许的前缀开头
                    redirectUri == allowedUri || redirectUri.startsWith(allowedUri)
                }
                if (!isValidRedirectUri) {
                    return@GET handleAuthorizationError(
                        redirectUri,
                        "invalid_request",
                        "Invalid redirect_uri: $redirectUri",
                        state
                    )
                }
            }

            val user = oidcRedirectAuth.userInfo()
            val authCode = AuthCodeData(
                clientId = clientId,
                redirectUri = redirectUri,
                scope = scope,
                codeChallenge = codeChallenge,
                codeChallengeMethod = codeChallengeMethod,
                state = state,
                nonce = nonce,
                user = user
            )
            val code = UUIDUtils.generateBase64Compressed()
            authCodes.put(code, authCode)

            var u = redirectUri
            if (u.contains("?")) {
                u += "&"
            } else {
                u += "?"
            }
            u += "code=$code"
            if (state != null) {
                u += "&state=$state"
            }

            ServerResponse.temporaryRedirect(URI.create(u)).build()

        }

        // 令牌端点
        POST(oidcServerProperties.tokenEndpoint) { request ->
            val baseUri = URI.create(oidcServerProperties.baseUrl ?: getBaseUrl(request))
            val params = request.params()
            val code = params.getFirst("code") ?: ""
            val clientId = params.getFirst("client_id") ?: ""
            val redirectUri = params.getFirst("redirect_uri") ?: ""
            val codeVerifier = params.getFirst("code_verifier") ?: ""
            val grantType = params.getFirst("grant_type") ?: "authorization_code"
            val state = params.getFirst("state")

            // 验证授权类型 - 只支持 authorization_code
            if (grantType != "authorization_code") {
                return@POST ServerResponse.badRequest()
                    .body("Unsupported grant_type: $grantType. Only 'authorization_code' is supported.")
            }

            // 验证必要参数
            if (code.isEmpty()) {
                return@POST ServerResponse.badRequest()
                    .body("Missing required parameters: code")
            }

            // 验证授权码
            val authCodeData = authCodes.getIfPresent(code)
                ?: return@POST ServerResponse.badRequest()
                    .body("Invalid authorization code")

            // 验证重定向URI（如果提供）
            if (redirectUri.isNotEmpty() && redirectUri != authCodeData.redirectUri) {
                return@POST ServerResponse.badRequest()
                    .body("Invalid redirect_uri")
            }

            if (authCodeData.codeChallenge.isNotEmpty()) {
                val digest = MessageDigest.getInstance("SHA-256")
                val bytes = digest.digest(codeVerifier.toByteArray())
                val calcCodeChallenge = Encoders.BASE64URL.encode(bytes)
                if (calcCodeChallenge != authCodeData.codeChallenge) {
                    return@POST ServerResponse.badRequest()
                        .body("Invalid code verifier")
                }
            } else {
                if (clientId.isEmpty()) {
                    return@POST ServerResponse.badRequest()
                        .body("Missing required parameters: client_id")
                }
                val clientCf = getClientCf(clientId)
                    ?: return@POST handleAuthorizationError(
                        redirectUri,
                        "invalid_client",
                        "Invalid client_id: $clientId",
                        state
                    )
                val clientSecret = params.getFirst("client_secret") ?: ""
                if (clientSecret.isEmpty()) {
                    return@POST handleAuthorizationError(
                        redirectUri,
                        "invalid_client",
                        "Missing required parameters: client_secret",
                        state
                    )
                }
                if (clientSecret != clientCf.clientSecret) {
                    return@POST handleAuthorizationError(
                        redirectUri,
                        "invalid_client",
                        "Invalid client_secret: $clientSecret",
                        state
                    )
                }

            }

            // 生成ID令牌和访问令牌
            val idToken = generateIdToken(baseUri.toString(), authCodeData.user, authCodeData.nonce, clientId)

            val tokenResponse = mapOf(
                "access_token" to idToken,
                "token_type" to "Bearer",
                "expires_in" to 7200,
                "refresh_token" to "none",
                "id_token" to idToken,
                "scope" to authCodeData.scope
            )

            // 移除已使用的授权码
            authCodes.invalidate(code)

            ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(tokenResponse)
        }
    }

    private fun getClientCf(clientId: String): OidcServerClientProperties? {
        if (clientId.isEmpty()) {
            return oidcServerProperties.clients.firstOrNull()
        }
        return oidcServerProperties.clients.firstOrNull { it.clientId == clientId }
    }

    private fun getBaseUrl(request: ServerRequest): String {
        val uri = request.uri()
        return "${uri.scheme}://${uri.host}:${uri.port}"
    }

    private fun handleAuthorizationError(
        redirectUri: String,
        error: String,
        errorDescription: String,
        state: String?
    ): ServerResponse {
        return if (redirectUri.isNotEmpty()) {
            val errorParams = "error=$error&error_description=$errorDescription"
            ServerResponse.temporaryRedirect(
                URI.create("$redirectUri?$errorParams${state?.let { "&state=$it" } ?: ""}")
            ).build()
        } else {
            ServerResponse.badRequest()
                .body("$error: $errorDescription")
        }
    }

    private fun generateIdToken(issuer: String, user: Map<String, Any?>, nonce: String?, clientId: String?): String {
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
            .apply { clientId?.let { audience().add(it).and() } }
            .apply { nonce?.let { claim("nonce", it) } }
            .signWith(oidcKeyService.getSignKey())
            .compact()
    }

    // 授权码数据类
    data class AuthCodeData(
        val clientId: String? = null,
        val redirectUri: String,
        val scope: String,
        val codeChallenge: String,
        val codeChallengeMethod: String,
        val state: String? = null,
        val nonce: String? = null,
        val timestamp: Long = System.currentTimeMillis(),
        val user: Map<String, Any?>,
    )
}