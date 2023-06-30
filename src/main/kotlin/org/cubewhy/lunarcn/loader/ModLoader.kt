package org.cubewhy.lunarcn.loader

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.cubewhy.lunarcn.Main
import org.cubewhy.lunarcn.loader.api.ModInitializer
import org.cubewhy.lunarcn.loader.mixins.LunarCnMixinService
import org.cubewhy.lunarcn.loader.mixins.LunarCnMixinTransformer
import org.spongepowered.asm.launch.MixinBootstrap
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.Mixins
import org.spongepowered.asm.service.MixinService
import java.lang.instrument.Instrumentation
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.JarFile
import kotlin.io.path.*

public object ModLoader {
    /**
     * @see [org.cubewhy.lunarcn.loader.bootstrap.premain]
     */
    @JvmStatic
    @OptIn(ExperimentalSerializationApi::class)
    public fun init(inst: Instrumentation) {
        println("[LunarCN Loader] Initializing LunarCN - based on Weave Loader")

        MixinBootstrap.init() // Init mixin

        Mixins.addConfiguration("mixins.lunarcn.json") // Load default mixin config
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT)

        check(MixinService.getService() is LunarCnMixinService) { "Active mixin service is NOT LunarCnMixinService" }

        inst.addTransformer(LunarCnMixinTransformer)
        inst.addTransformer(HookManager)

        val initializers = mutableListOf<ModInitializer>()
        val json = Json { ignoreUnknownKeys = true }
        getOrCreateModDirectory()
            .listDirectoryEntries("*.jar")
            .filter { it.isRegularFile() }
            .map { JarFile(it.toFile()).also(inst::appendToSystemClassLoaderSearch) }
            .forEach { jar ->
                println("[LunarCN Loader] Loading mod ${jar.name}")

                val configEntry = jar.getEntry("lunarcn.mod.json") ?: error("${jar.name} does not contain a lunarcn.mod.json!")
                val config = json.decodeFromStream<ModConfig>(jar.getInputStream(configEntry))

                config.mixinConfigs.forEach(Mixins::addConfiguration)
                HookManager.hooks += config.hooks.map(ModLoader::instantiate)
                initializers += config.entrypoints.map(ModLoader::instantiate)
            }

        //call preInit after all hooks/mixins are added
        initializers.forEach { it.preInit() }

        println("[LunarCN Loader] Load finished")
    }

    @Serializable
    private data class ModConfig(
        val mixinConfigs: List<String> = listOf(),
        val hooks: List<String> = listOf(),
        val entrypoints: List<String>
    )

    /**
     * Grabs the mods' directory, creating it if it doesn't exist.
     * **IF** the file exists as a file and not a directory, it will be deleted.
     *
     * @return The 'mods' directory: `"~/.cubewhy/lunarcn/mods"`
     */
    private fun getOrCreateModDirectory(): Path {
        val dir = Paths.get(Main.configDir.path + "/mods")
        if (dir.exists() && !dir.isDirectory()) Files.delete(dir)
        if (!dir.exists()) dir.createDirectories()
        return dir
    }

    private inline fun<reified T> instantiate(className: String): T =
        Class.forName(className)
            .getConstructor()
            .newInstance() as? T
            ?: error("$className does not implement ${T::class.java.simpleName}!")
}


