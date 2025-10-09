package ch.heiafr.intelliprolog.reference;

import ch.heiafr.intelliprolog.psi.PrologNamedElement;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// BAP: I tried that when the renaming feature appeared broken. It works, but without the gadget showing the
// preview or letting choose to look into comments. For now I decide to keep the previous mechanism and
// comment the line in plugin.xml about "lang.refactoringSupport" extension.
// [Apparently running the initProject task somehow repaired the renaming feature]
public class PrologRefactoringSupportProvider extends RefactoringSupportProvider {
  @Override
  public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement elementToRename, @Nullable PsiElement context) {
    return (elementToRename instanceof PrologNamedElement);
  }

}
