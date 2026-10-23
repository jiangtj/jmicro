package com.jiangtj.micro.demobackend

import com.jiangtj.micro.web.BaseExceptionUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.RouterFunctions
import org.springframework.web.servlet.function.ServerResponse

@Configuration
class BaseRouter {

    @Bean
    fun baseRoutes(): RouterFunction<ServerResponse> = RouterFunctions.route()
        .GET("/insecure/fn/err") { throw BaseExceptionUtils.badRequest("fn") }
        .GET("/insecure/fn/err2") { throw RuntimeException("系统错误") }
        .GET("/fn/needtoken") { ServerResponse.ok().body("ok") }
        .build()
}
