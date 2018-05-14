package ch.eif.intelliprolog.repl.runner;

import ch.eif.intelliprolog.psi.PrologFile;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrologRunConfigurationProducer extends RunConfigurationProducer<PrologRunConfiguration> {

    public PrologRunConfigurationProducer() {
        super(PrologRunConfigurationType.getInstance());
    }

    @Nullable
    private static VirtualFile findRunnablePrologFile(final @NotNull Ref<PsiElement> source) {

        PsiFile file = source.get().getContainingFile();
        if (!(file instanceof PrologFile)) {
            return null;
        }
        VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null) {
            return null;
        }
        return virtualFile;
    }

    @Override
    protected boolean setupConfigurationFromContext(PrologRunConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {

        VirtualFile virtualFile = findRunnablePrologFile(sourceElement);

        configuration.setPathToSourceFile(virtualFile.getPath());

        return true;
    }

    @Override
    public boolean isConfigurationFromContext(PrologRunConfiguration configuration, ConfigurationContext context) {
        return context.getPsiLocation().getContainingFile() instanceof PrologFile;
    }
}
