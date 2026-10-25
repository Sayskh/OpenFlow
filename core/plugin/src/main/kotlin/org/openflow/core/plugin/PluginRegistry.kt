package org.openflow.core.plugin

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream

class PluginRegistry(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
) {
    private sealed interface PluginSource {
        class FileSource(val rootDir: File, val entryPoint: String) : PluginSource
        class ResourceSource(val resourcePath: String) : PluginSource
    }

    private val _plugins = MutableStateFlow<List<PluginManifest>>(emptyList())
    val plugins: StateFlow<List<PluginManifest>> = _plugins.asStateFlow()

    private val manifestMap = mutableMapOf<String, PluginManifest>()
    private val sourceMap = mutableMapOf<String, PluginSource>()

    fun registerManifest(manifest: PluginManifest, source: (entryPoint: String) -> String?) {
        manifestMap[manifest.id] = manifest
        sourceMap[manifest.id] = object : PluginSource {
            val resolver = source
        }.let {
            // Store as custom lambda source
            CustomSource(source)
        }
        syncPluginsFlow()
    }

    private class CustomSource(val loader: (String) -> String?) : PluginSource

    fun registerFromDirectory(pluginDir: File): PluginManifest? {
        val manifestFile = File(pluginDir, "manifest.json")
        if (!manifestFile.exists() || !manifestFile.isFile) return null

        return runCatching {
            val manifestText = manifestFile.readText()
            val manifest = json.decodeFromString<PluginManifest>(manifestText)
            manifestMap[manifest.id] = manifest
            sourceMap[manifest.id] = PluginSource.FileSource(pluginDir, manifest.entryPoint)
            syncPluginsFlow()
            manifest
        }.getOrNull()
    }

    fun discoverFromStorage(storageDir: File): List<PluginManifest> {
        if (!storageDir.exists() || !storageDir.isDirectory) return emptyList()

        val registered = mutableListOf<PluginManifest>()
        storageDir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                registerFromDirectory(file)?.let { registered.add(it) }
            }
        }
        return registered
    }

    fun registerFromResource(
        resourcePrefix: String,
        classLoader: ClassLoader = Thread.currentThread().contextClassLoader ?: javaClass.classLoader
    ): PluginManifest? {
        val normalizedPrefix = resourcePrefix.trimEnd('/')
        val manifestResource = "$normalizedPrefix/manifest.json"
        val stream = classLoader.getResourceAsStream(manifestResource) ?: return null

        return runCatching {
            val manifestText = stream.bufferedReader().use { it.readText() }
            val manifest = json.decodeFromString<PluginManifest>(manifestText)
            manifestMap[manifest.id] = manifest
            sourceMap[manifest.id] = PluginSource.ResourceSource("$normalizedPrefix/${manifest.entryPoint}")
            syncPluginsFlow()
            manifest
        }.getOrNull()
    }

    fun discoverBuiltInPlugins(
        builtInPluginPaths: List<String> = DEFAULT_BUILTIN_PATHS,
        classLoader: ClassLoader = Thread.currentThread().contextClassLoader ?: javaClass.classLoader
    ): List<PluginManifest> {
        return builtInPluginPaths.mapNotNull { path ->
            registerFromResource(path, classLoader)
        }
    }

    fun getPlugin(pluginId: String): PluginManifest? = manifestMap[pluginId]

    fun getShaderSource(
        pluginId: String,
        classLoader: ClassLoader = Thread.currentThread().contextClassLoader ?: javaClass.classLoader
    ): String? {
        val manifest = manifestMap[pluginId] ?: return null
        if (manifest.type != PluginType.ShaderEffect) return null
        return loadSource(pluginId, manifest.entryPoint, classLoader)
    }

    fun getExpressionSource(
        pluginId: String,
        classLoader: ClassLoader = Thread.currentThread().contextClassLoader ?: javaClass.classLoader
    ): String? {
        val manifest = manifestMap[pluginId] ?: return null
        if (manifest.type != PluginType.ExpressionPreset) return null
        return loadSource(pluginId, manifest.entryPoint, classLoader)
    }

    private fun loadSource(pluginId: String, entryPoint: String, classLoader: ClassLoader): String? {
        return when (val source = sourceMap[pluginId]) {
            is PluginSource.FileSource -> {
                val entryFile = File(source.rootDir, entryPoint)
                if (entryFile.exists() && entryFile.isFile) entryFile.readText() else null
            }
            is PluginSource.ResourceSource -> {
                val stream: InputStream? = classLoader.getResourceAsStream(source.resourcePath)
                stream?.bufferedReader()?.use { it.readText() }
            }
            is CustomSource -> {
                source.loader(entryPoint)
            }
            null -> null
        }
    }

    private fun syncPluginsFlow() {
        _plugins.value = manifestMap.values.toList()
    }

    companion object {
        val DEFAULT_BUILTIN_PATHS = listOf(
            "plugins/effects/chromatic_aberration",
            "plugins/effects/vignette",
            "plugins/expressions/inertia_bounce"
        )
    }
}
