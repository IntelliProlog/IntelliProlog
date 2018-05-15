package ch.eif.intelliprolog.module

import ch.eif.intelliprolog.PrologFileType
import ch.eif.intelliprolog.PrologIcons
import com.intellij.openapi.module.ModuleType
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

}
