package __PKG__.common

import com.jiangtj.micro.web.toProblemDetail
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * 把 `BaseException`（鉴权失败、业务异常）转换为 RFC 9457 ProblemDetail。
 *
 * 缺少这个 Advice 时，`@HasLogin` 等注解拒绝请求只会得到空 body 的 401/403。
 */
@ControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(e: RuntimeException): ProblemDetail = e.toProblemDetail()
}
