package ch.eif.intelliprolog.util

import com.intellij.openapi.util.SystemInfo

object OSUtil {
    val newLine = System.getProperty("line.separator").toString()

    val isLinux: Boolean = SystemInfo.isLinux
    val isWindows: Boolean = SystemInfo.isWindows
    val isMac: Boolean = SystemInfo.isMac

    @JvmStatic
    fun getExe(cmd: String): String = if (isWindows) cmd + ".exe" else cmd

    fun userHome(): String = System.getProperty("user.home")!!

    fun removeExtension(name: String): String {
        if (name.endsWith(".exe")) {
            return name.substring(name.length - 4)
        }
        return name
    }
}