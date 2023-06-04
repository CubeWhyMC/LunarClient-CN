package org.cubewhy.lunarcn.loader.hooks

import org.cubewhy.lunarcn.loader.api.Hook
import org.cubewhy.lunarcn.loader.api.event.CancellableEvent
import org.cubewhy.lunarcn.loader.api.event.ChatSentEvent
import org.cubewhy.lunarcn.loader.api.util.asm
import org.cubewhy.lunarcn.loader.util.callEvent
import org.cubewhy.lunarcn.loader.util.internalNameOf
import org.cubewhy.lunarcn.loader.util.named
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LabelNode

internal class ChatSentEventHook : Hook("net/minecraft/client/entity/EntityPlayerSP") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.methods.named("sendChatMessage").instructions.insert(asm {
            new(internalNameOf<ChatSentEvent>())
            dup
            dup
            aload(1)
            invokespecial(
                internalNameOf<ChatSentEvent>(),
                "<init>",
                "(L${internalNameOf<String>()};)V"
            )
            callEvent()

            val end = LabelNode()

            invokevirtual(internalNameOf<CancellableEvent>(), "isCancelled", "()Z")
            ifeq(end)

            _return

            +end
            f_same()
        })
    }
}
