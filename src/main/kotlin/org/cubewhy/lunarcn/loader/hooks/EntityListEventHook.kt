package org.cubewhy.lunarcn.loader.hooks

import org.cubewhy.lunarcn.loader.api.Hook
import org.cubewhy.lunarcn.loader.api.event.EntityListEvent
import org.cubewhy.lunarcn.loader.api.util.asm
import org.cubewhy.lunarcn.loader.util.callEvent
import org.cubewhy.lunarcn.loader.util.internalNameOf
import org.cubewhy.lunarcn.loader.util.named
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

internal class EntityListEventAddHook : Hook("net/minecraft/world/World") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.methods.named("spawnEntityInWorld").instructions.insert(asm {
            new(internalNameOf<EntityListEvent.Add>())
            dup
            aload(1)
            invokespecial(
                internalNameOf<EntityListEvent.Add>(),
                "<init>",
                "(Lnet/minecraft/entity/Entity;)V"
            )
            callEvent()
        })
    }
}

internal class EntityListEventRemoveHook : Hook("net/minecraft/client/multiplayer/WorldClient") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        val mn = node.methods.named("removeEntityFromWorld")
        mn.instructions.insert(mn.instructions.find { it.opcode == Opcodes.IFNULL }, asm {
            new(internalNameOf<EntityListEvent.Remove>())
            dup
            aload(2)
            invokespecial(
                internalNameOf<EntityListEvent.Remove>(),
                "<init>",
                "(Lnet/minecraft/entity/Entity;)V"
            )
            callEvent()
        })
    }
}
