package com.jiangtj.micro.test

import com.jiangtj.micro.auth.context.AuthRequest
import org.springframework.http.HttpMethod
import org.springframework.http.server.RequestPath
import java.net.URI
import java.net.URISyntaxException

class MockAuthRequest private constructor(
    private val uri: URI,
    private val httpMethod: HttpMethod,
    private val headers: MutableMap<String, MutableList<String>>,
    private val queryParams: MutableMap<String, MutableList<String>>,
    private val attributes: MutableMap<String, Any>,
) : AuthRequest {

    private val requestPath: RequestPath = RequestPath.parse(uri, null)

    init {
        val query = uri.query
        if (!query.isNullOrEmpty()) {
            for (pair in query.split("&")) {
                val idx = pair.indexOf('=')
                val key = if (idx > 0) pair.substring(0, idx) else pair
                val value = if (idx > 0 && idx < pair.length - 1) pair.substring(idx + 1) else ""
                addQueryParam(key, value)
            }
        }
    }

    fun contextPath(path: String) {
        requestPath.modifyContextPath(path)
    }

    fun addHeader(name: String, value: String) {
        headers.computeIfAbsent(name) { ArrayList() }.add(value)
    }

    fun addQueryParam(name: String, value: String) {
        queryParams.computeIfAbsent(name) { ArrayList() }.add(value)
    }

    override fun getPath(): String = requestPath.pathWithinApplication().value()

    override fun getURI(): URI = uri

    override fun getMethod(): HttpMethod = httpMethod

    override fun getQueryParams(name: String): List<String> = queryParams[name] ?: emptyList()

    override fun getHeaders(name: String): List<String> = headers[name] ?: emptyList()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getSessionAttribute(name: String): T? = attributes[name] as T?

    companion object {

        @JvmStatic
        fun get(urlString: String): Builder = Builder(HttpMethod.GET, urlString)

        @JvmStatic
        fun post(urlString: String): Builder = Builder(HttpMethod.POST, urlString)

        @JvmStatic
        fun put(urlString: String): Builder = Builder(HttpMethod.PUT, urlString)

        @JvmStatic
        fun delete(urlString: String): Builder = Builder(HttpMethod.DELETE, urlString)
    }

    class Builder internal constructor(private val method: HttpMethod, urlString: String) {

        private val uri: URI = try {
            URI(urlString)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
        private val headers: MutableMap<String, MutableList<String>> = HashMap()
        private val queryParams: MutableMap<String, MutableList<String>> = HashMap()
        private val attributes: MutableMap<String, Any> = HashMap()

        fun header(name: String, value: String): Builder {
            headers.computeIfAbsent(name) { ArrayList() }.add(value)
            return this
        }

        fun queryParam(name: String, value: String): Builder {
            queryParams.computeIfAbsent(name) { ArrayList() }.add(value)
            return this
        }

        fun attribute(name: String, value: Any): Builder {
            attributes[name] = value
            return this
        }

        fun build(): MockAuthRequest = MockAuthRequest(uri, method, headers, queryParams, attributes)
    }
}
