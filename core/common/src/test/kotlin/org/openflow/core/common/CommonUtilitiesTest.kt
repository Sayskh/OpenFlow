package org.openflow.core.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommonUtilitiesTest {

    @Test
    fun testTimecodeCalculations() {
        val tc = Timecode(frame = 90, fps = 30.0)
        assertEquals(3.0, tc.totalSeconds)
        assertEquals(3000L, tc.totalMilliseconds)
        assertEquals("00:00:03:00", tc.toString())

        val parsed = Timecode.parse("00:01:30:15", 30.0)
        assertEquals(90.5, parsed.totalSeconds)
        assertEquals(2715L, parsed.frame)
        assertEquals("00:01:30:15", parsed.toString())
    }

    @Test
    fun testCubicBezierEvaluation() {
        assertEquals(0.0, CubicBezier.solve(0.0, 0.25, 0.1, 0.25, 1.0), 0.001)
        assertEquals(1.0, CubicBezier.solve(1.0, 0.25, 0.1, 0.25, 1.0), 0.001)

        val mid = CubicBezier.solve(0.5, 0.0, 0.0, 1.0, 1.0)
        assertEquals(0.5, mid, 0.001)
    }
}
