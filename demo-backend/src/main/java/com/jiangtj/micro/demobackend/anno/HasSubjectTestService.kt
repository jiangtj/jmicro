package com.jiangtj.micro.demobackend.anno

import com.jiangtj.micro.auth.annotations.HasSubject
import org.springframework.stereotype.Service

@Service
class HasSubjectTestService {

    @HasSubject(id = "1")
    fun hasId1() {
    }

    @HasSubject("2")
    fun hasId2() {
    }

    @HasSubject(name = "name")
    fun hasName() {
    }

    @HasSubject(displayName = "displayName")
    fun hasDisplayName() {
    }

    @HasSubject(type = "type")
    fun hasType() {
    }

    @HasSubject(issuer = "iss")
    fun hasIss() {
    }
}
