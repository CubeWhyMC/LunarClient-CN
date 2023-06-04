package org.cubewhy.lunarcn.loader.hooks

import org.cubewhy.lunarcn.loader.api.event.KeyboardEvent
import org.cubewhy.lunarcn.loader.api.util.asm
import org.cubewhy.lunarcn.loader.util.*
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode

internal class KeyboardEventHook : org.cubewhy.lunarcn.loader.api.Hook("net/minecraft/client/Minecraft") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.methods.named("runTick").let { mn ->
            mn.instructions.insert(
                mn.instructions.find { it is MethodInsnNode && it.name == "dispatchKeypresses" },
                asm {
                    new(internalNameOf<KeyboardEvent>())
                    dup
                    invokespecial(
                        internalNameOf<KeyboardEvent>(),
                        "<init>",
                        "()V"
                    )
                    callEvent()
                }
            )
        }
    }
}
