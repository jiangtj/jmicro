package com.jiangtj.platform.basereactive;

import com.jiangtj.micro.auth.annotations.HasSubject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class HasSubjectTestService {

    @HasSubject(id = "1")
    public Mono<Void> hasId1() {
        return Mono.empty();
    }

    @HasSubject("2")
    public Mono<Void> hasId2() {
        return Mono.empty();
    }

    @HasSubject(name = "name")
    public Mono<Void> hasName() {
        return Mono.empty();
    }

    @HasSubject(displayName = "displayName")
    public Mono<Void> hasDisplayName() {
        return Mono.empty();
    }

    @HasSubject(type = "type")
    public Mono<Void> hasType() {
        return Mono.empty();
    }

    @HasSubject(issuer = "iss")
    public Mono<Void> hasIss() {
        return Mono.empty();
    }
}
