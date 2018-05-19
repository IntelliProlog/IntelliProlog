package ch.eif.intelliprolog.module

import ch.eif.intelliprolog.sdk.PrologSdkType
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.StdModuleTypes
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File

class PrologModuleBuilder : ModuleBuilder() {

    override fun getBuilderId() = "intelliprolog.module.builder"

    override fun modifySettingsStep(settingsStep: SettingsStep): ModuleWizardStep? =
            StdModuleTypes.JAVA!!.modifySettingsStep(settingsStep, this)


    override fun getGroupName(): String? = "Prolog"

    override fun getPresentableName(): String? = "Prolog"

    override fun createWizardSteps(wizardContext: WizardContext, modulesProvider: ModulesProvider): Array<ModuleWizardStep> =
            moduleType.createWizardSteps(wizardContext, this, modulesProvider)

    override fun getModuleType(): PrologModuleType {
        return PrologModuleType.INSTANCE
    }

    override fun setupRootModel(rootModel: ModifiableRootModel?) {
        if (myJdk != null) {
            rootModel!!.sdk = myJdk
        } else {
            rootModel!!.inheritSdk()
        }

        val contentEntry = doAddContentEntry(rootModel)
        if (contentEntry != null) {
            val srcPath = contentEntryPath!! + File.separator + "src"
            File(srcPath).mkdirs()
            val sourceRoot = LocalFileSystem.getInstance()!!.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(srcPath))
            if (sourceRoot != null) {
                contentEntry.addSourceFolder(sourceRoot, false, "")
            }
        }

    }

    override fun isSuitableSdkType(sdkType: SdkTypeId?): Boolean {
        return sdkType is PrologSdkType
    }

}