package org.openflow.core.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModelSerializationTest {

    private val json = Json { prettyPrint = true }

    @Test
    fun testProjectSerializationRoundtrip() {
        val clip = Clip(
            id = "clip_1",
            trackId = "track_1",
            startFrame = 0,
            durationFrames = 120,
            sourceUri = "content://media/external/video/media/1",
            transform = Transform(positionX = 100f, positionY = 200f, scaleX = 1.2f, scaleY = 1.2f),
            keyframes = listOf(
                Keyframe(frame = 0, parameterName = "opacity", value = 0f, interpolationType = InterpolationType.Linear),
                Keyframe(frame = 30, parameterName = "opacity", value = 1f, interpolationType = InterpolationType.Bezier)
            ),
            effects = listOf(
                Effect(id = "eff_1", name = "Color Adjust", type = EffectType.Shader, parameters = mapOf("brightness" to 0.1f))
            ),
            expressions = listOf(
                Expression(parameterTarget = "rotation", expression = "time * 30")
            )
        )

        val track = Track(
            id = "track_1",
            name = "Main Video",
            type = TrackType.Video,
            clips = listOf(clip)
        )

        val project = Project(
            id = "proj_1",
            name = "Demo Edit",
            width = 1920,
            height = 1080,
            fps = 30.0,
            durationFrames = 300,
            tracks = listOf(track)
        )

        val serialized = json.encodeToString(project)
        val deserialized = json.decodeFromString<Project>(serialized)

        assertEquals(project.id, deserialized.id)
        assertEquals(project.tracks.size, deserialized.tracks.size)
        assertEquals(project.tracks.first().clips.first().transform.scaleX, 1.2f)
        assertEquals(1, project.getClipsAtFrame(10).size)
        assertEquals(0, project.getClipsAtFrame(200).size)
    }
}
