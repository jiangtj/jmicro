package com.jiangtj.platform.baseservlet;

import com.jiangtj.micro.auth.annotations.HasSubject;
import org.springframework.stereotype.Service;

@Service
public class HasSubjectTestService {

    @HasSubject(id = "1")
    public void hasId1() {
    }

    @HasSubject("2")
    public void hasId2() {
    }

    @HasSubject(name = "name")
    public void hasName() {
    }

    @HasSubject(displayName = "displayName")
    public void hasDisplayName() {
    }

    @HasSubject(type = "type")
    public void hasType() {
    }

    @HasSubject(issuer = "iss")
    public void hasIss() {
    }
}
