package ch.heiafr.intelliprolog

import ch.heiafr.intelliprolog.module.PrologModuleType
import ch.heiafr.intelliprolog.sdk.PrologSdkType
import ch.heiafr.intelliprolog.util.OSUtil
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.startup.StartupActivity
import com.intellij.util.ui.UIUtil
import java.io.File

class PrologProjectComponent : StartupActivity {
    companion object {
        const val PROLOG_PATH_NOT_FOUND = "gprolog not found in PATH. It can cause issues." +
                "Please specify prolog executable SDK for project."
    }

    fun invokeInUI(block: () -> Unit) {
        UIUtil.invokeAndWaitIfNeeded(Runnable { block() })
    }

    fun getPrologModules(project: Project): List<Module> {
        return project.modules.filter { ModuleType.get(it) == PrologModuleType.INSTANCE }
    }

    override fun runActivity(project: Project) {
        if (getPrologModules(project).isNotEmpty()) {
            val paths = System.getenv("PATH")!!.split(File.pathSeparator.toRegex()).toTypedArray().toMutableList()

            val sdk = ProjectRootManager.getInstance(project).projectSdk
            if (sdk != null && sdk.sdkType is PrologSdkType) {
                paths.add(sdk.homePath + File.separator + "bin")
            }

            if (OSUtil.isMac) {
                paths.add("/usr/local/bin")
            }
        }
    }
}
