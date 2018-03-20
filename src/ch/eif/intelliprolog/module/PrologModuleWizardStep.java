package ch.eif.intelliprolog.module;

import ch.eif.intelliprolog.ui.PrologProjectSettings;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;

import javax.swing.*;

class PrologModuleWizardStep extends ModuleWizardStep {

    private final PrologProjectSettings projectSettings = new PrologProjectSettings();

    @Override
    public JComponent getComponent() {
        return projectSettings.getPanel();
    }

    @Override
    public void updateDataModel() {
        PropertiesComponent.getInstance().setValue("PROLOG_EXEC_PATH", projectSettings.getExecutablePath());
    }
}
