package com.jiangtj.micro.auth.oidc

import io.jsonwebtoken.Header
import io.jsonwebtoken.Locator
import java.security.Key

interface KidLocator: Locator<Key> {
    fun support(kid: String): Boolean
    fun handle(header: Header): Key

    override fun locate(header: Header): Key? {
        val kid = header.getKid() ?: return null
        if (support(kid)) {
            return handle(header)
        }
        return null
    }
}