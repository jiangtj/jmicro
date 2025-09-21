package com.jiangtj.micro.demobackend;

import com.jiangtj.micro.auth.annotations.HasLogin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    @HasLogin
    @GetMapping("books")
    public String getBookPage() {
        return "success";
    }
}
