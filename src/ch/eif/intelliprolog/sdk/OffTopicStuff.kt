package ch.eif.intelliprolog.sdk

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

fun runCommand(cmd: String, arg: String): String? {  // workingDir: File
    return try {
        // println("processBuilder: " + cmd + arg)
        val proc = ProcessBuilder(cmd, arg)  //ProcessBuilder("cmd", "/c", "dir")
                // .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
        proc.waitFor(2, TimeUnit.SECONDS)
        val res = proc.inputStream.bufferedReader().readText()
        return res
    } catch(e: IOException) {
        e.printStackTrace()
        null
    }
}

// Hard to believe: on windows, "gprolog.exe --version" writes something on the
// console, but not on the process stdout...

fun getPrologVersion(prologPath: File): String? {
    if (prologPath.isDirectory) {
        return null
    }
    val versionFlag = "--version"  // --version --entry_goal halt --query_goal halt
    val output = runCommand(prologPath.absolutePath, versionFlag) ?: return "1.4.x(?)"
    print(output)
    print(output.length)
    val eolIndex = output.indexOf("\n")
    val firstLine = if (eolIndex > 0) output.substring(0, eolIndex) else output
    println(firstLine)
    return firstLine // firstLine.split("\\s".toRegex()).last()
}

fun main(args: Array<String>) {
    val v = "E:\\_apps\\GNU-Prolog\\bin\\gprolog.exe"
    println(getPrologVersion(File(v)))
    println("ab cd ef".split("\\s".toRegex()).last())
}