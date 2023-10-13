package org.cubewhy.lunarcn.loader.api

interface ModInitializer {
    fun onPreInit()
    fun onInit()
    fun onStart()
    fun onCrash()
    fun onStop()
}
