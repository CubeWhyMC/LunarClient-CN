package org.cubewhy.lunarcn.loader.utils

import org.cubewhy.lunarcn.loader.ModLoader
import java.util.*

class GitUtils {
    companion object {
        @JvmField
        val gitInfo = Properties().also {
            val inputStream = ModLoader::class.java.classLoader.getResourceAsStream("git.properties")
            if (inputStream != null) {
                it.load(inputStream)
            } else {
                it["git.branch"] = "master"
            }
        }

        @JvmField
        val gitBranch: Any = (gitInfo["git.branch"] ?: "unknown")
    }
}