package com.jiangtj.platform.baseservlet;

import com.jiangtj.micro.test.JMicroTest;
import com.jiangtj.micro.test.WithMockSubject;
import com.jiangtj.micro.web.BaseException;
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
        assertThrows(BaseException.class, () -> hasSubjectTestService.hasId2());
        assertThrows(BaseException.class, () -> hasSubjectTestService.hasName());
        assertThrows(BaseException.class, () -> hasSubjectTestService.hasDisplayName());
        assertThrows(BaseException.class, () -> hasSubjectTestService.hasType());
        assertThrows(BaseException.class, () -> hasSubjectTestService.hasIss());
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
        assertThrows(BaseException.class, () -> hasSubjectTestService.hasId1());
        assertDoesNotThrow(() -> hasSubjectTestService.hasId2());
        assertDoesNotThrow(() -> hasSubjectTestService.hasName());
        assertDoesNotThrow(() -> hasSubjectTestService.hasDisplayName());
        assertDoesNotThrow(() -> hasSubjectTestService.hasType());
        assertDoesNotThrow(() -> hasSubjectTestService.hasIss());
    }

}