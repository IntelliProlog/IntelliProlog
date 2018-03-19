package ch.eif.intelliprolog;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PrologRunConfigurationType implements ConfigurationType {
    private PrologConfigurationFactory pcf = new PrologConfigurationFactory(this);

    @Nls
    @Override
    public String getDisplayName() {
        return "Prolog";
    }

    @Nls
    @Override
    public String getConfigurationTypeDescription() {
        return "GnuProlog Run Configuration Type";
    }

    @Override
    public Icon getIcon() {
        return PrologIcons.FILE;
    }

    @NotNull
    @Override
    public String getId() {
        return "GPROLOG_RUN_CONFIGURATION";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{pcf};
    }
}
