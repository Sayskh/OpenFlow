package org.openflow.core.plugin

import kotlinx.serialization.Serializable

@Serializable
enum class PluginType {
    ShaderEffect,
    ExpressionPreset,
    AudioProcessor
}

@Serializable
enum class ParameterType {
    FloatSlider,
    IntSlider,
    ColorPicker,
    Toggle,
    Dropdown
}

@Serializable
data class ParameterDefinition(
    val key: String,
    val label: String,
    val type: ParameterType,
    val min: Float? = null,
    val max: Float? = null,
    val default: Float? = null,
    val options: List<String>? = null
)

@Serializable
data class PluginManifest(
    val id: String,
    val name: String,
    val version: String,
    val category: String,
    val author: String,
    val description: String,
    val entryPoint: String,
    val type: PluginType,
    val parameters: List<ParameterDefinition> = emptyList()
)
