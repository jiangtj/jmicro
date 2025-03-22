package com.jiangtj.micro.demobackend;

import com.jiangtj.micro.web.BaseExceptionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    @GetMapping("/")
    public String index(){
        return "Base Servlet Client Started !!";
    }

    @GetMapping("/insecure/err")
    public String err(){
        throw BaseExceptionUtils.badRequest("insecure");
    }

    @GetMapping("/insecure/err2")
    public String err2() {
        throw new RuntimeException("系统错误");
    }

    @GetMapping("/needtoken")
    public String needToken(){
        return "这个请求需要token";
    }

    @GetMapping("/call-with-server")
    public String callWithServer(){
        return "这个请求需要 server token";
    }
}
