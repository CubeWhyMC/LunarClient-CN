package org.cubewhy.lunarcn.loader

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.cubewhy.lunarcn.loader.api.ModInitializer
import org.cubewhy.lunarcn.loader.mixins.LunarCnMixinService
import org.cubewhy.lunarcn.loader.mixins.LunarCnMixinTransformer
import org.cubewhy.lunarcn.loader.utils.AccessWriter
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.launch.MixinBootstrap
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.Mixins
import org.spongepowered.asm.service.MixinService
import java.io.File
import java.io.IOException
import java.lang.instrument.Instrumentation
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.JarFile
import javax.swing.JOptionPane
import kotlin.io.path.*

object ModLoader {
    const val clientLogo = "assets/minecraft/lunarcn/lunarcn.png"
    @JvmStatic
    val configDir: File = File(System.getProperty("configPath", System.getProperty("user.home") + "/.cubewhy/lunarcn"))
    /**
     * @see [org.cubewhy.lunarcn.loader.bootstrap.premain]
     */
    @JvmStatic
    @OptIn(ExperimentalSerializationApi::class)
    fun init(inst: Instrumentation) {
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
                if (jar.getEntry("weave.mod.json") != null) {
                    JOptionPane.showMessageDialog(
                        null,
                        "${jar.name} maybe a Weave mod, lunarCN Loader may failed to load this mod",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                    )
                }

                val configEntry =
                    jar.getEntry("lunarcn.mod.json") ?: error("${jar.name} does not contain a lunarcn.mod.json!")
                val config = json.decodeFromStream<ModConfig>(jar.getInputStream(configEntry))

                config.mixinConfigs.forEach(Mixins::addConfiguration)
                HookManager.hooks += config.hooks.map(ModLoader::instantiate)
                initializers += config.entrypoints.map(ModLoader::instantiate)

                // write access for LunarClient
                try {
                    val accessFileEntry = jar.getEntry("access.txt")
                    val inputStream = jar.getInputStream(accessFileEntry)
                    for (line in inputStream.bufferedReader().readLines()) {
                        writeAccess(line)
                    }
                } catch (err: NullPointerException) {
                    err.printStackTrace()
                    println("${jar.name} didn't have access.txt or failed to write access, so skip write access for this mod")
                }
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

    private fun writeAccess(line: String) {
        val code = line.split("#")[0]
        if (code.startsWith("#")) {
            return // this line is a comment
        }
        val accessCode = code.split(" ")[0]
        val target = code.split(" ")[1]
        var opCodeAccess: Int = Opcodes.ACC_PUBLIC
        println("Writing access ($accessCode) -> $target")
        when (accessCode) {
            "public" -> {
                opCodeAccess = Opcodes.ACC_PUBLIC
            }

            "private" -> {
                opCodeAccess = Opcodes.ACC_PRIVATE
            }

            "protected" -> {
                opCodeAccess = Opcodes.ACC_PROTECTED
            }
        }
        val cr = ClassReader(target.replace(".", "/"))
        val cw = ClassWriter(0)
        val writer = AccessWriter(opCodeAccess, cw)
        cr.accept(writer, ClassReader.SKIP_DEBUG) // write access
    }

    /**
     * Grabs the mods' directory, creating it if it doesn't exist.
     * **IF** the file exists as a file and not a directory, it will be deleted.
     *
     * @return The 'mods' directory: `"~/.cubewhy/lunarcn/mods"`
     */
    private fun getOrCreateModDirectory(): Path {
        val dir = Paths.get(configDir.path + "/mods")
        if (dir.exists() && !dir.isDirectory()) Files.delete(dir)
        if (!dir.exists()) dir.createDirectories()
        return dir
    }

    private inline fun <reified T> instantiate(className: String): T =
        Class.forName(className)
            .getConstructor()
            .newInstance() as? T
            ?: error("$className does not implement ${T::class.java.simpleName}!")
}


