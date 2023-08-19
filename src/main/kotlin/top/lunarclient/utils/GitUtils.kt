package top.lunarclient.utils

import java.util.Properties

@Suppress("unused")
object GitUtils {
    @JvmField
    val gitInfo = Properties().also {
        val inputStream = GitUtils::class.java.classLoader.getResourceAsStream("git.properties")
        if (inputStream != null) {
            it.load(inputStream)
        } else {
            it["git.branch"] = "master"
        }
    }

    @JvmField
    val gitBranch: String = ((gitInfo["git.branch"] ?: "unknown").toString())
    @JvmField
    val gitRemote: String = ((gitInfo["git.remote.origin.url"] ?: "unknown").toString())
    @JvmField
    var buildVersion: String = ((gitInfo["git.build.version"] ?: "unknown").toString())
}