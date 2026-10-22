package com.jiangtj.micro.demobackend

import com.jiangtj.micro.common.exceptions.MicroException
import com.jiangtj.micro.common.exceptions.MicroHttpException
import com.jiangtj.micro.common.exceptions.MicroProblemDetailException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

fun ProblemDetail.toResponseEntity(): ResponseEntity<ProblemDetail> {
    return ResponseEntity.status(this.status).body(this)
}

@ControllerAdvice
class ExControllerAdvice : ResponseEntityExceptionHandler() {

    @ExceptionHandler(MicroException::class)
    fun handleMicroException(e: MicroException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        problemDetail.detail = e.message
        return problemDetail.toResponseEntity()
    }

    @ExceptionHandler(MicroHttpException::class)
    fun handleMicroHttpException(e: MicroHttpException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatus(e.status)
        problemDetail.detail = e.message
        return problemDetail.toResponseEntity()
    }

    @ExceptionHandler(MicroProblemDetailException::class)
    fun handleMicroProblemDetailException(e: MicroProblemDetailException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatus(e.status)
        problemDetail.title = e.title
        problemDetail.detail = e.detail
        return problemDetail.toResponseEntity()
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(e: RuntimeException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        problemDetail.detail = e.message
        return problemDetail.toResponseEntity()
    }
}