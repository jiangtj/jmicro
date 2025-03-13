package com.jiangtj.platform.baseservlet;

import com.jiangtj.micro.auth.annotations.HasRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    @HasRole("test")
    @GetMapping("books")
    public String getBookPage() {
        return "success";
    }
}
