package ch.heiafr.intelliprolog.module

import ch.heiafr.intelliprolog.sdk.PrologSdkType
import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.module.StdModuleTypes
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.util.Condition
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File

class PrologModuleBuilder : ModuleBuilder() {

    override fun getBuilderId() = "intelliprolog.module.builder"

    override fun modifySettingsStep(settingsStep: SettingsStep): ModuleWizardStep? =
            StdModuleTypes.JAVA!!.modifySettingsStep(settingsStep, this)

//    override fun modifySettingsStep(settingsStep: SettingsStep): ModuleWizardStep? {
//        val moduleBuilder: ModuleBuilder
//        moduleBuilder = this
//        val cond: Condition<SdkTypeId?>
//        cond = Condition { t -> moduleBuilder.isSuitableSdkType(t) }
//        return ProjectWizardStepFactory.getInstance().createJavaSettingsStep(settingsStep, moduleBuilder, cond)
//    }  // BAP: this does not seem to be working...

    override fun getGroupName(): String? = "Prolog"

    override fun getPresentableName(): String? = "Prolog"

    override fun createWizardSteps(wizardContext: WizardContext, modulesProvider: ModulesProvider): Array<ModuleWizardStep> =
            moduleType.createWizardSteps(wizardContext, this, modulesProvider)

    override fun getModuleType(): PrologModuleType {
        return PrologModuleType.INSTANCE
    }

    override fun setupRootModel(rootModel: ModifiableRootModel) {
        if (myJdk != null) {
            rootModel!!.sdk = myJdk
        } else {
            val candidates = ProjectJdkTable.getInstance().getSdksOfType(PrologSdkType.INSTANCE)
            if (candidates.isEmpty()) {
                rootModel!!.inheritSdk()
            } else {
                // BAP: not sure it's reasonable...
                rootModel.sdk = candidates.get(0)
            }
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