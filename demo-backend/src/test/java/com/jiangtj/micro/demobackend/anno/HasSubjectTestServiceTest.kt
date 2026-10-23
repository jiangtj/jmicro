package com.jiangtj.micro.demobackend.anno

import com.jiangtj.micro.auth.exceptions.UnAuthorizationException
import com.jiangtj.micro.test.JMicroTest
import com.jiangtj.micro.test.WithMockSubject
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

@JMicroTest
class HasSubjectTestServiceTest {

    @Autowired
    lateinit var hasSubjectTestService: HasSubjectTestService

    @Test
    @WithMockSubject(id = "1")
    fun testId1() {
        assertDoesNotThrow { hasSubjectTestService.hasId1() }
        assertThrows<UnAuthorizationException> { hasSubjectTestService.hasId2() }
        assertThrows<UnAuthorizationException> { hasSubjectTestService.hasName() }
        assertThrows<UnAuthorizationException> { hasSubjectTestService.hasDisplayName() }
        assertThrows<UnAuthorizationException> { hasSubjectTestService.hasType() }
        assertThrows<UnAuthorizationException> { hasSubjectTestService.hasIss() }
    }

    @Test
    @WithMockSubject(
        id = "2",
        name = "name",
        displayName = "displayName",
        type = "type",
        issuer = "iss",
    )
    fun testId2() {
        assertThrows<UnAuthorizationException> { hasSubjectTestService.hasId1() }
        assertDoesNotThrow { hasSubjectTestService.hasId2() }
        assertDoesNotThrow { hasSubjectTestService.hasName() }
        assertDoesNotThrow { hasSubjectTestService.hasDisplayName() }
        assertDoesNotThrow { hasSubjectTestService.hasType() }
        assertDoesNotThrow { hasSubjectTestService.hasIss() }
    }
}
