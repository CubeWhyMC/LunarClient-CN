package org.cubewhy.lunarcn.loader.api.event

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.renderer.entity.RendererLivingEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.Packet
import net.minecraft.network.play.server.S38PacketPlayerListItem.AddPlayerData
import net.minecraft.util.IChatComponent
import net.minecraft.world.World
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

abstract class Event
abstract class CancellableEvent : Event() {
    @get:JvmName("isCancelled")
    var cancelled: Boolean = false
}

sealed class TickEvent : Event() {
    data object Pre : TickEvent()
    data object Post : TickEvent()
}

class KeyboardEvent : Event() {

    val keyCode: Int =
        if (Keyboard.getEventKey() == 0) Keyboard.getEventCharacter().code + 256
        else Keyboard.getEventKey()

    val keyState: Boolean = Keyboard.getEventKeyState()

}

class MouseEvent : CancellableEvent() {

    val x: Int = Mouse.getEventX()
    val y: Int = Mouse.getEventY()

    @get:JvmName("getDX")
    val dx: Int = Mouse.getEventDX()

    @get:JvmName("getDY")
    val dy: Int = Mouse.getEventDY()

    @get:JvmName("getDWheel")
    val dwheel: Int = Mouse.getEventDWheel()
    val button: Int = Mouse.getEventButton()
    val buttonState: Boolean = Mouse.getEventButtonState()
    val nanoseconds: Long = Mouse.getEventNanoseconds()

}

class ChatReceivedEvent(val message: IChatComponent) : CancellableEvent()
class ChatSentEvent(val message: String) : CancellableEvent()
class GuiOpenEvent(val screen: GuiScreen?) : CancellableEvent()

sealed class RenderGameOverlayEvent(val partialTicks: Float) : Event() {

    class Pre(partialTicks: Float) : RenderGameOverlayEvent(partialTicks)
    class Post(partialTicks: Float) : RenderGameOverlayEvent(partialTicks)

}

sealed class EntityListEvent(val entity: Entity) : Event() {

    class Add(entity: Entity) : EntityListEvent(entity)
    class Remove(entity: Entity) : EntityListEvent(entity)

}

sealed class PlayerListEvent(val playerData: AddPlayerData) : Event() {

    class Add(playerData: AddPlayerData) : PlayerListEvent(playerData)
    class Remove(playerData: AddPlayerData) : PlayerListEvent(playerData)

}

sealed class RenderLivingEvent(
    val renderer: RendererLivingEntity<EntityLivingBase>,
    val entity: EntityLivingBase,
    val x: Double,
    val y: Double,
    val z: Double,
    val partialTicks: Float
) : CancellableEvent() {

    class Pre(
        renderer: RendererLivingEntity<EntityLivingBase>,
        entity: EntityLivingBase,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float
    ) : RenderLivingEvent(renderer, entity, x, y, z, partialTicks)

    class Post(
        renderer: RendererLivingEntity<EntityLivingBase>,
        entity: EntityLivingBase,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float
    ) : RenderLivingEvent(renderer, entity, x, y, z, partialTicks)

}

class RenderWorldEvent(val partialTicks: Float) : Event()

class RenderHandEvent(val partialTicks: Float) : CancellableEvent()

class ServerConnectEvent(
    val ip: String,
    val port: Int,
) : Event() {
    val serverData: ServerData = Minecraft.getMinecraft().currentServerData
}

sealed class StartGameEvent : Event() {

    object Pre : StartGameEvent()
    object Post : StartGameEvent()

}

object ShutdownEvent : Event()

sealed class WorldEvent(val world: World) : Event() {
    class Load(world: World) : WorldEvent(world)
    class Unload(world: World) : WorldEvent(world)
}

sealed class PacketEvent(val packet: Packet<*>) : CancellableEvent() {
    class Send(packet: Packet<*>) : PacketEvent(packet)
    class Receive(packet: Packet<*>) : PacketEvent(packet)
}
