package ch.heiafr.intelliprolog.module

import ch.heiafr.intelliprolog.PrologIcons
import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.util.Condition
import javax.swing.Icon


class PrologModuleType : ModuleType<PrologModuleBuilder>("PROLOG_MODULE") {

    override fun createModuleBuilder(): PrologModuleBuilder {
        return PrologModuleBuilder()
    }

    override fun getName(): String {
        return "Prolog Module"
    }

    override fun getDescription(): String {
        return "Prolog Module"
    }

    override fun getIcon(): Icon {
        return PrologIcons.FILE
    }

    override fun getNodeIcon(isOpened: Boolean): Icon {
        return PrologIcons.FILE
    }

    companion object {
        val INSTANCE: PrologModuleType = PrologModuleType()
    }

    override fun modifyProjectTypeStep(settingsStep: SettingsStep, moduleBuilder: ModuleBuilder): ModuleWizardStep? {
        val cond: Condition<SdkTypeId?> = Condition { t -> moduleBuilder.isSuitableSdkType(t) }
        return ProjectWizardStepFactory.getInstance().createJavaSettingsStep(settingsStep, moduleBuilder, cond)
    }
}
