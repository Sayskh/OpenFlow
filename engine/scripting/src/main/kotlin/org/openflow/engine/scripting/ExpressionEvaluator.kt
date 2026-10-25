package org.openflow.engine.scripting

import org.openflow.core.common.CubicBezier
import kotlin.math.*

/**
 * Interface for evaluating procedural expressions on animation parameters.
 */
interface ExpressionEvaluator {
    fun evaluate(
        expression: String,
        time: Double,
        value: Double,
        variables: Map<String, Double> = emptyMap()
    ): Double
}

/**
 * Pure Kotlin parser and evaluator supporting standard After Effects-style expressions.
 * Built-in variables: `time`, `value`.
 * Built-in functions: `wiggle(freq, amp, [time])`, `linear(t, tMin, tMax, val1, val2)`,
 * `ease(t, tMin, tMax, val1, val2)`, `clamp(val, min, max)`, `sin(x)`, `cos(x)`,
 * `tan(x)`, `abs(x)`, `min(a, b)`, `max(a, b)`, `pow(base, exp)`, `sqrt(x)`.
 */
class PureKotlinExpressionEvaluator : ExpressionEvaluator {

    override fun evaluate(
        expression: String,
        time: Double,
        value: Double,
        variables: Map<String, Double>
    ): Double {
        val sanitized = expression.trim()
        if (sanitized.isEmpty()) return value

        val context = HashMap<String, Double>(variables)
        context["time"] = time
        context["value"] = value

        val tokens = tokenize(sanitized)
        val parser = Parser(tokens, context, time)
        return parser.parseExpression()
    }

    private fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        while (i < input.length) {
            val c = input[i]
            when {
                c.isWhitespace() -> i++
                c.isDigit() || (c == '.' && i + 1 < input.length && input[i + 1].isDigit()) -> {
                    val start = i
                    var hasDot = false
                    while (i < input.length && (input[i].isDigit() || input[i] == '.')) {
                        if (input[i] == '.') {
                            if (hasDot) break
                            hasDot = true
                        }
                        i++
                    }
                    tokens.add(Token.Number(input.substring(start, i).toDouble()))
                }
                c.isLetter() || c == '_' || c == '$' -> {
                    val start = i
                    while (i < input.length && (input[i].isLetterOrDigit() || input[i] == '_' || input[i] == '$')) {
                        i++
                    }
                    tokens.add(Token.Identifier(input.substring(start, i)))
                }
                c == '+' -> { tokens.add(Token.Plus); i++ }
                c == '-' -> { tokens.add(Token.Minus); i++ }
                c == '*' -> { tokens.add(Token.Multiply); i++ }
                c == '/' -> { tokens.add(Token.Divide); i++ }
                c == '%' -> { tokens.add(Token.Modulo); i++ }
                c == '(' -> { tokens.add(Token.LParen); i++ }
                c == ')' -> { tokens.add(Token.RParen); i++ }
                c == ',' -> { tokens.add(Token.Comma); i++ }
                c == ';' -> { i++ } // Statement terminator ignored in expression
                else -> i++ // Skip unknown symbol
            }
        }
        return tokens
    }

    private sealed interface Token {
        data class Number(val value: Double) : Token
        data class Identifier(val name: String) : Token
        data object Plus : Token
        data object Minus : Token
        data object Multiply : Token
        data object Divide : Token
        data object Modulo : Token
        data object LParen : Token
        data object RParen : Token
        data object Comma : Token
    }

    private class Parser(
        private val tokens: List<Token>,
        private val context: Map<String, Double>,
        private val currentTime: Double
    ) {
        private var pos = 0

        fun parseExpression(): Double = parseAdditive()

        private fun peek(): Token? = if (pos < tokens.size) tokens[pos] else null
        private fun consume(): Token = tokens[pos++]

        private fun parseAdditive(): Double {
            var left = parseMultiplicative()
            while (true) {
                when (peek()) {
                    is Token.Plus -> {
                        consume()
                        left += parseMultiplicative()
                    }
                    is Token.Minus -> {
                        consume()
                        left -= parseMultiplicative()
                    }
                    else -> return left
                }
            }
        }

        private fun parseMultiplicative(): Double {
            var left = parseUnary()
            while (true) {
                when (peek()) {
                    is Token.Multiply -> {
                        consume()
                        left *= parseUnary()
                    }
                    is Token.Divide -> {
                        consume()
                        val right = parseUnary()
                        left = if (right != 0.0) left / right else 0.0
                    }
                    is Token.Modulo -> {
                        consume()
                        val right = parseUnary()
                        left = if (right != 0.0) left % right else 0.0
                    }
                    else -> return left
                }
            }
        }

        private fun parseUnary(): Double {
            return when (peek()) {
                is Token.Plus -> {
                    consume()
                    parseUnary()
                }
                is Token.Minus -> {
                    consume()
                    -parseUnary()
                }
                else -> parsePrimary()
            }
        }

        private fun parsePrimary(): Double {
            return when (val token = peek()) {
                is Token.Number -> {
                    consume()
                    token.value
                }
                is Token.Identifier -> {
                    consume()
                    if (peek() is Token.LParen) {
                        parseFunctionCall(token.name)
                    } else {
                        context[token.name] ?: 0.0
                    }
                }
                is Token.LParen -> {
                    consume()
                    val result = parseExpression()
                    if (peek() is Token.RParen) consume()
                    result
                }
                else -> 0.0
            }
        }

        private fun parseFunctionCall(name: String): Double {
            consume() // consume '('
            val args = mutableListOf<Double>()
            if (peek() !is Token.RParen) {
                args.add(parseExpression())
                while (peek() is Token.Comma) {
                    consume() // consume ','
                    args.add(parseExpression())
                }
            }
            if (peek() is Token.RParen) consume() // consume ')'

            return evaluateBuiltin(name.lowercase(), args)
        }

        private fun evaluateBuiltin(name: String, args: List<Double>): Double {
            return when (name) {
                "wiggle" -> {
                    // wiggle(freq, amp, [t])
                    val freq = args.getOrElse(0) { 0.0 }
                    val amp = args.getOrElse(1) { 0.0 }
                    val t = args.getOrElse(2) { currentTime }
                    sampleWiggle(freq, amp, t)
                }
                "linear" -> {
                    // linear(t, tMin, tMax, val1, val2) or linear(t, val1, val2)
                    when (args.size) {
                        3 -> {
                            val t = args[0].coerceIn(0.0, 1.0)
                            args[1] + (args[2] - args[1]) * t
                        }
                        5 -> {
                            val t = args[0]
                            val tMin = args[1]
                            val tMax = args[2]
                            val val1 = args[3]
                            val val2 = args[4]
                            if (tMin == tMax) val1
                            else {
                                val progress = ((t - tMin) / (tMax - tMin)).coerceIn(0.0, 1.0)
                                val1 + (val2 - val1) * progress
                            }
                        }
                        else -> 0.0
                    }
                }
                "ease" -> {
                    // ease(t, tMin, tMax, val1, val2) with standard cubic bezier
                    if (args.size >= 5) {
                        val t = args[0]
                        val tMin = args[1]
                        val tMax = args[2]
                        val val1 = args[3]
                        val val2 = args[4]
                        if (tMin == tMax) val1
                        else {
                            val progress = ((t - tMin) / (tMax - tMin)).coerceIn(0.0, 1.0)
                            val eased = CubicBezier.solve(progress, 0.42, 0.0, 0.58, 1.0)
                            val1 + (val2 - val1) * eased
                        }
                    } else 0.0
                }
                "clamp" -> {
                    // clamp(val, min, max)
                    val v = args.getOrElse(0) { 0.0 }
                    val min = args.getOrElse(1) { 0.0 }
                    val max = args.getOrElse(2) { 1.0 }
                    v.coerceIn(min, max)
                }
                "sin" -> sin(args.getOrElse(0) { 0.0 })
                "cos" -> cos(args.getOrElse(0) { 0.0 })
                "tan" -> tan(args.getOrElse(0) { 0.0 })
                "abs" -> abs(args.getOrElse(0) { 0.0 })
                "sqrt" -> sqrt(args.getOrElse(0) { 0.0 }.coerceAtLeast(0.0))
                "pow" -> args.getOrElse(0) { 0.0 }.pow(args.getOrElse(1) { 1.0 })
                "min" -> min(args.getOrElse(0) { 0.0 }, args.getOrElse(1) { 0.0 })
                "max" -> max(args.getOrElse(0) { 0.0 }, args.getOrElse(1) { 0.0 })
                "floor" -> floor(args.getOrElse(0) { 0.0 })
                "ceil" -> ceil(args.getOrElse(0) { 0.0 })
                "round" -> args.getOrElse(0) { 0.0 }.roundToLong().toDouble()
                else -> 0.0
            }
        }

        private fun sampleWiggle(freq: Double, amp: Double, t: Double): Double {
            // Pseudo-random continuous noise using sinusoidal harmonics
            val w1 = sin(t * freq * 2.0 * PI)
            val w2 = sin(t * freq * 1.414 * PI + 1.23) * 0.5
            val w3 = sin(t * freq * 2.718 * PI + 4.56) * 0.25
            val normalized = (w1 + w2 + w3) / 1.75
            return normalized * amp
        }
    }
}
