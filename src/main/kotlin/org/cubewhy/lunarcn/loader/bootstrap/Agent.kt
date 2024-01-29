package org.cubewhy.lunarcn.loader.bootstrap

import org.cubewhy.lunarcn.LunarClient
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain

/**
 * 寻找Jordan的木琴
 *
 * @param inst instrumentation
 */
fun fuckTheAgentCheck(inst: Instrumentation) {
    // thanks LunarAntiAgent.jar
    if (!System.getProperties().contains("skip-patch")) {
        inst.addTransformer(object : ClassFileTransformer {
            override fun transform(
                loader: ClassLoader,
                targetClassName: String,
                targetClass: Class<*>?,
                protectionDomain: ProtectionDomain,
                buffer: ByteArray
            ): ByteArray {
                if (!targetClassName.startsWith("com/moonsworth/lunar/")) {
                    return buffer
                }
                val reader = ClassReader(buffer)
                if (reader.interfaces.isNotEmpty()) {
                    return buffer
                }
                val node = ClassNode()
                reader.accept(node, 0)
                for (method in node.methods) {
                    if (method.name != "check" || method.desc != "()V" || method.access != 9) continue
                    println("Jordan你妈死了吗 -> $targetClassName")
                    val inject = InsnList()
                    inject.add(InsnNode(177))
                    method.instructions = inject
                    val writer = ClassWriter(reader, 0)
                    node.accept(writer)
                    println("patch成功!")
                    return writer.toByteArray()
                }
                return buffer
            }
        })
    } else {
        println("取消绕过 禁止Agent, 可能一会崩端")
    }
}

@Suppress("UNUSED_PARAMETER")
fun premain(opt: String?, inst: Instrumentation) {
    // set encoding
    System.setProperty("file.encoding", "UTF-8")
    fuckTheAgentCheck(inst) // MUST fuck the agent check

    val version = findVersion()
    if (version !in arrayOf("1.8", "1.8.9")) {
        println("[LunarCN] $version not supported, disabling ModLoader...")
        return
    }


    inst.addTransformer(object : SafeTransformer {
        override fun transform(loader: ClassLoader, className: String, originalClass: ByteArray): ByteArray? {
            // net/minecraft/ false flags on launchwrapper which gets loaded earlier
            if (className.startsWith("net/minecraft/client/")) {
                inst.removeTransformer(this)

                /*
                Load the rest of the loader using Genesis class loader.
                This allows us to access Minecraft's classes throughout the project.
                */
                loader.loadClass("org.cubewhy.lunarcn.loader.ModLoader")
                    .getDeclaredMethod("init", Instrumentation::class.java)
                    .invoke(null, inst)
            }

            return null
        }
    })
}

private fun findVersion() =
    """--version\s+(\S+)""".toRegex()
        .find(System.getProperty("sun.java.command"))
        ?.groupValues?.get(1)
