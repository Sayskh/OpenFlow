package org.openflow.core.common

import kotlinx.serialization.Serializable
import kotlin.math.roundToLong

/**
 * Timecode utility for frame and millisecond conversion to/from HH:MM:SS:FF notation.
 * Supports standard and arbitrary frame rates (e.g., 24, 25, 30, 60 fps).
 */
@Serializable
data class Timecode(
    val frame: Long,
    val fps: Double
) : Comparable<Timecode> {

    init {
        require(fps > 0.0) { "FPS must be greater than zero, got $fps" }
        require(frame >= 0) { "Frame number must be non-negative, got $frame" }
    }

    val totalSeconds: Double
        get() = frame / fps

    val totalMilliseconds: Long
        get() = (totalSeconds * 1000.0).roundToLong()

    val hours: Long
        get() = (totalSeconds / 3600.0).toLong()

    val minutes: Long
        get() = ((totalSeconds % 3600.0) / 60.0).toLong()

    val seconds: Long
        get() = (totalSeconds % 60.0).toLong()

    val framesRemainder: Long
        get() = (frame % fps.toLong().coerceAtLeast(1L))

    override fun toString(): String {
        return "%02d:%02d:%02d:%02d".format(hours, minutes, seconds, framesRemainder)
    }

    override fun compareTo(other: Timecode): Int {
        val thisTime = this.totalSeconds
        val otherTime = other.totalSeconds
        return thisTime.compareTo(otherTime)
    }

    companion object {
        private val TIMECODE_REGEX = Regex("""^(\d{2}):(\d{2}):(\d{2}):(\d{2})$""")

        fun fromMilliseconds(ms: Long, fps: Double): Timecode {
            require(fps > 0.0) { "FPS must be greater than zero" }
            require(ms >= 0) { "Milliseconds must be non-negative" }
            val frame = ((ms / 1000.0) * fps).roundToLong()
            return Timecode(frame = frame, fps = fps)
        }

        fun fromSeconds(seconds: Double, fps: Double): Timecode {
            require(fps > 0.0) { "FPS must be greater than zero" }
            require(seconds >= 0.0) { "Seconds must be non-negative" }
            val frame = (seconds * fps).roundToLong()
            return Timecode(frame = frame, fps = fps)
        }

        fun parse(formatted: String, fps: Double): Timecode {
            val match = TIMECODE_REGEX.matchEntire(formatted.trim())
                ?: throw IllegalArgumentException("Invalid timecode format: '$formatted'. Expected HH:MM:SS:FF")

            val (hStr, mStr, sStr, fStr) = match.destructured
            val h = hStr.toLong()
            val m = mStr.toLong()
            val s = sStr.toLong()
            val f = fStr.toLong()

            val baseSec = (h * 3600L) + (m * 60L) + s
            val totalFrames = (baseSec * fps).roundToLong() + f
            return Timecode(frame = totalFrames, fps = fps)
        }
    }
}
