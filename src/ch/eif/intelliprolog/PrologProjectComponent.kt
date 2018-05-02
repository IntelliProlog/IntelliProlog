package ch.eif.intelliprolog

import ch.eif.intelliprolog.module.PrologModuleType
import ch.eif.intelliprolog.sdk.PrologSdkType
import ch.eif.intelliprolog.util.OSUtil
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.util.ui.UIUtil
import java.io.File

class PrologProjectComponent(val project: Project) : ProjectComponent {
    companion object {
        val PROLOG_PATH_NOT_FOUND = "gprolog not found in PATH. It can cause issues."+
                                    "Please specify prolog executable SDK for project."
    }

    fun invokeInUI(block: () -> Unit) {
        UIUtil.invokeAndWaitIfNeeded(object : Runnable {
            override fun run() {
                block()
            }
        })
    }

    fun getPrologModules(): List<Module> {
        val moduleManager = ModuleManager.getInstance(project)!!
        return moduleManager.modules.filter { ModuleType.get(it) == PrologModuleType.INSTANCE}
    }

    override fun projectOpened() {
        if(!getPrologModules().isEmpty()) {
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

    override fun projectClosed() {
    }

    override fun getComponentName(): String {
        return "PrologProjectComponent"
    }

    override fun initComponent() {
    }

    override fun disposeComponent() {
    }
}