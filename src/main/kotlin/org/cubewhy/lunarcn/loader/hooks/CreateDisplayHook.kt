package org.cubewhy.lunarcn.loader.hooks

import org.cubewhy.lunarcn.loader.api.Hook
import org.cubewhy.lunarcn.loader.api.event.StartGameEvent
import org.cubewhy.lunarcn.loader.api.util.asm
import org.cubewhy.lunarcn.loader.util.callEvent
import org.cubewhy.lunarcn.loader.util.getSingleton
import org.cubewhy.lunarcn.loader.util.named
import org.lwjgl.opengl.Display
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

internal class CreateDisplayHook : Hook("net/minecraft/client/Minecraft") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        val mn = node.methods.named("createDisplay")

        mn.instructions.insert(asm {
            Display.setTitle(Display.getTitle() + " | Lunar CN")
        })
    }
}