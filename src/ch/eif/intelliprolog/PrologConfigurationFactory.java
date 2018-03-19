package ch.eif.intelliprolog;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class PrologConfigurationFactory extends ConfigurationFactory {
    private static final String FACTORY_NAME = "Gnu Prolog configuration factory";

    PrologConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new PrologRunConfiguration(project, this, "Prolog");
    }

    @Override
    public String getName() {
        return FACTORY_NAME;
    }
}
