package com.sk.calculator_aisupported.util

import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object ExpressionEvaluator {
    private val decimalFormat = DecimalFormat("#.########", DecimalFormatSymbols(Locale.US))

    private fun cleanValue(value: Double): Double {
        if (value.isNaN()) return Double.NaN
        if (value.isInfinite()) return value
        // Snap near-zero values to 0.0 (e.g., 1.22e-16 -> 0.0)
        if (Math.abs(value) < 1e-12) return 0.0
        // Snap near-integers to exact integers (e.g., 0.9999999999999999 -> 1.0)
        val rounded = Math.round(value)
        if (Math.abs(value - rounded.toDouble()) < 1e-12) return rounded.toDouble()
        // Snap near-halves to exact half values (e.g., 0.49999999999 -> 0.5)
        val halfRounded = Math.round(value * 2.0) / 2.0
        if (Math.abs(value - halfRounded) < 1e-12) return halfRounded
        return value
    }

    private fun sinVal(x: Double, isRadians: Boolean): Double {
        if (!isRadians) {
            val norm = ((x % 360.0) + 360.0) % 360.0
            if (norm == 0.0 || norm == 180.0) return 0.0
            if (norm == 90.0) return 1.0
            if (norm == 270.0) return -1.0
            if (norm == 30.0 || norm == 150.0) return 0.5
            if (norm == 210.0 || norm == 330.0) return -0.5
            return cleanValue(Math.sin(Math.toRadians(x)))
        } else {
            val piRatio = x / Math.PI
            val roundedPi = Math.round(piRatio)
            if (Math.abs(piRatio - roundedPi) < 1e-12) {
                return 0.0
            }
            val halfPiRatio = x / (Math.PI / 2.0)
            val roundedHalfPi = Math.round(halfPiRatio)
            if (Math.abs(halfPiRatio - roundedHalfPi) < 1e-12) {
                val n = roundedHalfPi % 4
                if (n == 1L || n == -3L) return 1.0
                if (n == 3L || n == -1L) return -1.0
            }
            return cleanValue(Math.sin(x))
        }
    }

    private fun cosVal(x: Double, isRadians: Boolean): Double {
        if (!isRadians) {
            val norm = ((x % 360.0) + 360.0) % 360.0
            if (norm == 90.0 || norm == 270.0) return 0.0
            if (norm == 0.0) return 1.0
            if (norm == 180.0) return -1.0
            if (norm == 60.0 || norm == 300.0) return 0.5
            if (norm == 120.0 || norm == 240.0) return -0.5
            return cleanValue(Math.cos(Math.toRadians(x)))
        } else {
            val halfPiRatio = x / (Math.PI / 2.0)
            val roundedHalfPi = Math.round(halfPiRatio)
            if (Math.abs(halfPiRatio - roundedHalfPi) < 1e-12 && roundedHalfPi % 2 != 0L) {
                return 0.0
            }
            val piRatio = x / Math.PI
            val roundedPi = Math.round(piRatio)
            if (Math.abs(piRatio - roundedPi) < 1e-12) {
                val n = roundedPi % 2
                return if (n == 0L) 1.0 else -1.0
            }
            return cleanValue(Math.cos(x))
        }
    }

    private fun tanVal(x: Double, isRadians: Boolean): Double {
        if (!isRadians) {
            val norm = ((x % 360.0) + 360.0) % 360.0
            if (norm == 90.0 || norm == 270.0) throw IllegalArgumentException("Error")
            if (norm == 0.0 || norm == 180.0) return 0.0
            if (norm == 45.0 || norm == 225.0) return 1.0
            if (norm == 135.0 || norm == 315.0) return -1.0
            return cleanValue(Math.tan(Math.toRadians(x)))
        } else {
            val halfPiRatio = x / (Math.PI / 2.0)
            val roundedHalfPi = Math.round(halfPiRatio)
            if (Math.abs(halfPiRatio - roundedHalfPi) < 1e-12 && roundedHalfPi % 2 != 0L) {
                throw IllegalArgumentException("Error")
            }
            val piRatio = x / Math.PI
            val roundedPi = Math.round(piRatio)
            if (Math.abs(piRatio - roundedPi) < 1e-12) {
                return 0.0
            }
            return cleanValue(Math.tan(x))
        }
    }

    private fun factorial(x: Double): Double {
        if (x < 0.0 || x % 1.0 != 0.0) throw IllegalArgumentException("Error")
        if (x == 0.0 || x == 1.0) return 1.0
        val n = x.toInt()
        if (n > 170) return Double.POSITIVE_INFINITY
        var result = 1.0
        for (i in 2..n) {
            result *= i.toDouble()
        }
        return result
    }

    private fun nthRoot(x: Double, y: Double): Double {
        if (y == 0.0) throw IllegalArgumentException("Error")
        if (x < 0.0) {
            if (y % 2.0 != 0.0 && y % 1.0 == 0.0) {
                return -Math.pow(-x, 1.0 / y)
            }
            throw IllegalArgumentException("Error")
        }
        return Math.pow(x, 1.0 / y)
    }

    fun evaluate(formula: String, isRadians: Boolean = false): String {
        if (formula.isBlank()) return "0"

        var input = formula
            .replace("×", "*")
            .replace("÷", "/")
            .replace("EE", "*10^")
            .replace(" ", "")

        // Handle Percentage (%)
        val addPercentRegex = Regex("(\\d+(\\.\\d+)?)\\s*\\+\\s*(\\d+(\\.\\d+)?)\\%")
        input = addPercentRegex.replace(input) { match ->
            val a = match.groupValues[1]
            val b = match.groupValues[3]
            "$a + ($a * ($b * 0.01))"
        }

        val subPercentRegex = Regex("(\\d+(\\.\\d+)?)\\s*\\-\\s*(\\d+(\\.\\d+)?)\\%")
        input = subPercentRegex.replace(input) { match ->
            val a = match.groupValues[1]
            val b = match.groupValues[3]
            "$a - ($a * ($b * 0.01))"
        }
        input = input.replace("%", "*0.01")

        // Handle y-th root (x ʸ√ y -> nthRoot(x, y))
        val yRootRegex = Regex("(\\d+(\\.\\d+)?|\\([^)]+\\))\\s*ʸ√\\s*(\\d+(\\.\\d+)?|\\([^)]+\\))")
        while (yRootRegex.containsMatchIn(input)) {
            input = yRootRegex.replace(input) { match ->
                "nthRoot(${match.groupValues[1]},${match.groupValues[3]})"
            }
        }

        // Clean up standalone roots
        input = input.replace("³√", "cbrt")
        input = input.replace("²√", "sqrt")
        input = input.replace("√", "sqrt")

        // Token replacements to map cleanly to custom exp4j functions
        input = input
            .replace("asinh(", "asinh_func(")
            .replace("acosh(", "acosh_func(")
            .replace("atanh(", "atanh_func(")
            .replace("sinh(", "sinh_func(")
            .replace("cosh(", "cosh_func(")
            .replace("tanh(", "tanh_func(")
            .replace("asin(", "asin_func(")
            .replace("acos(", "acos_func(")
            .replace("atan(", "atan_func(")
            .replace("sin(", "sin_func(")
            .replace("cos(", "cos_func(")
            .replace("tan(", "tan_func(")
            .replace("ln(", "ln_func(")
            .replace("log10(", "log10_func(")
            .replace("log2(", "log2_func(")
            .replace("π", "pi")

        // Handle Factorial (!)
        val factRegex = Regex("(\\d+(\\.\\d+)?|\\([^)]+\\))\\!")
        while (factRegex.containsMatchIn(input)) {
            input = factRegex.replace(input) { match ->
                "fact_func(${match.groupValues[1]})"
            }
        }

        // Implicit multiplication insertion
        input = input.replace(Regex("(?<=\\d|\\)|pi|e)(?=[(a-zA-Z]|pi|e)"), "*")

        // Automatically close brackets safely
        val openBrackets = input.count { it == '(' }
        val closeBrackets = input.count { it == ')' }
        if (openBrackets > closeBrackets) {
            input += ")".repeat(openBrackets - closeBrackets)
        }

        return try {
            // Core Trig Functions with exact angle snapping & floating-point cleanup
            val sinFunc = object : Function("sin_func", 1) {
                override fun apply(args: DoubleArray) = sinVal(args[0], isRadians)
            }
            val cosFunc = object : Function("cos_func", 1) {
                override fun apply(args: DoubleArray) = cosVal(args[0], isRadians)
            }
            val tanFunc = object : Function("tan_func", 1) {
                override fun apply(args: DoubleArray) = tanVal(args[0], isRadians)
            }

            // Inverse Trig
            val asinFunc = object : Function("asin_func", 1) {
                override fun apply(args: DoubleArray): Double {
                    if (args[0] < -1.0 || args[0] > 1.0) throw IllegalArgumentException("Error")
                    val rad = Math.asin(args[0])
                    return if (!isRadians) cleanValue(rad * (180.0 / Math.PI)) else cleanValue(rad)
                }
            }
            val acosFunc = object : Function("acos_func", 1) {
                override fun apply(args: DoubleArray): Double {
                    if (args[0] < -1.0 || args[0] > 1.0) throw IllegalArgumentException("Error")
                    val rad = Math.acos(args[0])
                    return if (!isRadians) rad * (180.0 / Math.PI) else rad
                }
            }
            val atanFunc = object : Function("atan_func", 1) {
                override fun apply(args: DoubleArray): Double {
                    val rad = Math.atan(args[0])
                    return if (!isRadians) rad * (180.0 / Math.PI) else rad
                }
            }

            // Hyperbolics
            val sinhFunc = object : Function("sinh_func", 1) { override fun apply(args: DoubleArray) = cleanValue(Math.sinh(args[0])) }
            val coshFunc = object : Function("cosh_func", 1) { override fun apply(args: DoubleArray) = cleanValue(Math.cosh(args[0])) }
            val tanhFunc = object : Function("tanh_func", 1) { override fun apply(args: DoubleArray) = cleanValue(Math.tanh(args[0])) }
            val asinhFunc = object : Function("asinh_func", 1) { override fun apply(args: DoubleArray) = cleanValue(Math.log(args[0] + Math.sqrt(args[0] * args[0] + 1.0))) }
            val acoshFunc = object : Function("acosh_func", 1) { 
                override fun apply(args: DoubleArray): Double {
                    if (args[0] < 1.0) throw IllegalArgumentException("Error")
                    return cleanValue(Math.log(args[0] + Math.sqrt(args[0] * args[0] - 1.0)))
                }
            }
            val atanhFunc = object : Function("atanh_func", 1) {
                override fun apply(args: DoubleArray): Double {
                    if (args[0] <= -1.0 || args[0] >= 1.0) throw IllegalArgumentException("Error")
                    return cleanValue(0.5 * Math.log((1.0 + args[0]) / (1.0 - args[0])))
                }
            }

            // Logs & Custom Functions
            val lnFunc = object : Function("ln_func", 1) {
                override fun apply(args: DoubleArray) = if (args[0] <= 0) throw IllegalArgumentException("Error") else cleanValue(Math.log(args[0]))
            }
            val log10Func = object : Function("log10_func", 1) {
                override fun apply(args: DoubleArray) = if (args[0] <= 0) throw IllegalArgumentException("Error") else cleanValue(Math.log10(args[0]))
            }
            val log2Func = object : Function("log2_func", 1) {
                override fun apply(args: DoubleArray) = if (args[0] <= 0) throw IllegalArgumentException("Error") else cleanValue(Math.log(args[0]) / Math.log(2.0))
            }
            val factFunc = object : Function("fact_func", 1) { override fun apply(args: DoubleArray) = factorial(args[0]) }
            val nthRootFunc = object : Function("nthRoot", 2) { override fun apply(args: DoubleArray) = nthRoot(args[0], args[1]) }

            // Build exp4j Engine execution context
            val expression = ExpressionBuilder(input)
                .functions(
                    sinFunc, cosFunc, tanFunc, 
                    asinFunc, acosFunc, atanFunc,
                    sinhFunc, coshFunc, tanhFunc,
                    asinhFunc, acoshFunc, atanhFunc,
                    lnFunc, log10Func, log2Func, 
                    factFunc, nthRootFunc
                )
                .build()

            val rawResult = expression.evaluate()
            
            if (rawResult.isNaN() || rawResult.isInfinite()) "Error" else decimalFormat.format(rawResult)
        } catch (_: ArithmeticException) {
            "Error"
        } catch (e: IllegalArgumentException) {
            if (e.message == "Error") "Error" else ""
        } catch (_: Exception) {
            ""
        }
    }
}