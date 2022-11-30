package ch.heiafr.intelliprolog.psi.impl;

import ch.heiafr.intelliprolog.psi.PrologNamedElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrologNamedElementHelperImpl extends ASTWrapperPsiElement implements PrologNamedElement {

  public PrologNamedElementHelperImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public @Nullable PsiElement getNameIdentifier() {
    return null;
  }

  @Override
  public PsiElement setName(@NlsSafe @NotNull String name) throws IncorrectOperationException {
    return null;
  }
}
