package com.jiangtj.micro.demoreactive;

import com.jiangtj.micro.auth.casdoor.CasdoorAuthService;
import com.jiangtj.micro.auth.casdoor.CasdoorUserContextImpl;
import com.jiangtj.micro.auth.reactive.AuthReactiveHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.casbin.casdoor.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class CasdoorController {

    @Resource
    private CasdoorAuthService casdoorAuthService;

    @GetMapping("toLogin")
    public Mono<String> toLogin(String redirectUrl) {
        return Mono.just(casdoorAuthService.getSigninUrl(redirectUrl));
    }

    @GetMapping("user")
    public Mono<User> user() {
        return AuthReactiveHolder.deferAuthContext()
            .cast(CasdoorUserContextImpl.class)
            .map(CasdoorUserContextImpl::getCasdoorUser);
    }

    @PostMapping("login")
    public Mono<String> login(@RequestParam String code,@RequestParam String state) {
        return Mono.just(casdoorAuthService.getOAuthToken(code, state));
    }
}
