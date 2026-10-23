package com.jiangtj.micro.demobackend

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class DemoBackendApplication

fun main(args: Array<String>) {
    SpringApplication.run(DemoBackendApplication::class.java, *args)
}
