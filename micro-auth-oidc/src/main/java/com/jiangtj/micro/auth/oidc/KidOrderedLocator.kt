package com.jiangtj.micro.auth.oidc

import org.springframework.core.Ordered

interface KidOrderedLocator : KidLocator, Ordered {
    override fun getOrder() = 0
}