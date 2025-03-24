package com.jiangtj.micro.demoreactive.anno;

import com.jiangtj.micro.auth.exceptions.UnAuthorizationException;
import com.jiangtj.micro.test.AuthStepVerifier;
import com.jiangtj.micro.test.JMicroTest;
import com.jiangtj.micro.test.WithMockSubject;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

@JMicroTest
class HasSubjectTestServiceTest {

    @Resource
    HasSubjectTestService hasSubjectTestService;

    @Test
    @WithMockSubject(id = "1")
    void testId1() {
        AuthStepVerifier.create(hasSubjectTestService.hasId1())
            .expectComplete()
            .verify();
        AuthStepVerifier.create(hasSubjectTestService.hasId2())
            .expectError(UnAuthorizationException.class)
            .verify();
        AuthStepVerifier.create(hasSubjectTestService.hasName())
            .expectError(UnAuthorizationException.class)
            .verify();
        AuthStepVerifier.create(hasSubjectTestService.hasDisplayName())
            .expectError(UnAuthorizationException.class)
            .verify();
        AuthStepVerifier.create(hasSubjectTestService.hasType())
            .expectError(UnAuthorizationException.class)
            .verify();
        AuthStepVerifier.create(hasSubjectTestService.hasIss())
            .expectError(UnAuthorizationException.class)
            .verify();
    }

    @Test
    @WithMockSubject(
        id = "2",
        name = "name",
        displayName = "displayName",
        type = "type",
        issuer = "iss"
    )
    void testId2() {
        AuthStepVerifier.create(hasSubjectTestService.hasId1())
            .expectError(UnAuthorizationException.class)
            .verify();
        AuthStepVerifier.create(hasSubjectTestService.hasId2())
            .expectComplete()
            .verify();
        AuthStepVerifier.create(hasSubjectTestService.hasName())
            .expectComplete()
            .verify();
        AuthStepVerifier.create(hasSubjectTestService.hasDisplayName())
            .expectComplete()
            .verify();
        AuthStepVerifier.create(hasSubjectTestService.hasType())
            .expectComplete()
            .verify();
        AuthStepVerifier.create(hasSubjectTestService.hasIss())
            .expectComplete()
            .verify();
    }

}