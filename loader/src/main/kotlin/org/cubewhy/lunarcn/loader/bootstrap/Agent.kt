package org.cubewhy.lunarcn.loader.bootstrap

import java.lang.instrument.Instrumentation

@Suppress("UNUSED_PARAMETER")
fun premain(opt: String?, inst: Instrumentation) {
    val version = findVersion()
    if (version !in arrayOf("1.8", "1.8.9")) {
        println("[LunarCN]] $version not supported, disabling ModLoader...")
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
