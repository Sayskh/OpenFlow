package org.openflow.engine.render

import kotlin.test.Test
import kotlin.test.assertNotNull

class ShaderResourcesTest {

    @Test
    fun testShaderResourcesPresence() {
        val shaders = listOf(
            "/shaders/passthrough.vert",
            "/shaders/composite_blend.frag",
            "/shaders/color_adjust.frag",
            "/shaders/radial_blur.frag",
            "/shaders/shake_warp.frag"
        )

        for (shaderPath in shaders) {
            val stream = javaClass.getResourceAsStream(shaderPath)
            assertNotNull(stream, "Shader resource $shaderPath must exist")
            val content = stream.bufferedReader().use { it.readText() }
            assert(content.isNotBlank()) { "Shader resource $shaderPath must not be blank" }
        }
    }
}
