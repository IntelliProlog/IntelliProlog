package ch.eif.intelliprolog.runner;

import ch.eif.intelliprolog.PrologFileType;
import ch.eif.intelliprolog.PrologIcons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

public class PrologRunConfigurationType extends ConfigurationTypeBase {

    private PrologRunConfigurationType() {
        super("PrologCommandLineConfigurationType", "Prolog", "Prolog", PrologIcons.FILE);
        addFactory(new ConfigurationFactory(this) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new PrologRunConfiguration(project, PrologRunConfigurationType.this);
            }

            @Override
            public boolean isApplicable(@NotNull Project project) {
                return FileTypeIndex.containsFileOfType(PrologFileType.INSTANCE, GlobalSearchScope.projectScope(project));
            }
        });
    }

    public static PrologRunConfigurationType getInstance() {
        return Extensions.findExtension(CONFIGURATION_TYPE_EP, PrologRunConfigurationType.class);
    }
}
