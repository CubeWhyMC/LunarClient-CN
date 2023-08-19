package org.cubewhy.lunarcn.loader.utils

import org.cubewhy.lunarcn.loader.api.event.Event
import org.cubewhy.lunarcn.loader.api.event.EventBus
import org.cubewhy.lunarcn.loader.api.util.InsnBuilder

internal inline fun <reified T : Any> InsnBuilder.getSingleton() =
    getstatic(internalNameOf<T>(), "INSTANCE", "L${internalNameOf<T>()};")

internal fun InsnBuilder.callEvent() {
    invokestatic(
        internalNameOf<EventBus>(),
        "callEvent",
        "(L${internalNameOf<Event>()};)V"
    )
}

internal fun InsnBuilder.println() {
    getstatic("java/lang/System", "out", "Ljava/io/PrintStream;")
    swap
    invokevirtual("java/io/PrintStream", "println", "(Ljava/lang/Object;)V")
}
