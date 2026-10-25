package org.openflow.engine.scripting

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExpressionEvaluatorTest {

    private val evaluator = PureKotlinExpressionEvaluator()

    @Test
    fun testBasicArithmeticAndVariables() {
        val result = evaluator.evaluate("value + time * 10", time = 2.5, value = 50.0)
        assertEquals(75.0, result, 0.001)

        val complex = evaluator.evaluate("(10 + 20) * 2 - 5 / 2.5", time = 0.0, value = 0.0)
        assertEquals(58.0, complex, 0.001)
    }

    @Test
    fun testLinearAndClampFunctions() {
        val clamped = evaluator.evaluate("clamp(150, 0, 100)", time = 0.0, value = 0.0)
        assertEquals(100.0, clamped, 0.001)

        val lin = evaluator.evaluate("linear(time, 0, 10, 0, 100)", time = 5.0, value = 0.0)
        assertEquals(50.0, lin, 0.001)
    }

    @Test
    fun testWiggleFunction() {
        val w1 = evaluator.evaluate("value + wiggle(5, 20)", time = 1.0, value = 100.0)
        assertTrue(w1 in 70.0..130.0)
    }
}
