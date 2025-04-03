package com.jiangtj.micro.demobackend.anno;

import com.jiangtj.micro.auth.exceptions.UnAuthorizationException;
import com.jiangtj.micro.test.JMicroTest;
import com.jiangtj.micro.test.WithMockSubject;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JMicroTest
class HasSubjectTestServiceTest {

    @Resource
    HasSubjectTestService hasSubjectTestService;

    @Test
    @WithMockSubject(id = "1")
    void testId1() {
        assertDoesNotThrow(() -> hasSubjectTestService.hasId1());
        assertThrows(UnAuthorizationException.class, () -> hasSubjectTestService.hasId2());
        assertThrows(UnAuthorizationException.class, () -> hasSubjectTestService.hasName());
        assertThrows(UnAuthorizationException.class, () -> hasSubjectTestService.hasDisplayName());
        assertThrows(UnAuthorizationException.class, () -> hasSubjectTestService.hasType());
        assertThrows(UnAuthorizationException.class, () -> hasSubjectTestService.hasIss());
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
        assertThrows(UnAuthorizationException.class, () -> hasSubjectTestService.hasId1());
        assertDoesNotThrow(() -> hasSubjectTestService.hasId2());
        assertDoesNotThrow(() -> hasSubjectTestService.hasName());
        assertDoesNotThrow(() -> hasSubjectTestService.hasDisplayName());
        assertDoesNotThrow(() -> hasSubjectTestService.hasType());
        assertDoesNotThrow(() -> hasSubjectTestService.hasIss());
    }

}