package org.cubewhy.lunarcn.loader.hooks

import org.cubewhy.lunarcn.loader.api.event.CancellableEvent
import org.cubewhy.lunarcn.loader.api.event.RenderHandEvent
import org.cubewhy.lunarcn.loader.api.util.asm
import org.cubewhy.lunarcn.loader.util.*
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.LdcInsnNode

internal class RenderHandEventHook : org.cubewhy.lunarcn.loader.api.Hook("net/minecraft/client/renderer/EntityRenderer") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        val renderWorldPass = node.methods.named("renderWorldPass")

        val ifeq = renderWorldPass.instructions.find {
            it is LdcInsnNode && it.cst == "hand"
        }!!.next<JumpInsnNode> { it.opcode == Opcodes.IFEQ }!!

        renderWorldPass.instructions.insert(
            ifeq,
            asm {
                new(internalNameOf<RenderHandEvent>())
                dup; dup
                fload(2)
                invokespecial(internalNameOf<RenderHandEvent>(), "<init>", "(F)V")

                callEvent()

                invokevirtual(internalNameOf<CancellableEvent>(), "isCancelled", "()Z")
                ifne(ifeq.label)
            }
        )
    }
}
