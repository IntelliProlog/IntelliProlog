package ch.eif.intelliprolog.runner;

import ch.eif.intelliprolog.PrologFileType;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

class PrologConfigurationFactory extends ConfigurationFactory {
    private static final String FACTORY_NAME = "Gnu Prolog configuration factory";

    public PrologConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new PrologRunConfiguration(project, PrologRunConfigurationType.getInstance());
    }

    @Override
    public boolean isApplicable(@NotNull Project project) {
        return FileTypeIndex.containsFileOfType(PrologFileType.INSTANCE, GlobalSearchScope.projectScope(project));
    }

    @Override
    public String getName() {
        return FACTORY_NAME;
    }
}
