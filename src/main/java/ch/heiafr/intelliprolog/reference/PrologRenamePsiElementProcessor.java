package ch.heiafr.intelliprolog.reference;

import ch.heiafr.intelliprolog.psi.PrologAtom;
import ch.heiafr.intelliprolog.psi.PrologCompoundName;
import ch.heiafr.intelliprolog.psi.PrologNamedElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PrologRenamePsiElementProcessor extends RenamePsiElementProcessor {

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return element instanceof PrologNamedElement; //If the element is a PrologNamedElement, we can process it
    }

    @Override
    public void prepareRenaming(@NotNull PsiElement element, @NotNull String newName, @NotNull Map<PsiElement, String> allRenames) {
        int arity = ReferenceHelper.getArityFromClicked(element);

        /*
            Two cases:
            1. The element is a PrologCompoundName or a PrologAtom
                => Same treatment : find every file where this predicate is used and add it to the renaming map

            2. The element is a PrologVariable
                => Search only in the current sentence => Variable renaming
         */

        //Case 1
        if (element instanceof PrologCompoundName || element instanceof PrologAtom) {

            Collection<PsiElement> files = ReferenceHelper.findAllRelatedFiles(element);
            files.forEach(file ->
                    PsiTreeUtil.collectElementsOfType(file, PrologNamedElement.class).stream()
                            .filter(e -> Objects.equals(e.getName(), element.getText()))
                            .filter(e -> ReferenceHelper.getArityFromClicked(e) == arity)
                            .forEach(e -> {
                                if (e instanceof PrologCompoundName) {
                                    allRenames.put(e.getParent(), newName);
                                } else {
                                    allRenames.put(e, newName);
                                }
                            }));
        } else {
            //Case 2
            PsiTreeUtil.findChildrenOfType(element.getContainingFile(), PrologNamedElement.class).stream()
                    .filter(e -> ReferenceHelper.areInSameSentence(element, e))
                    .filter(e -> Objects.equals(e.getName(), element.getText()))
                    .forEach(e -> allRenames.put(e, newName));
        }

    }

    @Override
    public void renameElement(@NotNull PsiElement element, @NotNull String newName, UsageInfo @NotNull [] usages, @Nullable RefactoringElementListener listener) throws IncorrectOperationException {
        if (element instanceof PrologNamedElement) { //Normally, this should always be true because of the canProcessElement method
            ((PrologNamedElement) element).setName(newName); //Set the new name
            if (listener != null) {
                listener.elementRenamed(element); //Notify the listener that the element has been renamed
            }
        }
    }
}
