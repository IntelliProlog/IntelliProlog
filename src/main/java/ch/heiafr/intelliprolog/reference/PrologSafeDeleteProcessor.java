package ch.heiafr.intelliprolog.reference;

import ch.heiafr.intelliprolog.PrologFileType;
import ch.heiafr.intelliprolog.psi.PrologFile;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.refactoring.safeDelete.NonCodeUsageSearchInfo;
import com.intellij.refactoring.safeDelete.SafeDeleteProcessorDelegateBase;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PrologSafeDeleteProcessor extends SafeDeleteProcessorDelegateBase {
    @Override
    public @Nullable Collection<? extends PsiElement> getElementsToSearch(@NotNull PsiElement element, @Nullable Module module, @NotNull Collection<PsiElement> allElementsToDelete) {

        //Find every .pl file in the project
        return FilenameIndex.getAllFilesByExt(element.getProject(), PrologFileType.INSTANCE.getDefaultExtension()).stream()
                .map(f -> PsiManager.getInstance(element.getProject()).findFile(f))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public boolean handlesElement(PsiElement element) {
        //Only Prolog Files for now
        return element instanceof PrologFile;
    }

    @Override
    public @Nullable NonCodeUsageSearchInfo findUsages(@NotNull PsiElement element, PsiElement @NotNull [] allElementsToDelete, @NotNull List<UsageInfo> result) {
        return null; //First action
    }

    @Override
    public @Nullable Collection<PsiElement> getAdditionalElementsToDelete(@NotNull PsiElement element, @NotNull Collection<PsiElement> allElementsToDelete, boolean askUser) {
        return null; //Second action
    }

    @Override
    public @Nullable Collection<@Nls String> findConflicts(@NotNull PsiElement element, PsiElement @NotNull [] allElementsToDelete) {
        return null; //Third action
    }

    @Override
    public UsageInfo @Nullable [] preprocessUsages(Project project, UsageInfo[] usages) {
        return new UsageInfo[0]; //Fourth action
    }

    @Override
    public void prepareForDeletion(PsiElement element) throws IncorrectOperationException {
        System.out.println("prepareForDeletion"); //Fifth action
    }

    @Override
    public boolean isToSearchInComments(PsiElement element) {
        return false;
    }

    @Override
    public void setToSearchInComments(PsiElement element, boolean enabled) {

    }

    @Override
    public boolean isToSearchForTextOccurrences(PsiElement element) {
        return false;
    }

    @Override
    public void setToSearchForTextOccurrences(PsiElement element, boolean enabled) {

    }
}
