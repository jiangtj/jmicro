package com.jiangtj.micro.demobackend

import com.jiangtj.micro.web.BaseExceptionUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BaseController {

    @GetMapping("/")
    fun index(): String = "Base Servlet Client Started !!"

    @GetMapping("/insecure/err")
    fun err(): String = throw BaseExceptionUtils.badRequest("insecure")

    @GetMapping("/insecure/err2")
    fun err2(): String = throw RuntimeException("系统错误")

    @GetMapping("/needtoken")
    fun needToken(): String = "这个请求需要token"

    @GetMapping("/call-with-server")
    fun callWithServer(): String = "这个请求需要 server token"
}
