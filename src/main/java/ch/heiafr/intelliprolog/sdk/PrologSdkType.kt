package ch.heiafr.intelliprolog.sdk

import ch.heiafr.intelliprolog.PrologIcons
import ch.heiafr.intelliprolog.util.PrologVersion
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VirtualFile
import org.jdom.Element
import java.io.File
import java.io.FileFilter
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.swing.Icon

class PrologSdkType : SdkType("GPROLOG") {

    companion object {
        private val WINDOWS_EXECUTABLE_SUFFIXES = arrayOf("cmd", "exe", "bat", "com")
        val INSTANCE: PrologSdkType = PrologSdkType()
        private val GPROLOG_ICON: Icon = PrologIcons.FILE

        private fun getLatestVersion(prologPaths: List<File>): SDKInfo? {
            val length = prologPaths.size
            if (length == 0)
                return null
            if (length == 1)
                return SDKInfo(prologPaths[0])
            val prologDirs = ArrayList<SDKInfo>()
            for (name in prologPaths) {
                prologDirs.add(SDKInfo(name))
            }
            prologDirs.sortWith(Comparator { o1, o2 -> o1.version.compareTo(o2.version) })
            return prologDirs[prologDirs.size - 1]
        }

        fun checkForProlog(path: String): Boolean {
            val file = File(path)
            if (file.isDirectory) {
                val children = file.listFiles(object : FileFilter {
                    override fun accept(f: File): Boolean {
                        if (f.isDirectory)
                            return false
                        return f.name == "gprolog"
                    }
                })
                return children.isNotEmpty()
            } else {
                return isProlog(file.name)
            }
        }

        fun isProlog(name: String): Boolean =
                name == "gprolog" ||
                name == "gprolog.exe" ||
                name.matches("gprolog-[.0-9*]+".toRegex())

        private fun runCommand(cmd: String, arg: String): String? {  // workingDir: File
            return try {
                val proc = ProcessBuilder(cmd, arg)
                        // .directory(workingDir)
                        .redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .redirectError(ProcessBuilder.Redirect.PIPE)
                        .start()
                proc.waitFor(2, TimeUnit.SECONDS)
                proc.inputStream.bufferedReader().readText()
            } catch(e: IOException) {
                e.printStackTrace()
                null
            }
        }

        fun getPrologVersion(prologPath: File): String? {
            if (prologPath.isDirectory) {
                return null
            }
            return "1.5.0" // TODO determine version instead of hard-coding it!!
            // This won't work: on Windows,  "gprolog.exe --version" writes
            // something on the console, but not on the process stdout...
//            val versionFlag = "--version"
//            val output = runCommand(prologPath.absolutePath, versionFlag) ?: return "1.4.x(?)"
//            val eolIndex = output.indexOf("\n")
//            val firstLine = if (eolIndex > 0) output.substring(0, eolIndex) else output
//            return firstLine
        }
    }

    class SDKInfo(val prologPath: File) {
        val version: PrologVersion = getVersion(prologPath.name)

        companion object {
            fun getVersion(name: String?): PrologVersion {
                val versionStr: List<String> = if (name == null) {
                    listOf()
                } else {
                    name.split("[^0-9]+".toRegex()).filter { it.isNotEmpty() }
                }
                val parts = ArrayList<Int>()
                for (part in versionStr) {
                    if (part.isEmpty())
                        continue
                    try {
                        parts.add(part.toInt())
                    } catch (nfex: NumberFormatException) {

                    }
                }
                return PrologVersion(parts)
            }
        }
    }

    override fun getHomeChooserDescriptor(): FileChooserDescriptor {
        val isWindows = SystemInfo.isWindows
        return object : FileChooserDescriptor(true, false, false, false, false, false) {
            @Throws(Exception::class)
            override fun validateSelectedFiles(files: Array<out VirtualFile>) {
                if (files.size != 0) {
                    if (!isValidSdkHome(files[0].path)) {
                        throw Exception("Not valid gprolog " + files[0].name)
                    }
                }
            }

            override fun isFileVisible(file: VirtualFile, showHiddenFiles: Boolean): Boolean {
                if (!file.isDirectory) {
                    if (!file.name.lowercase().startsWith("gprolog")) {
                        return false
                    }
                    if (isWindows) {
                        val path = file.path
                        var looksExecutable = false
                        for (ext in WINDOWS_EXECUTABLE_SUFFIXES) {
                            if (path.endsWith(ext)) {
                                looksExecutable = true
                                break
                            }
                        }
                        return looksExecutable && super.isFileVisible(file, showHiddenFiles)
                    }
                }
                return super.isFileVisible(file, showHiddenFiles)
            }
        }.withTitle("Select GProlog executable").withShowHiddenFiles(SystemInfo.isUnix)
    }

    override fun suggestHomePath(): String? {
        val versions: List<File>
        if (SystemInfo.isLinux) {
            val versionsRoot = File("/usr/bin")
            if (!versionsRoot.isDirectory) {
                return null
            }
            versions = (versionsRoot.listFiles { dir, name ->
                !File(dir,name).isDirectory && isProlog(name.lowercase())
            }?.toList() ?: listOf())
        } else if (SystemInfo.isWindows) {
            val cDrive = "C:"
            val versionsRoot = File(cDrive, "GNU-Prolog")
            if (!versionsRoot.isDirectory)
                return cDrive
            versions = versionsRoot.listFiles()?.toList() ?: listOf()
        } else if (SystemInfo.isMac) {
            val macVersions = ArrayList<File>()
            val brewVersionsRoot = File("/usr/local/Cellar/gnu-prolog")
            if (brewVersionsRoot.isDirectory) {
                macVersions.addAll(brewVersionsRoot.listFiles()?.toList() ?: listOf())
            }
            versions = macVersions
        } else {
            return null
        }
        val latestVersion = getLatestVersion(versions)

        return latestVersion?.prologPath?.absolutePath
    }

    override fun isValidSdkHome(path: String): Boolean {
        return checkForProlog(path)
    }

//    override fun isValidSdkHome(path: String?): Boolean {
//        return checkForProlog(path!!)
//    }

//  override fun suggestSdkName(currentSdkName: String?, sdkHome: String?): String {
    override fun suggestSdkName(currentSdkName: String?, sdkHome: String): String {
        val suggestedName: String
        if (!currentSdkName.isNullOrEmpty()) {
            suggestedName = currentSdkName
        } else {
            val versionString = getVersionString(sdkHome)
            if (versionString != null) {
                suggestedName = "GNU-Prolog$versionString"
            } else {
                suggestedName = "Unknown"
            }
        }
        return suggestedName
    }

    override fun getVersionString(sdkHome: String?): String? {
        if (sdkHome == null) {
            return null
        }
        val versionString: String? = getPrologVersion(File(sdkHome))
        if (versionString != null && versionString.isEmpty()) {
            return null
        }

        return versionString
    }

    override fun createAdditionalDataConfigurable(sdkModel: SdkModel, sdkModificator: SdkModificator): AdditionalDataConfigurable? {
        return null
    }

    override fun saveAdditionalData(additionalData: SdkAdditionalData, additional: Element) {
    }

    override fun loadAdditionalData(additional: Element): SdkAdditionalData? {
        return null
    }

    override fun getPresentableName(): String {
        return "GNU-Prolog"
    }

    override fun getIcon(): Icon {
        return GPROLOG_ICON
    }

/*  // Deprecated (calls getIcon())
    override fun getIconForAddAction(): Icon {
        return icon
    }
*/

    override fun setupSdkPaths(sdk: Sdk) {
    }

    override fun isRootTypeApplicable(type: OrderRootType): Boolean {
        return true
    }
}
