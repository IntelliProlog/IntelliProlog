package ch.eif.intelliprolog.runner;

import ch.eif.intelliprolog.psi.PrologFile;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.roots.ProjectRootManager;
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
    private static VirtualFile findRunnablePrologFile(final @NotNull ConfigurationContext context) {
        final PsiElement psiLocation = context.getPsiLocation();
        final PsiFile psiFile = psiLocation == null ? null : psiLocation.getContainingFile();
        final VirtualFile virtualFile = psiFile != null ? psiFile.getOriginalFile().getVirtualFile() : null;

        if (!(psiFile instanceof PrologFile))
            return null;
        if (!ProjectRootManager.getInstance(context.getProject()).getFileIndex().isInContent(virtualFile))
            return null;
        return virtualFile;
    }

    @Override
    protected boolean setupConfigurationFromContext(PrologRunConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {
        final VirtualFile prologFile = findRunnablePrologFile(context);
        if (prologFile == null)
            return false;
        configuration.setPathToSourceFile(prologFile.getPath());
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(PrologRunConfiguration configuration, ConfigurationContext context) {
        final VirtualFile prologFile = findRunnablePrologFile(context);
        return prologFile != null;
    }
}
