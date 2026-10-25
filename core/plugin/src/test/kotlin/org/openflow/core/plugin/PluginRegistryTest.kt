package org.openflow.core.plugin

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PluginRegistryTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun testManifestDeserialization() {
        val jsonText = """
            {
              "id": "chromatic_aberration",
              "name": "Chromatic Aberration",
              "version": "1.0.0",
              "category": "Distortion",
              "author": "OpenFlow",
              "description": "RGB channel split distortion effect",
              "entryPoint": "shader.frag",
              "type": "ShaderEffect",
              "parameters": [
                {
                  "key": "offset",
                  "label": "Offset",
                  "type": "FloatSlider",
                  "min": 0.0,
                  "max": 0.05,
                  "default": 0.005
                },
                {
                  "key": "mode",
                  "label": "Mode",
                  "type": "Dropdown",
                  "options": ["Fast", "Quality"]
                }
              ]
            }
        """.trimIndent()

        val manifest = json.decodeFromString<PluginManifest>(jsonText)
        assertEquals("chromatic_aberration", manifest.id)
        assertEquals("Chromatic Aberration", manifest.name)
        assertEquals("1.0.0", manifest.version)
        assertEquals("Distortion", manifest.category)
        assertEquals(PluginType.ShaderEffect, manifest.type)
        assertEquals("shader.frag", manifest.entryPoint)
        assertEquals(2, manifest.parameters.size)

        val offsetParam = manifest.parameters[0]
        assertEquals("offset", offsetParam.key)
        assertEquals(ParameterType.FloatSlider, offsetParam.type)
        assertEquals(0.0f, offsetParam.min)
        assertEquals(0.05f, offsetParam.max)
        assertEquals(0.005f, offsetParam.default)

        val modeParam = manifest.parameters[1]
        assertEquals("mode", modeParam.key)
        assertEquals(ParameterType.Dropdown, modeParam.type)
        assertEquals(listOf("Fast", "Quality"), modeParam.options)
    }

    @Test
    fun testDiscoverBuiltInPlugins() = runTest {
        val registry = PluginRegistry(json)
        val discovered = registry.discoverBuiltInPlugins()

        assertEquals(3, discovered.size)
        assertEquals(3, registry.plugins.value.size)

        val chromatic = registry.getPlugin("chromatic_aberration")
        assertNotNull(chromatic)
        assertEquals("Distortion", chromatic.category)
        assertEquals(PluginType.ShaderEffect, chromatic.type)
        val chromaticShader = registry.getShaderSource("chromatic_aberration")
        assertNotNull(chromaticShader)
        assertTrue(chromaticShader.contains("uniform float offset;"))
        assertTrue(chromaticShader.contains("fragColor = vec4(r, g, b, a);"))

        val vignette = registry.getPlugin("vignette")
        assertNotNull(vignette)
        assertEquals(PluginType.ShaderEffect, vignette.type)
        val vignetteShader = registry.getShaderSource("vignette")
        assertNotNull(vignetteShader)
        assertTrue(vignetteShader.contains("smoothstep(radius, radius - softness, dist);"))

        val bounce = registry.getPlugin("inertia_bounce")
        assertNotNull(bounce)
        assertEquals(PluginType.ExpressionPreset, bounce.type)
        val bounceExpr = registry.getExpressionSource("inertia_bounce")
        assertNotNull(bounceExpr)
        assertTrue(bounceExpr.contains("function evaluate"))
        assertTrue(bounceExpr.contains("Math.sin(t * w)"))

        // Cross-type source retrieval should fail/return null
        assertNull(registry.getShaderSource("inertia_bounce"))
        assertNull(registry.getExpressionSource("chromatic_aberration"))
    }

    @Test
    fun testStorageDirectoryPluginDiscovery() = runTest {
        val tempDir = Files.createTempDirectory("openflow_plugins_test").toFile()
        tempDir.deleteOnExit()

        val customPluginDir = File(tempDir, "custom_invert")
        customPluginDir.mkdirs()

        val manifestFile = File(customPluginDir, "manifest.json")
        manifestFile.writeText(
            """
            {
              "id": "custom_invert",
              "name": "Custom Invert",
              "version": "0.1.0",
              "category": "Color",
              "author": "CommunityDev",
              "description": "Inverts color buffer",
              "entryPoint": "invert.frag",
              "type": "ShaderEffect",
              "parameters": [
                {
                  "key": "invertAlpha",
                  "label": "Invert Alpha",
                  "type": "Toggle",
                  "default": 0.0
                }
              ]
            }
            """.trimIndent()
        )

        val shaderFile = File(customPluginDir, "invert.frag")
        shaderFile.writeText(
            """
            #version 300 es
            precision mediump float;
            in vec2 vTexCoord;
            out vec4 fragColor;
            uniform sampler2D uTexture;
            uniform float invertAlpha;
            void main() {
                vec4 c = texture(uTexture, vTexCoord);
                fragColor = vec4(1.0 - c.rgb, invertAlpha > 0.5 ? 1.0 - c.a : c.a);
            }
            """.trimIndent()
        )

        val registry = PluginRegistry(json)
        val discovered = registry.discoverFromStorage(tempDir)

        assertEquals(1, discovered.size)
        assertEquals("custom_invert", discovered.first().id)
        assertEquals(1, registry.plugins.value.size)

        val loadedShader = registry.getShaderSource("custom_invert")
        assertNotNull(loadedShader)
        assertTrue(loadedShader.contains("invertAlpha > 0.5"))

        tempDir.deleteRecursively()
    }
}
