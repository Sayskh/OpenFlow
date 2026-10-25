package org.openflow.engine.render

import android.opengl.GLES20
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * OpenGL ES 2.0 / 3.0 Program abstraction for compiling, linking, and binding shaders.
 */
class GlShader(
    vertexSource: String,
    fragmentSource: String
) {
    val programId: Int
    private val attributes = mutableMapOf<String, Int>()
    private val uniforms = mutableMapOf<String, Int>()

    init {
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)

        programId = GLES20.glCreateProgram().also { program ->
            check(program != 0) { "Failed to create GL program" }
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)

            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                val log = GLES20.glGetProgramInfoLog(program)
                GLES20.glDeleteProgram(program)
                GLES20.glDeleteShader(vertexShader)
                GLES20.glDeleteShader(fragmentShader)
                throw RuntimeException("Could not link GL program:\n$log")
            }

            // Once linked, shaders can be detached and flagged for deletion
            GLES20.glDetachShader(program, vertexShader)
            GLES20.glDetachShader(program, fragmentShader)
            GLES20.glDeleteShader(vertexShader)
            GLES20.glDeleteShader(fragmentShader)
        }
    }

    fun use() {
        GLES20.glUseProgram(programId)
    }

    fun release() {
        if (programId != 0) {
            GLES20.glDeleteProgram(programId)
        }
    }

    fun getAttribLocation(name: String): Int {
        return attributes.getOrPut(name) {
            val location = GLES20.glGetAttribLocation(programId, name)
            check(location != -1) { "Attribute not found: $name" }
            location
        }
    }

    fun getUniformLocation(name: String): Int {
        return uniforms.getOrPut(name) {
            val location = GLES20.glGetUniformLocation(programId, name)
            check(location != -1) { "Uniform not found: $name" }
            location
        }
    }

    fun setUniform1i(name: String, value: Int) {
        GLES20.glUniform1i(getUniformLocation(name), value)
    }

    fun setUniform1f(name: String, value: Float) {
        GLES20.glUniform1f(getUniformLocation(name), value)
    }

    fun setUniform2f(name: String, x: Float, y: Float) {
        GLES20.glUniform2f(getUniformLocation(name), x, y)
    }

    fun setUniform3f(name: String, x: Float, y: Float, z: Float) {
        GLES20.glUniform3f(getUniformLocation(name), x, y, z)
    }

    fun setUniform4f(name: String, x: Float, y: Float, z: Float, w: Float) {
        GLES20.glUniform4f(getUniformLocation(name), x, y, z, w)
    }

    fun setUniformMatrix4fv(name: String, matrix: FloatArray) {
        GLES20.glUniformMatrix4fv(getUniformLocation(name), 1, false, matrix, 0)
    }

    fun bindAttribute(name: String, buffer: FloatBuffer, size: Int, stride: Int = 0) {
        val location = getAttribLocation(name)
        GLES20.glEnableVertexAttribArray(location)
        GLES20.glVertexAttribPointer(location, size, GLES20.GL_FLOAT, false, stride, buffer)
    }

    companion object {
        fun fromResources(
            vertexStream: InputStream,
            fragmentStream: InputStream
        ): GlShader {
            val vertexSource = vertexStream.bufferedReader().use { it.readText() }
            val fragmentSource = fragmentStream.bufferedReader().use { it.readText() }
            return GlShader(vertexSource, fragmentSource)
        }

        fun createFloatBuffer(coords: FloatArray): FloatBuffer {
            return ByteBuffer.allocateDirect(coords.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .apply {
                    put(coords)
                    position(0)
                }
        }

        private fun compileShader(type: Int, source: String): Int {
            val shader = GLES20.glCreateShader(type)
            check(shader != 0) { "Failed to create shader of type $type" }

            GLES20.glShaderSource(shader, source)
            GLES20.glCompileShader(shader)

            val compiled = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                val log = GLES20.glGetShaderInfoLog(shader)
                GLES20.glDeleteShader(shader)
                throw RuntimeException("Could not compile shader (type $type):\n$log\nSource:\n$source")
            }
            return shader
        }
    }
}
