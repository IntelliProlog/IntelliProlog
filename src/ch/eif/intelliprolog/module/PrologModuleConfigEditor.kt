package ch.eif.intelliprolog.module

import com.intellij.openapi.module.ModuleConfigurationEditor
import com.intellij.openapi.roots.ui.configuration.ContentEntriesEditor
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState
import com.intellij.openapi.roots.ui.configuration.OutputEditor

class PrologModuleConfigEditor : ModuleConfigurationEditorProvider {
    override fun createEditors(state: ModuleConfigurationState?): Array<ModuleConfigurationEditor> {
        val module = state!!.rootModel!!.module

        return arrayOf(ContentEntriesEditor(module.name, state),
                OutputEditor(state))
    }
}