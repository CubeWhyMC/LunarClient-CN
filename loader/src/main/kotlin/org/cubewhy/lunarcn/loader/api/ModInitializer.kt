package org.cubewhy.lunarcn.loader.api

import net.minecraft.crash.CrashReport

interface ModInitializer {
    fun onPreInit() {}
    fun onInit() {}
    fun onStart() {}
    fun onCrash(crashReport: CrashReport) {}
    fun onStop() {}
}
