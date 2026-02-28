package com.jiangtj.micro.web

import org.springframework.expression.EvaluationContext
import org.springframework.expression.spel.support.SimpleEvaluationContext
import java.lang.reflect.Method

object SpelUtils {
    @JvmStatic
    fun getMethodContext(method: Method, vararg args: Any?): EvaluationContext {
        val context = SimpleEvaluationContext.forReadOnlyDataBinding().build()
        context.setVariable("args", args)
        val parameters = method.parameters
        for (i in parameters.indices) {
            val parameter = parameters[i]
            val arg = args.getOrNull(i)
            context.setVariable(parameter.name, arg)
            context.setVariable("p$i", arg)
            context.setVariable("a$i", arg)
        }
        return context
    }
}
