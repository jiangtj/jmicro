package com.jiangtj.micro.demobackend.common

import com.jiangtj.micro.web.toProblemDetail
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class CustomControllerAdvice : ResponseEntityExceptionHandler() {

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(e: RuntimeException): ProblemDetail {
        return e.toProblemDetail()
    }

}