package com.jiangtj.micro.web

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.expression.ParserContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.SimpleEvaluationContext
import kotlin.reflect.jvm.javaMethod

private val log = KotlinLogging.logger {}

class SpelUtilsTest {

    class TestClass {
        fun test(a: Int, b: Int) = a + b
    }

    @Test
    fun getMethodContext() {
        val method = TestClass::test.javaMethod!!
        val context = SpelUtils.getMethodContext(method, 2, 3)
        log.info { "ok" }
        val parser = SpelExpressionParser()
        val exp = parser.parseExpression("'ab:' + #a + #b")
        val value = exp.getValue(context)
        log.info { "$value" }
        val exp2 = parser.parseExpression("'args:' + #args[0] + #args[1]")
        val value2 = exp2.getValue(context)
        log.info { "$value2" }
        val exp3 = parser.parseExpression("args: #{#args[0]} #{#args[1]}", ParserContext.TEMPLATE_EXPRESSION)
        val value3 = exp3.getValue(context)
        log.info { "$value3" }
        val rootObject = TestDObj("test", 18)
        val rc = SimpleEvaluationContext.forReadOnlyDataBinding()
            .withRootObject(rootObject)
            .build()
        val exp4 = parser.parseExpression("#{name}: #{age}", ParserContext.TEMPLATE_EXPRESSION)
        val value4 = exp4.getValue(rc)
        log.info { "$value4" }
    }

    data class TestDObj(val name: String, val age: Int)

}