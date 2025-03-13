package com.jiangtj.platform.basereactive;

import com.jiangtj.micro.auth.annotations.HasRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class BookController {

    @HasRole("test")
    @GetMapping("books")
    public Mono<String> getBookPage() {
        return Mono.just("success");
    }
}
