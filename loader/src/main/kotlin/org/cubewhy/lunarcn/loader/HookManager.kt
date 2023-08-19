package org.cubewhy.lunarcn.loader

import org.cubewhy.lunarcn.loader.api.Hook
import org.cubewhy.lunarcn.loader.bootstrap.SafeTransformer
import org.cubewhy.lunarcn.loader.utils.ClassUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.transformers.MixinClassWriter

internal object HookManager : SafeTransformer {
    var hooks: ArrayList<Hook> = run {
        val hookPackage = "org.cubewhy.lunarcn.loader.hooks"
        val hookList = ArrayList<Hook>()
        ClassUtils.resolvePackage(hookPackage, Hook::class.java)
            .forEach { hook ->
                hookList.add(hook.newInstance())
            }
        hookList
    }

    override fun transform(loader: ClassLoader, className: String, originalClass: ByteArray): ByteArray? {
        val hooks = hooks.filter { it.targets.contains("*") || it.targets.contains(className) }
        if (hooks.isEmpty()) return null

        val node = ClassNode()
        val reader = ClassReader(originalClass)
        reader.accept(node, 0)

        var computeFrames = false
        val cfg = Hook.AssemblerConfig { computeFrames = true }

        hooks.forEach { it.transform(node, cfg) }
        val flags = if (computeFrames) ClassWriter.COMPUTE_FRAMES else ClassWriter.COMPUTE_MAXS

        // HACK: use MixinClassWriter because it doesn't load classes when computing frames.
        val writer = MixinClassWriter(reader, flags)
        node.accept(writer)
        return writer.toByteArray()
    }
}
