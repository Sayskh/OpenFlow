package org.openflow.core.common

import kotlin.math.abs

/**
 * Cubic Bezier easing solver using Newton-Raphson method with binary bisection fallback.
 * Mimics CSS cubic-bezier / After Effects easing curves.
 */
object CubicBezier {

    private const val NEWTON_ITERATIONS = 8
    private const val NEWTON_MIN_SLOPE = 0.001
    private const val SUBDIVISION_MAX_ITERATIONS = 10
    private const val SUBDIVISION_PRECISION = 0.0000001

    /**
     * Solves for y given x (progress t in [0.0, 1.0]) along the cubic Bezier curve
     * defined by start (0, 0), control point 1 (p1x, p1y), control point 2 (p2x, p2y), and end (1, 1).
     */
    fun solve(t: Double, p1x: Double, p1y: Double, p2x: Double, p2y: Double): Double {
        if (t <= 0.0) return 0.0
        if (t >= 1.0) return 1.0
        if (p1x == p1y && p2x == p2y) return t // Linear shortcut

        val clampedP1x = p1x.coerceIn(0.0, 1.0)
        val clampedP2x = p2x.coerceIn(0.0, 1.0)

        val parametricT = solveCurveX(t, clampedP1x, clampedP2x)
        return sampleCurveY(parametricT, p1y, p2y)
    }

    private fun sampleCurveX(t: Double, p1x: Double, p2x: Double): Double {
        // (1-t)^3*0 + 3*(1-t)^2*t*p1x + 3*(1-t)*t^2*p2x + t^3*1
        val cx = 3.0 * p1x
        val bx = 3.0 * (p2x - p1x) - cx
        val ax = 1.0 - cx - bx
        return ((ax * t + bx) * t + cx) * t
    }

    private fun sampleCurveY(t: Double, p1y: Double, p2y: Double): Double {
        val cy = 3.0 * p1y
        val by = 3.0 * (p2y - p1y) - cy
        val ay = 1.0 - cy - by
        return ((ay * t + by) * t + cy) * t
    }

    private fun sampleCurveDerivativeX(t: Double, p1x: Double, p2x: Double): Double {
        val cx = 3.0 * p1x
        val bx = 3.0 * (p2x - p1x) - cx
        val ax = 1.0 - cx - bx
        return (3.0 * ax * t + 2.0 * bx) * t + cx
    }

    private fun solveCurveX(x: Double, p1x: Double, p2x: Double): Double {
        var t = x
        // Newton-Raphson iteration
        for (i in 0 until NEWTON_ITERATIONS) {
            val currentX = sampleCurveX(t, p1x, p2x) - x
            if (abs(currentX) < SUBDIVISION_PRECISION) return t
            val dX = sampleCurveDerivativeX(t, p1x, p2x)
            if (abs(dX) < NEWTON_MIN_SLOPE) break
            t -= currentX / dX
        }

        // Binary bisection fallback if Newton diverges
        var t0 = 0.0
        var t1 = 1.0
        t = x

        if (t < t0) return t0
        if (t > t1) return t1

        for (i in 0 until SUBDIVISION_MAX_ITERATIONS) {
            val currentX = sampleCurveX(t, p1x, p2x)
            if (abs(currentX - x) < SUBDIVISION_PRECISION) return t
            if (x > currentX) {
                t0 = t
            } else {
                t1 = t
            }
            t = (t1 + t0) * 0.5
        }

        return t
    }
}
